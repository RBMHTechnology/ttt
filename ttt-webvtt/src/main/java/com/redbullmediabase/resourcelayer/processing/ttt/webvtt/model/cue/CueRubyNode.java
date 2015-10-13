package com.redbullmediabase.resourcelayer.processing.ttt.webvtt.model.cue;

import java.util.List;

/**
 * Used with ruby text tags.
 *
 * @author Michal Samek, (Michal.Samek at at.redbullmediahouse.com)
 */
public class CueRubyNode extends CueAbstractNode {

    public CueRubyNode(List<CueNode> children) {
        super(children);
    }

    @Override
    public boolean requiresAnnotation() {
        return false;
    }

    /**
     * Ruby text tags to display ruby characters (i.e. small annotative characters above other characters).
     */
    public static class CueRubyRtNode extends CueAbstractNode {

        public CueRubyRtNode(List<CueNode> children) {
            super(children);
        }

        @Override
        public boolean requiresAnnotation() {
            return false;
        }

    }
}
