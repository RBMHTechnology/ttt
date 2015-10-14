package com.redbullmediabase.resourcelayer.processing.ttt.webvtt.model;

/**
 * Represents simple textual cue payload. The text is not escaped.
 * 
 * @author Michal Samek, (Michal.Samek at at.redbullmediahouse.com)
 */
public class CueTextualPayload implements CuePayload {
    private final String text;

    public CueTextualPayload(final String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

}
