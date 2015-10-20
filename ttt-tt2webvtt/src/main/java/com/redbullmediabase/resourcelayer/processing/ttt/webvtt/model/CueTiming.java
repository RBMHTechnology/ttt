package com.redbullmediabase.resourcelayer.processing.ttt.webvtt.model;

import com.redbullmediabase.resourcelayer.processing.ttt.webvtt.model.helper.Timestamp;

/**
 * Represents the cue timing.
 * 
 * @author Michal Samek, (Michal.Samek at at.redbullmediahouse.com)
 */
public class CueTiming implements Comparable<CueTiming> {

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

    @Override
    public int compareTo(CueTiming other) {
        int cmp = this.getStart().compareTo(other.getStart());
        if (cmp == 0) {
            cmp = this.getEnd().compareTo(other.getEnd());
        }
        return cmp;
    }
}
