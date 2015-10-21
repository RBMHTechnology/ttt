package com.redbullmediabase.resourcelayer.processing.ttt.tt2webvtt.app;

import com.skynav.ttv.model.ttml1.tt.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.xml.bind.JAXBIntrospector;

/**
 *
 * @author Michal Samek, (Michal.Samek at at.redbullmediahouse.com)
 */
public class StyleHelper {

    /**
     *
     * @param root
     * @return
     */
    public static Map<String, Style> extractStyleMap(TimedText root) {
        Map<String, Style> styleMap = new HashMap<>();
        for (com.skynav.ttv.model.ttml1.tt.Style s : root.getHead().getStyling().getStyle()) {
            styleMap.put(s.getId(), s);
        }
        return styleMap;
    }

//    public static Style mergeStyleList(List<Style> styles) {
//        return styles.stream().reduce((a, b) -> mergeStyles(a, b)).get();
//    }

//    public static Style mergeStyles(Style a, Style b) {
//        Style merged = new Style();
//        try {
////            BeanUtils.copyProperties(merged, a);
////            BeanUtils.copyProperties(merged, b);
//        } catch (Exception ex) {
//            throw new RuntimeException(ex);
////            throw new ConversionException(ex);
//        }
//        return merged;
//    }

    public static void inheritStyleFromBody(Body body) {
        List styles = body.getStyleAttribute();
        for (Division div : body.getDiv()) {
            inheritStyleToDiv(div, styles);
        }
    }

    public static void inheritStyleToDiv(Division div, List inheritedStyles) {
        //Prepend inherited
        div.getStyleAttribute().add(0, inheritedStyles);
        List styles = div.getStyleAttribute();
        for (Object block : div.getBlockClass()) {
            if (JAXBIntrospector.getValue(block) instanceof Paragraph) {
                Paragraph par = (Paragraph) block;
                inheritStyleToParagraph(par, styles);
            }
        }
    }

    public static void inheritStyleToParagraph(Paragraph par, List inheritedStyles) {
        //Prepend inherited
        par.getStyleAttribute().add(0, inheritedStyles);

        List styles = par.getStyleAttribute();
        for (Object block : par.getContent()) {
            if (JAXBIntrospector.getValue(block) instanceof Span) {
                Span span = (Span) block;
                inheritStyleToSpan(span, styles);
            }
        }
    }

    private static void inheritStyleToSpan(Span span, List styles) {
        //Prepend inherited
        span.getStyleAttribute().addAll(0, styles);
    }

}
