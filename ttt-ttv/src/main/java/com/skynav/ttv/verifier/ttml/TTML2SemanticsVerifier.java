/*
 * Copyright 2015 Skynav, Inc. All rights reserved.
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

package com.skynav.ttv.verifier.ttml;

import java.io.Serializable;
import java.util.Collection;

import javax.xml.bind.JAXBElement;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.skynav.ttv.model.Model;
import com.skynav.ttv.model.ttml2.tt.Body;
import com.skynav.ttv.model.ttml2.tt.Break;
import com.skynav.ttv.model.ttml2.tt.Division;
import com.skynav.ttv.model.ttml2.tt.Head;
import com.skynav.ttv.model.ttml2.tt.Initial;
import com.skynav.ttv.model.ttml2.tt.Layout;
import com.skynav.ttv.model.ttml2.tt.Metadata;
import com.skynav.ttv.model.ttml2.tt.Paragraph;
import com.skynav.ttv.model.ttml2.tt.Region;
import com.skynav.ttv.model.ttml2.tt.Set;
import com.skynav.ttv.model.ttml2.tt.Span;
import com.skynav.ttv.model.ttml2.tt.Styling;
import com.skynav.ttv.model.ttml2.tt.TimedText;
import com.skynav.ttv.model.ttml2.ttm.Actor;
import com.skynav.ttv.model.ttml2.ttm.Agent;
import com.skynav.ttv.model.ttml2.ttm.Copyright;
import com.skynav.ttv.model.ttml2.ttm.Description;
import com.skynav.ttv.model.ttml2.ttm.Item;
import com.skynav.ttv.model.ttml2.ttm.Name;
import com.skynav.ttv.model.ttml2.ttm.Title;
import com.skynav.ttv.model.ttml2.ttp.Extensions;
import com.skynav.ttv.model.ttml2.ttp.Features;
import com.skynav.ttv.model.ttml2.ttp.Profile;

public class TTML2SemanticsVerifier extends TTML1SemanticsVerifier {

    public TTML2SemanticsVerifier(Model model) {
        super(model);
    }

    // verifier overrides

    @Override
    protected boolean verifyRoot(Object root) {
        if (root instanceof TimedText)
            return verifyTimedText(root);
        else if (root instanceof Profile)
            return verifyProfile(root);
        else
            return unexpectedContent(root);
    }

    @Override
    protected boolean verifyStyling(Object styling) {
        boolean failed = false;
        if (super.verifyStyling(styling)) {
            for (Object i : getStylingInitials(styling)) {
                if (i instanceof Initial) {
                    Initial initial = (Initial) i;
                    if (verifyInitial(initial))
                        addInitialOverrides(initial);
                    else
                        failed = true;
                }
            }
        } else
            failed = true;
        return !failed;
    }

    protected Collection<? extends Object> getStylingInitials(Object styling) {
        assert styling instanceof Styling;
        return ((Styling) styling).getInitial();
    }

    protected boolean verifyInitial(Initial initial) {
        boolean failed = false;
        if (!verifyStyleAttributes(initial))
            failed = true;
        if (!verifyOtherAttributes(initial))
            failed = true;
        return !failed;
    }

    private void addInitialOverrides(Object initial) {
        this.styleVerifier.addInitialOverrides(initial, getContext());
    }

    @Override
    protected boolean verifyMetadataItem(Object metadata) {
        if (metadata instanceof JAXBElement<?>)
            return verifyMetadata(((JAXBElement<?>)metadata).getValue());
        else if (metadata instanceof Actor)
            return verifyActor(metadata);
        else if (metadata instanceof Agent)
            return verifyAgent(metadata);
        else if (metadata instanceof Copyright)
            return verifyCopyright(metadata);
        else if (metadata instanceof Description)
            return verifyDescription(metadata);
        else if (metadata instanceof Item)
            return verifyItem(metadata);
        else if (metadata instanceof Metadata)
            return verifyMetadata(metadata);
        else if (metadata instanceof Name)
            return verifyName(metadata);
        else if (metadata instanceof Title)
            return verifyTitle(metadata);
        else if (metadata instanceof Element)
            return verifyForeignMetadata((Element)metadata);
        else
            return unexpectedContent(metadata);
    }

    @Override
    protected boolean verifyAnimation(Object animation) {
        if (animation instanceof Set)
            return verifySet(animation);
        else
            return unexpectedContent(animation);
    }

    @Override
    protected boolean verifyBlock(Object block) {
        if (block instanceof Division)
            return verifyDivision(block);
        else if (block instanceof Paragraph)
            return verifyParagraph(block);
        else
            return unexpectedContent(block);
    }

    @Override
    protected boolean verifyContent(Serializable content) {
        if (content instanceof JAXBElement<?>) {
            Object element = ((JAXBElement<?>)content).getValue();
            if (isMetadataItem(element))
                return verifyMetadata(element);
            else if (element instanceof Set)
                return verifySet(element);
            else if (element instanceof Span)
                return verifySpan(element);
            else if (element instanceof Break)
                return verifyBreak(element);
            else
                return unexpectedContent(element);
        } else if (content instanceof String) {
            return true;
        } else
            return unexpectedContent(content);
    }

    // metadata overrides

    @Override
    protected boolean isMetadataItem(Object element) {
        if (element instanceof Agent)
            return true;
        else if (element instanceof Copyright)
            return true;
        else if (element instanceof Description)
            return true;
        else if (element instanceof Metadata)
            return true;
        else if (element instanceof Title)
            return true;
        else
            return false;
    }

    @Override
    protected Object findMetadataItemBindingElement(Object metadata, Node node) {
        if (metadata instanceof JAXBElement<?>)
            return findMetadataItemBindingElement(((JAXBElement<?>)metadata).getValue(), node);
        else if (getContext().getXMLNode(metadata) == node)
            return metadata;
        else if (metadata instanceof Actor)
            return findActorBindingElement(metadata, node);
        else if (metadata instanceof Agent)
            return findAgentBindingElement(metadata, node);
        else if (metadata instanceof Copyright)
            return findCopyrightBindingElement(metadata, node);
        else if (metadata instanceof Description)
            return findDescriptionBindingElement(metadata, node);
        else if (metadata instanceof Metadata)
            return findMetadataBindingElement(metadata, node);
        else if (metadata instanceof Name)
            return findNameBindingElement(metadata, node);
        else if (metadata instanceof Title)
            return findTitleBindingElement(metadata, node);
        else if (metadata instanceof Element)
            return findForeignMetadataBindingElement((Element)metadata, node);
        else
            return null;
    }

    @Override
    protected Object findAnimationBindingElement(Object animation, Node node) {
        if (animation instanceof Set)
            return findSetBindingElement(animation, node);
        else
            return null;
    }

    // accessor overrides

    @Override
    protected Object getTimedTextHead(Object tt) {
        assert tt instanceof TimedText;
        return ((TimedText) tt).getHead();
    }

    @Override
    protected Object getTimedTextBody(Object tt) {
        assert tt instanceof TimedText;
        return ((TimedText) tt).getBody();
    }

    @Override
    protected Collection<? extends Object> getHeadMetadata(Object head) {
        assert head instanceof Head;
        return ((Head) head).getMetadataClass();
    }

    @Override
    protected Collection<? extends Object> getHeadParameters(Object head) {
        assert head instanceof Head;
        return ((Head) head).getParametersClass();
    }

    @Override
    protected Object getHeadStyling(Object head) {
        assert head instanceof Head;
        return ((Head) head).getStyling();
    }

    @Override
    protected Object getHeadLayout(Object head) {
        assert head instanceof Head;
        return ((Head) head).getLayout();
    }

    @Override
    protected Collection<? extends Object> getStylingMetadata(Object styling) {
        assert styling instanceof Styling;
        return ((Styling) styling).getMetadataClass();
    }

    @Override
    protected Collection<? extends Object> getStylingStyles(Object styling) {
        assert styling instanceof Styling;
        return ((Styling) styling).getStyle();
    }

    @Override
    protected Collection<? extends Object> getLayoutMetadata(Object layout) {
        assert layout instanceof Layout;
        return ((Layout) layout).getMetadataClass();
    }

    @Override
    protected Collection<? extends Object> getLayoutRegions(Object layout) {
        assert layout instanceof Layout;
        return ((Layout) layout).getRegion();
    }

    @Override
    protected Collection<? extends Object> getRegionMetadata(Object region) {
        assert region instanceof Region;
        return ((Region) region).getMetadataClass();
    }

    @Override
    protected Collection<? extends Object> getRegionAnimations(Object region) {
        assert region instanceof Region;
        return ((Region) region).getAnimationClass();
    }

    @Override
    protected Collection<? extends Object> getRegionStyles(Object region) {
        assert region instanceof Region;
        return ((Region) region).getStyle();
    }

    @Override
    protected Collection<? extends Object> getBodyMetadata(Object body) {
        assert body instanceof Body;
        return ((Body) body).getMetadataClass();
    }

    @Override
    protected Collection<? extends Object> getBodyAnimations(Object body) {
        assert body instanceof Body;
        return ((Body) body).getAnimationClass();
    }

    @Override
    protected Collection<? extends Object> getBodyDivisions(Object body) {
        assert body instanceof Body;
        return ((Body) body).getDiv();
    }

    @Override
    protected Collection<? extends Object> getDivisionMetadata(Object division) {
        assert division instanceof Division;
        return ((Division) division).getMetadataClass();
    }

    @Override
    protected Collection<? extends Object> getDivisionAnimations(Object division) {
        assert division instanceof Division;
        return ((Division) division).getAnimationClass();
    }

    @Override
    protected Collection<? extends Object> getDivisionBlocks(Object division) {
        assert division instanceof Division;
        return ((Division) division).getBlockOrEmbeddedClass();
    }

    @Override
    protected Collection<Serializable> getParagraphContent(Object paragraph) {
        assert paragraph instanceof Paragraph;
        return ((Paragraph) paragraph).getContent();
    }

    @Override
    protected Collection<Serializable> getSpanContent(Object span) {
        assert span instanceof Span;
        return ((Span) span).getContent();
    }

    @Override
    protected Collection<? extends Object> getBreakMetadata(Object br) {
        assert br instanceof Break;
        return ((Break) br).getMetadataClass();
    }

    @Override
    protected Collection<? extends Object> getBreakAnimations(Object br) {
        assert br instanceof Break;
        return ((Break) br).getAnimationClass();
    }

    @Override
    protected Collection<? extends Object> getSetMetadata(Object set) {
        assert set instanceof Set;
        return ((Set) set).getMetadataClass();
    }

    @Override
    protected Collection<? extends Object> getProfileMetadata(Object profile) {
        assert profile instanceof Profile;
        return ((Profile) profile).getMetadataClass();
    }

    @Override
    protected Collection<? extends Object> getProfileFeatures(Object profile) {
        assert profile instanceof Profile;
        return ((Profile) profile).getFeatures();
    }

    @Override
    protected Collection<? extends Object> getProfileExtensions(Object profile) {
        assert profile instanceof Profile;
        return ((Profile) profile).getExtensions();
    }

    @Override
    protected Collection<? extends Object> getFeaturesMetadata(Object features) {
        assert features instanceof Features;
        return ((Features) features).getMetadataClass();
    }

    @Override
    protected Collection<? extends Object> getFeaturesFeatures(Object features) {
        assert features instanceof Features;
        return ((Features) features).getFeature();
    }

    @Override
    protected Collection<? extends Object> getExtensionsMetadata(Object extensions) {
        assert extensions instanceof Extensions;
        return ((Extensions) extensions).getMetadataClass();
    }

    @Override
    protected Collection<? extends Object> getExtensionsExtensions(Object extensions) {
        assert extensions instanceof Extensions;
        return ((Extensions) extensions).getExtension();
    }

    @Override
    protected Collection<? extends Object> getAgentNames(Object agent) {
        assert agent instanceof Agent;
        return ((Agent) agent).getName();
    }

    @Override
    protected Object getAgentActor(Object agent) {
        assert agent instanceof Agent;
        return ((Agent) agent).getActor();
    }

    @Override
    protected Collection<? extends Object> getMetadataAny(Object metadata) {
        assert metadata instanceof Metadata;
        return ((Metadata) metadata).getAny();
    }

    // new elements

    protected boolean verifyItem(Object item) {
        boolean failed = false;
        if (!verifyOtherAttributes(item))
            failed = true;
        if (!verifyMetadataItem(metadataVerifier, item))
            failed = true;
        return !failed;
    }

}
