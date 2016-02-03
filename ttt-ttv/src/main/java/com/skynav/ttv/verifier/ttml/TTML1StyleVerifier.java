/*
 * Copyright 2013 Skynav, Inc. All rights reserved.
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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

import org.w3c.dom.Attr;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import org.xml.sax.Locator;

import com.skynav.ttv.model.Model;
import com.skynav.ttv.model.ttml1.tt.Body;
import com.skynav.ttv.model.ttml1.tt.Break;
import com.skynav.ttv.model.ttml1.tt.Division;
import com.skynav.ttv.model.ttml1.tt.Paragraph;
import com.skynav.ttv.model.ttml1.tt.Region;
import com.skynav.ttv.model.ttml1.tt.Set;
import com.skynav.ttv.model.ttml1.tt.Span;
import com.skynav.ttv.model.ttml1.tt.Style;
import com.skynav.ttv.model.ttml1.tt.TimedText;
import com.skynav.ttv.model.ttml1.ttd.Direction;
import com.skynav.ttv.model.ttml1.ttd.Display;
import com.skynav.ttv.model.ttml1.ttd.DisplayAlign;
import com.skynav.ttv.model.ttml1.ttd.FontStyle;
import com.skynav.ttv.model.ttml1.ttd.FontWeight;
import com.skynav.ttv.model.ttml1.ttd.Overflow;
import com.skynav.ttv.model.ttml1.ttd.ShowBackground;
import com.skynav.ttv.model.ttml1.ttd.TextAlign;
import com.skynav.ttv.model.ttml1.ttd.TextDecoration;
import com.skynav.ttv.model.ttml1.ttd.UnicodeBidi;
import com.skynav.ttv.model.ttml1.ttd.Visibility;
import com.skynav.ttv.model.ttml1.ttd.WrapOption;
import com.skynav.ttv.model.ttml1.ttd.WritingMode;
import com.skynav.ttv.model.value.Length;
import com.skynav.ttv.util.Enums;
import com.skynav.ttv.util.Location;
import com.skynav.ttv.util.Reporter;
import com.skynav.ttv.verifier.StyleValueVerifier;
import com.skynav.ttv.verifier.StyleVerifier;
import com.skynav.ttv.verifier.VerifierContext;
import com.skynav.ttv.verifier.ttml.style.BackgroundColorVerifier;
import com.skynav.ttv.verifier.ttml.style.ColorVerifier;
import com.skynav.ttv.verifier.ttml.style.DirectionVerifier;
import com.skynav.ttv.verifier.ttml.style.DisplayAlignVerifier;
import com.skynav.ttv.verifier.ttml.style.DisplayVerifier;
import com.skynav.ttv.verifier.ttml.style.ExtentVerifier;
import com.skynav.ttv.verifier.ttml.style.FontFamilyVerifier;
import com.skynav.ttv.verifier.ttml.style.FontSizeVerifier;
import com.skynav.ttv.verifier.ttml.style.FontStyleVerifier;
import com.skynav.ttv.verifier.ttml.style.FontWeightVerifier;
import com.skynav.ttv.verifier.ttml.style.LineHeightVerifier;
import com.skynav.ttv.verifier.ttml.style.OpacityVerifier;
import com.skynav.ttv.verifier.ttml.style.OriginVerifier;
import com.skynav.ttv.verifier.ttml.style.OverflowVerifier;
import com.skynav.ttv.verifier.ttml.style.PaddingVerifier;
import com.skynav.ttv.verifier.ttml.style.RegionAttributeVerifier;
import com.skynav.ttv.verifier.ttml.style.ShowBackgroundVerifier;
import com.skynav.ttv.verifier.ttml.style.StyleAttributeVerifier;
import com.skynav.ttv.verifier.ttml.style.TextAlignVerifier;
import com.skynav.ttv.verifier.ttml.style.TextDecorationVerifier;
import com.skynav.ttv.verifier.ttml.style.TextOutlineVerifier;
import com.skynav.ttv.verifier.ttml.style.UnicodeBidiVerifier;
import com.skynav.ttv.verifier.ttml.style.VisibilityVerifier;
import com.skynav.ttv.verifier.ttml.style.WrapOptionVerifier;
import com.skynav.ttv.verifier.ttml.style.WritingModeVerifier;
import com.skynav.ttv.verifier.ttml.style.ZIndexVerifier;
import com.skynav.ttv.verifier.util.IdReferences;
import com.skynav.ttv.verifier.util.Strings;

import static com.skynav.ttv.model.ttml.TTML1.Constants.*;
import java.util.ArrayList;
import java.util.Arrays;

public class TTML1StyleVerifier implements StyleVerifier {

    public static final String NAMESPACE = NAMESPACE_TT_STYLE;

    public static final int APPLIES_TO_TT                       = 0x00000001;
    public static final int APPLIES_TO_BODY                     = 0x00000002;
    public static final int APPLIES_TO_DIV                      = 0x00000004;
    public static final int APPLIES_TO_P                        = 0x00000008;
    public static final int APPLIES_TO_SPAN                     = 0x00000010;
    public static final int APPLIES_TO_BR                       = 0x00000020;
    public static final int APPLIES_TO_CONTENT                  = (APPLIES_TO_BODY|APPLIES_TO_DIV|APPLIES_TO_P|APPLIES_TO_SPAN|APPLIES_TO_BR);
    public static final int APPLIES_TO_REGION                   = 0x00010000;

    public static final QName backgroundColorAttributeName      = new QName(NAMESPACE,"backgroundColor");
    public static final QName colorAttributeName                = new QName(NAMESPACE,"color");
    public static final QName directionAttributeName            = new QName(NAMESPACE,"direction");
    public static final QName displayAttributeName              = new QName(NAMESPACE,"display");
    public static final QName displayAlignAttributeName         = new QName(NAMESPACE,"displayAlign");
    public static final QName extentAttributeName               = new QName(NAMESPACE,"extent");
    public static final QName fontFamilyAttributeName           = new QName(NAMESPACE,"fontFamily");
    public static final QName fontSizeAttributeName             = new QName(NAMESPACE,"fontSize");
    public static final QName fontStyleAttributeName            = new QName(NAMESPACE,"fontStyle");
    public static final QName fontWeightAttributeName           = new QName(NAMESPACE,"fontWeight");
    public static final QName lineHeightAttributeName           = new QName(NAMESPACE,"lineHeight");
    public static final QName opacityAttributeName              = new QName(NAMESPACE,"opacity");
    public static final QName originAttributeName               = new QName(NAMESPACE,"origin");
    public static final QName overflowAttributeName             = new QName(NAMESPACE,"overflow");
    public static final QName paddingAttributeName              = new QName(NAMESPACE,"padding");
    public static final QName regionAttributeName               = new QName("", "region");
    public static final QName showBackgroundAttributeName       = new QName(NAMESPACE,"showBackground");
    public static final QName styleAttributeName                = new QName("", "style");
    public static final QName textAlignAttributeName            = new QName(NAMESPACE,"textAlign");

    public static final QName textDecorationAttributeName       = new QName(NAMESPACE,"textDecoration");
    public static final QName textOutlineAttributeName          = new QName(NAMESPACE,"textOutline");
    public static final QName unicodeBidiAttributeName          = new QName(NAMESPACE,"unicodeBidi");
    public static final QName visibilityAttributeName           = new QName(NAMESPACE,"visibility");
    public static final QName wrapOptionAttributeName           = new QName(NAMESPACE,"wrapOption");
    public static final QName writingModeAttributeName          = new QName(NAMESPACE,"writingMode");
    public static final QName zIndexAttributeName               = new QName(NAMESPACE,"zIndex");

    protected static final List<Object[]> styleAccessorMap      = new ArrayList<>(Arrays.asList(new Object[][] {
      {
            backgroundColorAttributeName,                       // attribute name
            "BackgroundColor",                                  // accessor method name suffix
            String.class,                                       // value type
            BackgroundColorVerifier.class,                      // specialized verifier
            Integer.valueOf(APPLIES_TO_CONTENT|APPLIES_TO_REGION), // applicability
            Boolean.FALSE,                                      // padding permitted
            Boolean.FALSE,                                      // inheritable
            "transparent",                                      // initial value (as object suitable for setter)
            null,                                               // initial value as string (or null if same as previous)
        },
        {
            colorAttributeName,
            "Color",
            String.class,
            ColorVerifier.class,
            Integer.valueOf(APPLIES_TO_SPAN),
            Boolean.FALSE,
            Boolean.TRUE,
            "white",
            null,
        },
        {
            directionAttributeName,
            "Direction",
            Direction.class,
            DirectionVerifier.class,
            Integer.valueOf(APPLIES_TO_P|APPLIES_TO_SPAN),
            Boolean.FALSE,
            Boolean.TRUE,
            Direction.LTR,
            Direction.LTR.value(),
        },
        {
            displayAttributeName,
            "Display",
            Display.class,
            DisplayVerifier.class,
            Integer.valueOf(APPLIES_TO_CONTENT|APPLIES_TO_REGION),
            Boolean.FALSE,
            Boolean.FALSE,
            Display.AUTO,
            Display.AUTO.value(),
        },
        {
            displayAlignAttributeName,
            "DisplayAlign",
            DisplayAlign.class,
            DisplayAlignVerifier.class,
            Integer.valueOf(APPLIES_TO_REGION),
            Boolean.FALSE,
            Boolean.FALSE,
            DisplayAlign.BEFORE,
            DisplayAlign.BEFORE.value(),
        },
        {
            extentAttributeName,
            "Extent",
            String.class,
            ExtentVerifier.class,
            Integer.valueOf(APPLIES_TO_TT|APPLIES_TO_REGION),
            Boolean.FALSE,
            Boolean.FALSE,
            "auto",
            null,
        },
        {
            fontFamilyAttributeName,
            "FontFamily",
            String.class,
            FontFamilyVerifier.class,
            Integer.valueOf(APPLIES_TO_P|APPLIES_TO_SPAN),
            Boolean.TRUE,
            Boolean.TRUE,
            "default",
            null,
        },
        {
            fontSizeAttributeName,
            "FontSize",
            String.class,
            FontSizeVerifier.class,
            Integer.valueOf(APPLIES_TO_P|APPLIES_TO_SPAN),
            Boolean.FALSE,
            Boolean.TRUE,
            "1c",
            null,
        },
        {
            fontStyleAttributeName,
            "FontStyle",
            FontStyle.class,
            FontStyleVerifier.class,
            Integer.valueOf(APPLIES_TO_P|APPLIES_TO_SPAN),
            Boolean.FALSE,
            Boolean.TRUE,
            FontStyle.NORMAL,
            FontStyle.NORMAL.value(),
        },
        {
            fontWeightAttributeName,
            "FontWeight",
            FontWeight.class,
            FontWeightVerifier.class,
            Integer.valueOf(APPLIES_TO_P|APPLIES_TO_SPAN),
            Boolean.FALSE,
            Boolean.TRUE,
            FontWeight.NORMAL,
            FontWeight.NORMAL.value(),
        },
        {
            lineHeightAttributeName,
            "LineHeight",
            String.class,
            LineHeightVerifier.class,
            Integer.valueOf(APPLIES_TO_P),
            Boolean.FALSE,
            Boolean.TRUE,
            "normal",
            null,
        },
        {
            opacityAttributeName,
            "Opacity",
            Float.class,
            OpacityVerifier.class,
            Integer.valueOf(APPLIES_TO_REGION),
            Boolean.FALSE,
            Boolean.FALSE,
            Float.valueOf(1.0F),
            "1.0",
        },
        {
            originAttributeName,
            "Origin",
            String.class,
            OriginVerifier.class,
            Integer.valueOf(APPLIES_TO_REGION),
            Boolean.FALSE,
            Boolean.FALSE,
            "auto",
            null
        },
        {
            overflowAttributeName,
            "Overflow",
            Overflow.class,
            OverflowVerifier.class,
            Integer.valueOf(APPLIES_TO_REGION),
            Boolean.FALSE,
            Boolean.FALSE,
            Overflow.HIDDEN,
            Overflow.HIDDEN.value(),
        },
        {
            paddingAttributeName,
            "Padding",
            String.class,
            PaddingVerifier.class,
            Integer.valueOf(APPLIES_TO_REGION),
            Boolean.FALSE,
            Boolean.FALSE,
            "0px",
            null,
        },
        {
            regionAttributeName,
            "Region",
            Object.class,
            RegionAttributeVerifier.class,
            Integer.valueOf(APPLIES_TO_CONTENT),
            Boolean.FALSE,
            Boolean.FALSE,
            null,
            null,
        },
        {
            showBackgroundAttributeName,
            "ShowBackground",
            ShowBackground.class,
            ShowBackgroundVerifier.class,
            Integer.valueOf(APPLIES_TO_REGION),
            Boolean.FALSE,
            Boolean.FALSE,
            ShowBackground.ALWAYS,
            ShowBackground.ALWAYS.value(),
        },
        {
            styleAttributeName,
            "StyleAttribute",
            List.class,
            StyleAttributeVerifier.class,
            Integer.valueOf(APPLIES_TO_CONTENT),
            Boolean.FALSE,
            Boolean.FALSE,
            null,
            null,
        },
        {
            textAlignAttributeName,
            "TextAlign",
            TextAlign.class,
            TextAlignVerifier.class,
            Integer.valueOf(APPLIES_TO_P),
            Boolean.FALSE,
            Boolean.TRUE,
            TextAlign.START,
            TextAlign.START.value(),
        },
        {
            textDecorationAttributeName,
            "TextDecoration",
            TextDecoration.class,
            TextDecorationVerifier.class,
            Integer.valueOf(APPLIES_TO_SPAN),
            Boolean.FALSE,
            Boolean.TRUE,
            TextDecoration.NONE,
            TextDecoration.NONE.value(),
        },
        {
            textOutlineAttributeName,
            "TextOutline",
            String.class,
            TextOutlineVerifier.class,
            Integer.valueOf(APPLIES_TO_SPAN),
            Boolean.FALSE,
            Boolean.TRUE,
            "none",
            null,
        },
        {
            unicodeBidiAttributeName,
            "UnicodeBidi",
            UnicodeBidi.class,
            UnicodeBidiVerifier.class,
            Integer.valueOf(APPLIES_TO_P|APPLIES_TO_SPAN),
            Boolean.FALSE,
            Boolean.FALSE,
            UnicodeBidi.NORMAL,
            UnicodeBidi.NORMAL.value(),
        },
        {
            visibilityAttributeName,
            "Visibility",
            Visibility.class,
            VisibilityVerifier.class,
            Integer.valueOf(APPLIES_TO_CONTENT|APPLIES_TO_REGION),
            Boolean.FALSE,
            Boolean.TRUE,
            Visibility.VISIBLE,
            Visibility.VISIBLE.value(),
        },
        {
            wrapOptionAttributeName,
            "WrapOption",
            WrapOption.class,
            WrapOptionVerifier.class,
            Integer.valueOf(APPLIES_TO_SPAN),
            Boolean.FALSE,
            Boolean.TRUE,
            WrapOption.WRAP,
            WrapOption.WRAP.value(),
        },
        {
            writingModeAttributeName,
            "WritingMode",
            WritingMode.class,
            WritingModeVerifier.class,
            Integer.valueOf(APPLIES_TO_REGION),
            Boolean.FALSE,
            Boolean.FALSE,
            WritingMode.LRTB,
            WritingMode.LRTB.value(),
        },
        {
            zIndexAttributeName,
            "ZIndex",
            String.class,
            ZIndexVerifier.class,
            Integer.valueOf(APPLIES_TO_REGION),
            Boolean.FALSE,
            Boolean.FALSE,
            "auto",
            null,
        },
    }));

    protected Model model;
    protected Map<QName, StyleAccessor> accessors;

    public TTML1StyleVerifier(Model model) {
        populate(model);
    }

    public Model getModel() {
        return model;
    }

    public QName getStyleAttributeName(String propertyName) {
        // assumes that property name is same as local part of qualified attribute name, which
        // is presently true in TTML1
        for (QName name : accessors.keySet()) {
            if (propertyName.equals(name.getLocalPart()))
                return name;
        }
        return null;
    }

    public Collection<QName> getDefinedStyleNames() {
        return accessors.keySet();
    }

    public Collection<QName> getApplicableStyleNames(QName eltName) {
        Collection<QName> names = new java.util.ArrayList<QName>();
        for (Map.Entry<QName, StyleAccessor> e : accessors.entrySet()) {
            if (e.getValue().doesStyleApply(eltName))
                names.add(e.getKey());
        }
        return names;
    }

    public boolean isInheritableStyle(QName eltName, QName styleName) {
        if (accessors.containsKey(styleName))
            return accessors.get(styleName).isInheritable();
        else
            return false;
    }

    public String getInitialStyleValue(QName eltName, QName styleName) {
        if (accessors.containsKey(styleName))
            return accessors.get(styleName).initialValueAsString();
        else
            return null;
    }

    public boolean doesStyleApply(QName eltName, QName styleName) {
        if (accessors.containsKey(styleName)) {
            return accessors.get(styleName).doesStyleApply(eltName);
        } else
            return false;
    }

    public boolean isNegativeLengthPermitted(QName eltName, QName styleName) {
        return true;
    }

    public boolean isLengthUnitsPermitted(QName eltName, QName styleName, Length.Unit units) {
        return true;
    }

    public boolean verify(Object content, Locator locator, VerifierContext context, ItemType type) {
        if (type == ItemType.Attributes)
            return verifyAttributeItems(content, locator, context);
        else if (type == ItemType.Element)
            return verifyElementItem(content, locator, context);
        else if (type == ItemType.Other)
            return verifyOtherAttributes(content, locator, context);
        else
            throw new IllegalArgumentException();
    }

    public void addInitialOverrides(Object initial, VerifierContext context) {
    }

    public Object getInitialOverride(QName styleName, VerifierContext context) {
        return null;
    }

    protected boolean verifyAttributeItems(Object content, Locator locator, VerifierContext context) {
        boolean failed = false;
        for (StyleAccessor sa : accessors.values()) {
            if (!verifyAttributeItem(content, locator, sa, context))
                failed = true;
        }
        return !failed;
    }

    protected boolean verifyAttributeItem(Object content, Locator locator, StyleAccessor sa, VerifierContext context) {
        return sa.verify(model, content, locator, context);
    }

    protected boolean verifyElementItem(Object content, Locator locator, VerifierContext context) {
        boolean failed = false;
        if (isSet(content))
            failed = !verifySet(content, locator, context);
        if (failed) {
            Reporter reporter = context.getReporter();
            reporter.logError(reporter.message(locator, "*KEY*", "Invalid ''{0}'' styled item.", context.getBindingElementName(content)));
        }
        return !failed;
    }

    protected boolean verifySet(Object content, Locator locator, VerifierContext context) {
        boolean failed = false;
        int numStyleAttributes = 0;
        NamedNodeMap attributes = context.getXMLNode(content).getAttributes();
        for (int i = 0, n = attributes.getLength(); i < n; ++i) {
            Node attribute = attributes.item(i);
            String nsUri = attribute.getNamespaceURI();
            if ((nsUri != null) && nsUri.equals(NAMESPACE))
                ++numStyleAttributes;
        }
        if (numStyleAttributes > 1) {
            Reporter reporter = context.getReporter();
            reporter.logInfo(reporter.message(locator, "*KEY*", "Style attribute count exceeds maximum, got {0}, expected no more than 1.", numStyleAttributes));
            failed = true;
        }
        return !failed;
    }

    protected boolean verifyOtherAttributes(Object content, Locator locator, VerifierContext context) {
        boolean failed = false;
        NamedNodeMap attributes = context.getXMLNode(content).getAttributes();
        for (int i = 0, n = attributes.getLength(); i < n; ++i) {
            Node item = attributes.item(i);
            if (!(item instanceof Attr))
                continue;
            Attr attribute = (Attr) item;
            String nsUri = attribute.getNamespaceURI();
            String localName = attribute.getLocalName();
            if (localName == null)
                localName = attribute.getName();
            if (localName.indexOf("xmlns") == 0)
                continue;
            QName name = new QName(nsUri != null ? nsUri : "", localName);
            if (name.getNamespaceURI().equals(NAMESPACE)) {
                Reporter reporter = context.getReporter();
                if ((content instanceof TimedText) && name.equals(extentAttributeName))
                    continue;
                else if (!isStyleAttribute(name)) {
                    reporter.logError(reporter.message(locator, "*KEY*", "Unknown attribute in TT Style namespace ''{0}'' not permitted on ''{1}''.",
                        name, context.getBindingElementName(content)));
                    failed = true;
                } else if (!permitsStyleAttribute(content, name)) {
                    reporter.logError(reporter.message(locator, "*KEY*", "TT Style attribute ''{0}'' not permitted on ''{1}''.",
                        name, context.getBindingElementName(content)));
                    failed = true;
                }
            }
        }
        return !failed;
    }

    protected boolean permitsStyleAttribute(Object content, QName name) {
        if (content instanceof Body)
            return true;
        else if (content instanceof Division)
            return true;
        else if (content instanceof Paragraph)
            return true;
        else if (content instanceof Span)
            return true;
        else if (content instanceof Break)
            return true;
        else if (content instanceof Style)
            return true;
        else if (content instanceof Region)
            return true;
        else if (content instanceof Set)
            return true;
        else
            return false;
    }

    protected boolean isInitial(Object content) {
        return false;
    }

    protected boolean isRegion(Object content) {
        return content instanceof Region;
    }

    protected boolean isSet(Object content) {
        return content instanceof Set;
    }

    protected boolean isTimedText(Object content) {
        return content instanceof TimedText;
    }

    protected boolean isStyleAttribute(QName name) {
        return name.getNamespaceURI().equals(NAMESPACE) && accessors.containsKey(name);
    }

    private void populate(Model model) {
        this.model = model;
        this.accessors = makeAccessors();
    }

    private Map<QName, StyleAccessor> makeAccessors() {
        Map<QName, StyleAccessor> accessors = new java.util.HashMap<QName, StyleAccessor>();
        populateAccessors(accessors);
        return accessors;
    }

    protected void populateAccessors(Map<QName, StyleAccessor> accessors) {
        populateAccessors(accessors, styleAccessorMap);
    }

    protected void populateAccessors(Map<QName, StyleAccessor> accessors, List<Object[]> accessorMap) {
        for (Object[] styleAccessorEntry : accessorMap) {
            assert styleAccessorEntry.length >= 9;
            QName styleName = (QName) styleAccessorEntry[0];
            String accessorName = (String) styleAccessorEntry[1];
            Class<?> valueClass = (Class<?>) styleAccessorEntry[2];
            Class<?> verifierClass = (Class<?>) styleAccessorEntry[3];
            int applicability = ((Integer) styleAccessorEntry[4]).intValue();
            boolean paddingPermitted = ((Boolean) styleAccessorEntry[5]).booleanValue();
            boolean inheritable = ((Boolean) styleAccessorEntry[6]).booleanValue();
            Object initialValue = styleAccessorEntry[7];
            String initialValueAsString = (String) styleAccessorEntry[8];
            if (initialValueAsString == null)
                initialValueAsString = (initialValue != null) ? initialValue.toString() : null;
            accessors.put(styleName,
                new StyleAccessor(styleName, accessorName, valueClass, verifierClass, applicability, paddingPermitted, inheritable, initialValue, initialValueAsString));
        }
    }

    protected void setStyleInitialValue(Object content, StyleAccessor sa, Object initialValue, VerifierContext context) {
        // defer initial value setting to ISD generation (ttx)
    }

    protected void setStyleValue(Object content, String setterName, Class<?> valueClass, Object value) {
        try {
            Class<?> contentClass = content.getClass();
            Method m = contentClass.getMethod(setterName, new Class<?>[]{ valueClass });
            m.invoke(content, new Object[]{ value });
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        } catch (SecurityException e) {
            throw new RuntimeException(e);
        }
    }

    protected class StyleAccessor {

        QName styleName;
        String getterName;
        String setterName;
        Class<?> valueClass;
        StyleValueVerifier verifier;
        int applicability;
        boolean paddingPermitted;
        boolean inheritable;
        Object initialValue;
        String initialValueAsString;

        public StyleAccessor(QName styleName, String accessorName, Class<?> valueClass, Class<?> verifierClass,
                             int applicability, boolean paddingPermitted, boolean inheritable, Object initialValue, String initialValueAsString) {
            populate(styleName, accessorName, valueClass, verifierClass, applicability, paddingPermitted, inheritable, initialValue, initialValueAsString);
        }

        public QName getStyleName() {
            return styleName;
        }

        @Override
        public String toString() {
            StringBuffer sb = new StringBuffer();
            sb.append('[');
            sb.append(styleName);
            sb.append(',');
            sb.append(initialValue != null ? initialValue.toString() : "");
            sb.append(',');
            sb.append(inheritable);
            sb.append(']');
            return sb.toString();
        }

        public boolean doesStyleApply(QName eltName) {
            String nsUri = eltName.getNamespaceURI();
            if ((nsUri == null) || !nsUri.equals(NAMESPACE_TT)) {
                return false;
            } else {
                String localName = eltName.getLocalPart();
                if (localName.equals("tt"))
                    return (applicability & APPLIES_TO_TT) == APPLIES_TO_TT;
                else if (localName.equals("body"))
                    return (applicability & APPLIES_TO_BODY) == APPLIES_TO_BODY;
                else if (localName.equals("div"))
                    return (applicability & APPLIES_TO_DIV) == APPLIES_TO_DIV;
                else if (localName.equals("p"))
                    return (applicability & APPLIES_TO_P) == APPLIES_TO_P;
                else if (localName.equals("span"))
                    return (applicability & APPLIES_TO_SPAN) == APPLIES_TO_SPAN;
                else if (localName.equals("br"))
                    return (applicability & APPLIES_TO_BR) == APPLIES_TO_BR;
                else if (localName.equals("region"))
                    return (applicability & APPLIES_TO_REGION) == APPLIES_TO_REGION;
                else
                    return false;
            }
        }

        public boolean isInheritable() {
            return inheritable;
        }

        public String initialValueAsString() {
            return initialValueAsString;
        }

        protected boolean verify(Model model, Object content, Locator locator, VerifierContext context) {
            boolean success = true;
            Object value = getStyleValue(content);
            if (value != null) {
                Location location = new Location(content, context.getBindingElementName(content), styleName, locator);
                if (value instanceof String)
                    success = verify((String) value, location, context);
                else
                    success = verifier.verify(value, location, context);
            } else if (!isInitial(content))
                setStyleInitialValue(content, context);
            if (!success) {
                if (value != null) {
                    if (styleName.equals(styleAttributeName)) {
                        value = IdReferences.getIdReferences(value);
                    } else if (styleName.equals(regionAttributeName)) {
                        value = IdReferences.getIdReference(value);
                    } else
                        value = value.toString();
                    Reporter reporter = context.getReporter();
                    reporter.logError(reporter.message(locator,
                        "*KEY*", "Invalid {0} value ''{1}''.", styleName, value));
                }
            }
            return success;
        }

        protected boolean verify(String value, Location location, VerifierContext context) {
            boolean success = false;
            Reporter reporter = context.getReporter();
            Locator locator = location.getLocator();
            if (value.length() == 0) {
                reporter.logInfo(reporter.message(locator,
                    "*KEY*", "Empty {0} not permitted, got ''{1}''.", styleName, value));
            } else if (Strings.isAllXMLSpace(value)) {
                reporter.logInfo(reporter.message(locator,
                    "*KEY*", "The value of {0} is entirely XML space characters, got ''{1}''.", styleName, value));
            } else if (!paddingPermitted && !value.equals(value.trim())) {
                reporter.logInfo(reporter.message(locator,
                    "*KEY*", "XML space padding not permitted on {0}, got ''{1}''.", styleName, value));
            } else
                success = verifier.verify(value, location, context);
            return success;
        }

        private StyleValueVerifier createStyleValueVerifier(Class<?> verifierClass) {
            try {
                return (StyleValueVerifier) verifierClass.newInstance();
            } catch (InstantiationException e) {
                throw new RuntimeException(e);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }

        private void populate(QName styleName, String accessorName, Class<?> valueClass, Class<?> verifierClass,
                              int applicability, boolean paddingPermitted, boolean inheritable, Object initialValue, String initialValueAsString) {
            this.styleName = styleName;
            this.getterName = "get" + accessorName;
            this.setterName = "set" + accessorName;
            this.valueClass = valueClass;
            this.verifier = createStyleValueVerifier(verifierClass);
            this.paddingPermitted = paddingPermitted;
            this.applicability = applicability;
            this.inheritable = inheritable;
            this.initialValue = initialValue;
            this.initialValueAsString = initialValueAsString;
        }

        public Object getStyleValue(Object content) {
            try {
                Class<?> contentClass = content.getClass();
                Method m = contentClass.getMethod(getterName, new Class<?>[]{});
                return convertType(m.invoke(content, new Object[]{}), valueClass);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            } catch (IllegalArgumentException e) {
                throw new RuntimeException(e);
            } catch (InvocationTargetException e) {
                throw new RuntimeException(e);
            } catch (NoSuchMethodException e) {
                return null;
            } catch (SecurityException e) {
                throw new RuntimeException(e);
            }
        }

        private void setStyleInitialValue(Object content, VerifierContext context) {
            TTML1StyleVerifier.this.setStyleInitialValue(content, this, initialValue, context);
        }

        private Object convertType(Object value, Class<?> targetClass) {
            if (value == null)
                return null;
            else if (targetClass.isInstance(value))
                return value;
            else if (value.getClass() == String.class) {
                if (targetClass == Float.class) {
                    try {
                        return Float.valueOf((String) value);
                    } catch (NumberFormatException e) {
                        return null;
                    }
                } else
                    return null;
            } else if (value.getClass() == Float.class) {
                if (targetClass == String.class)
                    return ((Float) value).toString();
                else
                    return null;
            } else if (value instanceof Enum<?>) {
                if (targetClass == String.class)
                    return Enums.getValue((Enum<?>) value);
                else
                    return null;
            } else
                return null;
        }

    }

    public static final String getStyleNamespaceUri() {
        return NAMESPACE;
    }

}
