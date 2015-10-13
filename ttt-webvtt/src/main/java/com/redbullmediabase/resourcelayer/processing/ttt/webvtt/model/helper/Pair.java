package com.redbullmediabase.resourcelayer.processing.ttt.webvtt.model.helper;

/**
 * Immutable pair.
 * 
 * @author Michal Samek, (Michal.Samek at at.redbullmediahouse.com)
 */
public class Pair<T,TT> {
    
    private final T first;
    private final TT second;
    
    public Pair(T first, TT second) {
        this.first = first;
        this.second = second;
    }

    public T getFirst() {
        return first;
    }

    public TT getSecond() {
        return second;
    }
    
    
}
