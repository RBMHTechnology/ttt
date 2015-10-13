package com.redbullmediabase.resourcelayer.processing.ttt.webvtt.model.helper;

/**
 *
 * @author Michal Samek, (Michal.Samek at at.redbullmediahouse.com)
 */
public class Percentage extends Number {
    public Double value;
    
    public Percentage(Integer value) {
        this(value.doubleValue());
    }
    
    public Percentage(Double value) {
        if (value < 0 || value > 100) {
            throw new IllegalArgumentException("Invalid percentage value");
        }
        this.value = value;
    }
    
    @Override
    public String toString() {
        return value + "%";
    }

    @Override
    public int intValue() {
        return value.intValue();
    }

    @Override
    public long longValue() {
        return value.longValue();
    }

    @Override
    public float floatValue() {
        return value.floatValue();
    }

    @Override
    public double doubleValue() {
        return value.doubleValue();
    }
}
