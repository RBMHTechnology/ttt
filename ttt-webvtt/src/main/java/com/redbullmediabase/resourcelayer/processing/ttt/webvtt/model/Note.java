package com.redbullmediabase.resourcelayer.processing.ttt.webvtt.model;

import com.redbullmediabase.resourcelayer.processing.ttt.webvtt.model.cue.CueOrNote;

/**
 *
 * @author Michal Samek, (Michal.Samek at at.redbullmediahouse.com)
 */
public class Note implements CueOrNote {

    private final String text;

    public Note(final String note) {
        text = note;
    }

    public String getText() {
        return text;
    }
}
