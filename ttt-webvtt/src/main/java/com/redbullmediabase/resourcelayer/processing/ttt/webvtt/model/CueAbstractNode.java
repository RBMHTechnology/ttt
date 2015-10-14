package com.redbullmediabase.resourcelayer.processing.ttt.webvtt.model;

import java.util.Collections;
import java.util.List;

/**
 * A base class for complex cue payload. Represents possible tree of cue nodes - every "complex" node can have multiple
 * child elements.
 * 
 * @see com.redbullmediabase.resourcelayer.processing.ttt.webvtt.model.CueHtmlNode
 * @see com.redbullmediabase.resourcelayer.processing.ttt.webvtt.model.CueRubyNode
 * @see com.redbullmediabase.resourcelayer.processing.ttt.webvtt.model.CueTimestampNode
 * @author Michal Samek, (Michal.Samek at at.redbullmediahouse.com)
 */
public abstract class CueAbstractNode implements CuePayload {
    
    protected final List<CuePayload> children;
    
    protected CueAbstractNode(List<CuePayload> children) {
        this.children = children != null
                ? Collections.unmodifiableList(children)
                : Collections.EMPTY_LIST;
    }

    public List<CuePayload> getChildren() {
        return children;
    }
}
