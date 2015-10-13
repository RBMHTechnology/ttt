package com.redbullmediabase.resourcelayer.processing.ttt.webvtt.model.cue;

import com.redbullmediabase.resourcelayer.processing.ttt.webvtt.model.helper.Timestamp;

/**
 *
 * @author Michal Samek, (Michal.Samek at at.redbullmediahouse.com)
 */
public class CueTiming {

    private final Timestamp start;
    private final Timestamp end;

    public CueTiming(final Timestamp start, final Timestamp end) {
        this.start = start;
        this.end = end;
    }

    public Timestamp getStart() {
        return start;
    }

    public Timestamp getEnd() {
        return end;
    }
}
