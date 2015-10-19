package com.redbullmediabase.resourcelayer.processing.ttt.webvtt.builders;

import com.redbullmediabase.resourcelayer.processing.ttt.webvtt.model.CueTextNode;

/**
 *
 * @author Michal Samek, (Michal.Samek at at.redbullmediahouse.com)
 */
public class CueTextNodeBuilder {
    
    private String text;
    
    private CueTextNodeBuilder() {}
    
    public static CueTextNodeBuilder create() {
        return new CueTextNodeBuilder();
    }
    
    public CueTextNode build() {
        return new CueTextNode(text);
    }
    
    public CueTextNodeBuilder withText(String text) {
        this.text = text;
        return this;
    }

}
