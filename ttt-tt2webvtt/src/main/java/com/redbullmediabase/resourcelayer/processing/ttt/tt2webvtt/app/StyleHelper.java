package com.redbullmediabase.resourcelayer.processing.ttt.tt2webvtt.app;

import com.skynav.ttv.model.ttml1.tt.*;
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
    public static Map<String, com.skynav.ttv.model.ttml1.tt.Style> extractStyleMap(TimedText root) {
        return root.getHead().getStyling().getStyle().stream()
                .collect(Collectors.toMap(com.skynav.ttv.model.ttml1.tt.Style::getId, Function.identity()));
    }

    public static Style mergeStyleList(List<Style> styles) {
        return styles.stream().reduce((a, b) -> mergeStyles(a, b)).get();
    }

    public static Style mergeStyles(Style a, Style b) {
        Style merged = new Style();
        try {
//            BeanUtils.copyProperties(merged, a);
//            BeanUtils.copyProperties(merged, b);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
//            throw new ConversionException(ex);
        }
        return merged;
    }

    public static void inheritStyleFromBody(Body body) {
        List styles = body.getStyleAttribute();

        body.getDiv().stream().forEach(div -> inheritStyleToDiv(div, styles));
    }

    public static void inheritStyleToDiv(Division div, List inheritedStyles) {
        //Prepend inherited
        div.getStyleAttribute().add(0, inheritedStyles);

        List styles = div.getStyleAttribute();
        div.getBlockClass().stream()
                .map(block -> JAXBIntrospector.getValue(block))
                .filter(block -> block instanceof Paragraph)
                .map(par -> Paragraph.class.cast(par))
                .forEach(par -> inheritStyleToParagraph(par, styles));
    }

    public static void inheritStyleToParagraph(Paragraph par, List inheritedStyles) {
        //Prepend inherited
        par.getStyleAttribute().add(0, inheritedStyles);

        List styles = par.getStyleAttribute();
        par.getContent().stream()
                .map(content -> JAXBIntrospector.getValue(content))
                .filter(content -> content instanceof Span)
                .map(span -> Span.class.cast(span))
                .forEach(span -> inheritStyleToSpan(span, styles));
    }

    private static void inheritStyleToSpan(Span span, List styles) {
        //Prepend inherited
        span.getStyleAttribute().addAll(0, styles);
    }

}
