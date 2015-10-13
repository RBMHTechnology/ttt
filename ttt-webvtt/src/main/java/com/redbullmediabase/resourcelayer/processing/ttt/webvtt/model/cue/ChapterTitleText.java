package com.redbullmediabase.resourcelayer.processing.ttt.webvtt.model.cue;

/**
 *
 * @author Michal Samek, (Michal.Samek at at.redbullmediahouse.com)
 */
public class ChapterTitleText implements CuePayload {
    
    private final String title;

    public ChapterTitleText(final String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }
    
}
