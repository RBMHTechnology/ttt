package com.redbullmediabase.resourcelayer.processing.ttt.webvtt.builders;

import com.redbullmediabase.resourcelayer.processing.ttt.webvtt.model.CueTextualPayload;

/**
 *
 * @author Michal Samek, (Michal.Samek at at.redbullmediahouse.com)
 */
class CueTextNodeBuilder {
    
    private String text;
    
    private CueTextNodeBuilder() {}
    
    public static CueTextNodeBuilder create() {
        return new CueTextNodeBuilder();
    }
    
    public CueTextualPayload build() {
        return new CueTextualPayload(text);
    }
    
    public CueTextNodeBuilder withText(String text) {
        this.text = text;
        return this;
    }
}
