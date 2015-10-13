package com.redbullmediabase.resourcelayer.processing.ttt.webvtt.model.cue;

import java.util.Collections;
import java.util.List;

/**
 *
 * @author Michal Samek, (Michal.Samek at at.redbullmediahouse.com)
 */
public abstract class CueAbstractNode implements CueNode {
    
    protected final List<CueNode> children;
    
    protected CueAbstractNode(List<CueNode> children) {
        this.children = children != null
                ? Collections.unmodifiableList(children)
                : Collections.EMPTY_LIST;
    }

    public List<CueNode> getChildren() {
        return children;
    }
}
