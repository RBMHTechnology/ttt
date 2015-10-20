package com.redbullmediabase.resourcelayer.processing.ttt.webvtt.builders;

import com.redbullmediabase.resourcelayer.processing.ttt.webvtt.model.CuePayload;
import com.redbullmediabase.resourcelayer.processing.ttt.webvtt.model.CueTimestampNode;
import com.redbullmediabase.resourcelayer.processing.ttt.webvtt.model.helper.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 *
 * @author Michal Samek, (Michal.Samek at at.redbullmediahouse.com)
 */
public class CueTimestampNodeBuilder {

    private Timestamp timestamp;
    private final List<CuePayload> children;
    
    private CueTimestampNodeBuilder() {
        children = new ArrayList<>();
    }
    
    public static CueTimestampNodeBuilder create() {
        return new CueTimestampNodeBuilder();
    }
    
    public CueTimestampNode build() {
        return new CueTimestampNode(timestamp, children);
    }
    
    public CueTimestampNodeBuilder withChild(CuePayload child) {
        children.add(child);
        return this;
    }
    
    public CueTimestampNodeBuilder withChildren(Collection<CuePayload> childs) {
        children.addAll(childs);
        return this;
    }

}
