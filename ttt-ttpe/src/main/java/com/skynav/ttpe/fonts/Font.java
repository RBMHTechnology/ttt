/*
 * Copyright 2014-15 Skynav, Inc. All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY SKYNAV, INC. AND ITS CONTRIBUTORS “AS IS” AND ANY
 * EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL SKYNAV, INC. OR ITS CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
 
package com.skynav.ttpe.fonts;

import java.util.Collection;

import com.skynav.ttpe.geometry.Axis;
import com.skynav.ttpe.geometry.Extent;
import com.skynav.ttpe.geometry.Rectangle;
import com.skynav.ttpe.geometry.TransformMatrix;
import com.skynav.ttv.util.Reporter;

public class Font {

    private FontCache cache;
    private FontKey key;
    private String source;
    private FontState ls;

    public Font(FontCache cache, FontKey key, String source, Reporter reporter) {
        this.cache = cache;
        this.key = key;
        this.source = source;
        this.ls = cache.getLoadedState(source, reporter);
    }

    @Override
    public String toString() {
        if (key != null)
            return key.toString();
        else
            return super.toString();
    }

    public FontKey getKey() {
        return key;
    }

    public FontSpecification getSpecification() {
        return key.getSpecification();
    }

    public String getSource() {
        return source;
    }

    public String getPreferredFamilyName() {
        return ls.getPreferredFamilyName(key);
    }

    public FontStyle getStyle() {
        return key.style;
    }

    public FontWeight getWeight() {
        return key.weight;
    }

    public Collection<FontFeature> getFeatures() {
        return key.getFeatures();
    }

    public FontFeature getFeature(String feature) {
        return key.getFeature(feature);
    }

    public boolean isKerningEnabled() {
        return key.isKerningEnabled();
    }

    public boolean isSheared() {
        return key.isSheared();
    }

    public double getShear() {
        return key.getShear();
    }

    public Axis getAxis() {
        return key.axis;
    }

    public Extent getSize() {
        return key.size;
    }

    public double getSize(Axis axis) {
        return key.size.getDimension(axis);
    }

    public double getWidth() {
        return getSize(Axis.HORIZONTAL);
    }

    public double getHeight() {
        return getSize(Axis.VERTICAL);
    }

    public boolean isAnamorphic() {
        return getSize(Axis.HORIZONTAL) != getSize(Axis.VERTICAL);
    }

    public Double getDefaultLineHeight() {
        return key.size.getDimension(key.axis) * 1.25;
    }

    public double getLeading() {
        return ls.getLeading(key);
    }

    public double getAscent() {
        return ls.getAscent(key);
    }

    public double getDescent() {
        return ls.getDescent(key);
    }

    public double getAdvance(String text) {
        return ls.getAdvance(key, text);
    }

    public double getAdvance(String text, boolean adjustForKerning) {
        return ls.getAdvance(key, text, adjustForKerning);
    }

    public double[] getAdvances(String text) {
        return ls.getAdvances(key, text);
    }

    public double getKerningAdvance(String text) {
        return ls.getKerningAdvance(key, text);
    }

    public double[] getKerning(String text) {
        return ls.getKerning(key, text);
    }

    public TransformMatrix getTransform() {
        TransformMatrix t = TransformMatrix.IDENTITY;
        if (isSheared())
            t = applyShear(t, getShear());
        if (isAnamorphic())
            t = applyAnamorphic(t, getSize());
        return !t.isIdentity() ? t : null;
    }

    private TransformMatrix applyShear(TransformMatrix t0, double shear) {
        TransformMatrix t = (TransformMatrix) t0.clone();
        double sx = -Math.tan(Math.toRadians(shear * 90));
        double sy = 0;
        t.shear(sx, sy);
        return t;
    }

    private TransformMatrix applyAnamorphic(TransformMatrix t0, Extent size) {
        TransformMatrix t = (TransformMatrix) t0.clone();
        double sx = size.getDimension(Axis.HORIZONTAL) / size.getDimension(Axis.VERTICAL);
        double sy = 1;
        t.scale(sx, sy);
        return t;
    }

    public Rectangle[] getGlyphBounds(String text) {
        return ls.getGlyphBounds(key, text);
    }

    public int[] getGlyphs(String text) {
        return ls.getGlyphs(text);
    }

    public Font getScaledFont(double scale) {
        return cache.getScaledFont(this, scale);
    }

}