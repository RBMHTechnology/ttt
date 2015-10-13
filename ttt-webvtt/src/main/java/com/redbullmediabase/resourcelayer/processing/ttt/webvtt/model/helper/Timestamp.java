package com.redbullmediabase.resourcelayer.processing.ttt.webvtt.model.helper;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Class representing timestamps in WebVTT cue files.
 * 
 * The Timestamp instances are IMMUTABLE.
 *
 * @author Michal Samek, (Michal.Samek at at.redbullmediahouse.com)
 */
public class Timestamp {

    private final int hours, minutes, seconds, millis;

    public Timestamp(
            final int hours,
            final int minutes,
            final int seconds,
            final int millis) {
        this.hours = hours;
        this.minutes = minutes;
        this.seconds = seconds;
        this.millis = millis;
        if (!validate()) {
            throw new IllegalStateException("Illegal value of " + getClass());
        }
    }

    public Timestamp(int minutes, int seconds, int millis) {
        this(0, minutes, seconds, millis);
    }

    public final boolean validate() {
        return !(hours < 0
                || minutes < 0 || minutes > 59
                || seconds < 0 || seconds > 59
                || millis < 0 || millis > 999);
    }

    public int getHours() {
        return hours;
    }

    public int getMinutes() {
        return minutes;
    }

    public int getSeconds() {
        return seconds;
    }

    public int getMillis() {
        return millis;
    }
    
    private static final Pattern withHrsPattern = Pattern.compile("([0-9]+):([0-5][0-9]):([0-5][0-9])\\.([0-9]{3})");
    private static final Pattern withoutHrsPattern = Pattern.compile("([0-5][0-9]):([0-5][0-9])\\.([0-9]{3})");

    public static Timestamp fromString(String str) {
        Matcher withHrsMatcher = withHrsPattern.matcher(str);
        if (withHrsMatcher.matches()) {
            return new Timestamp(Integer.parseInt(withHrsMatcher.group(1)),
                    Integer.parseInt(withHrsMatcher.group(2)),
                    Integer.parseInt(withHrsMatcher.group(3)),
                    Integer.parseInt(withHrsMatcher.group(4)));
        }
        Matcher withoutHrsMatcher = withoutHrsPattern.matcher(str);
        if (withoutHrsMatcher.matches()) {
            return new Timestamp(Integer.parseInt(withoutHrsMatcher.group(1)),
                    Integer.parseInt(withoutHrsMatcher.group(2)),
                    Integer.parseInt(withoutHrsMatcher.group(3)));
        }
        return null;
    }
    
    @Override
    public String toString() {
        return String.format("%02d:%02d:%02d.%03d", hours, minutes, seconds, millis);
    }
}
