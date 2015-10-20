package com.redbullmediabase.resourcelayer.processing.ttt.webvtt.model;

import java.util.List;

/**
 * Used with ruby text tags.
 *
 * @author Michal Samek, (Michal.Samek at at.redbullmediahouse.com)
 */
public class CueRubyNode extends CueAbstractNode {

    public CueRubyNode(List<CuePayload> children) {
        super(children);
    }


    /**
     * Ruby text tags to display ruby characters (i.e. small annotative characters above other characters).
     */
    public static class CueRubyRtNode extends CueAbstractNode {

        public CueRubyRtNode(List<CuePayload> children) {
            super(children);
        }

    }
}
