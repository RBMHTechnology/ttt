package com.redbullmediabase.resourcelayer.processing.ttt.webvtt.model.cue;

import java.util.Collections;
import java.util.List;

/**
 *
 * @author Michal Samek, (Michal.Samek at at.redbullmediahouse.com)
 */
public class CueHtmlNode extends CueAbstractNode {

    protected final String annotation;
    private final List<String> classes;
    private final NodeType type;

    public static enum NodeType {

        /**
         * Bold the contained text.
         */
        BOLD,
        /**
         * Italicize the contained text.
         */
        ITALIC,
        /**
         * Underline the contained text.
         */
        UNDERLINE,
        /**
         * Style the contained text using a CSS class.
         */
        CLASS,
        /**
         * Similar to class tag, but allows to specify a speaker. Also used to style the contained text using CSS.
         */
        VOICE,
        LANG,
    }

    public CueHtmlNode(
            final NodeType type,
            final String annotation,
            final List<CueNode> children,
            final List<String> nodeClasses) {
        super(children);
        this.type = type;
        this.annotation = annotation;

        if (requiresAnnotation() && (annotation == null || annotation.isEmpty())) {
            throw new IllegalStateException();
        }

        classes = nodeClasses != null
                ? Collections.unmodifiableList(nodeClasses)
                : Collections.EMPTY_LIST;
    }

    @Override
    public final boolean requiresAnnotation() {
        switch (getType()) {
            case VOICE:
                return true;
            case LANG:
                return true;
            default:
                return false;
        }
    }

    public List<String> getClasses() {
        return classes;
    }

    public String getAnnotation() {
        return annotation;
    }

    public NodeType getType() {
        return type;
    }
}
