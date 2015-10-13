package com.redbullmediabase.resourcelayer.processing.ttt.webvtt.model.cue;

/**
 *
 * @author Michal Samek, (Michal.Samek at at.redbullmediahouse.com)
 */
public class CueTextNode implements CueNode {
    private final String text;

    public CueTextNode(final String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    @Override
    public boolean requiresAnnotation() {
        return false;
    }
    
}
