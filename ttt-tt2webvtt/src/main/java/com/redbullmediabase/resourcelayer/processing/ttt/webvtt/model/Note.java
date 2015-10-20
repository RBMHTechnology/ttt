package com.redbullmediabase.resourcelayer.processing.ttt.webvtt.model;

/**
 * Represents notes/comments.
 * 
 * A WebVTT comment block is ignored by the parser.
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
