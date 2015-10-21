package com.redbullmediabase.resourcelayer.processing.ttt.webvtt.output;

/**
 *
 * @author Michal Samek, (Michal.Samek at at.redbullmediahouse.com)
 */
final public class Constants {

    public static final String WEBVTT_TAG = "WEBVTT";
    public static final String NOTE_TAG = "NOTE";
    public static final String REGION_TAG = "Region";
    public static final String LT = System.lineSeparator();

    /**
     * Cue settings names.
     */
    public static final String CUE_VERTICAL_TEXT_SETTING_NAME = "vertical";
    public static final String CUE_LINE_SETTING_NAME = "line";
    public static final String CUE_POSITION_SETTING_NAME = "position";
    public static final String CUE_SIZE_SETTING_NAME = "size";
    public static final String CUE_ALIGNMENT_SETTING_NAME = "align";
    public static final String CUE_REGION_SETTING_NAME = "region";

    /**
     * Region settings names.
     */
    public static final String REGION_WIDTH_SETTING_NAME = "width";
    public static final String REGION_LINES_SETTING_NAME = "lines";
    public static final String REGION_ANCHOR_SETTING_NAME = "regionanchor";
    public static final String REGION_VIEWPORT_ANCHOR_SETTING_NAME= "viewportanchor";
    public static final String REGION_SCROLL_SETTING_NAME = "scroll";
    public static final String REGION_ID_NAME = "id";
    
    /** Separates metadata header name from value */
    public static final String METADATA_HEADER_VALUE_DELIMITER = ":";
    
    /** Separates region setting header from value */
    public static final String REGION_SETTING_VALUE_DELIMITER = "=";
    
    public static final String REGION_SETTINGS_DELIMITER = " ";
    
    /** Separates cue setting name from value */
    public static final String CUE_SETTING_VALUE_DELIMITER = ":";
    
    /** Separates cue setting values from each other */
    public static final String CUE_SETTING_VALUE_INNER_DELIMITER = ",";
    
    /** Separates cue setting list from the timing */
    public static final String CUE_SETTINGLIST_DELIMITER = "\t";
    
    /** Separates cue setting items in the cue setting list */
    public static final String CUE_SETTINGS_DELIMITER = " ";
    
    /** Separates the comment payload from the NOTE_TAG */
    public static final String NOTE_DELIMITER = "\t";
    
    /** Separates timestamp from CUE_TIMING_DELIMITER */
    public static final String CUE_TIMING_INNER_DELIMITER = " ";
    
    /** Cue timing delimiter */
    public static final String CUE_TIMING_DELIMITER = "-->";
    
    /** Separates cue node classes */
    public static final String CUE_NODE_CLASS_DELIMITER = ".";
    
}
