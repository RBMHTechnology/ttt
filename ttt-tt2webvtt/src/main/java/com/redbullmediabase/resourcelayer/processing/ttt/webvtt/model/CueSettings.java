package com.redbullmediabase.resourcelayer.processing.ttt.webvtt.model;

import com.redbullmediabase.resourcelayer.processing.ttt.webvtt.model.helper.Pair;
import com.redbullmediabase.resourcelayer.processing.ttt.webvtt.model.helper.Percentage;

/**
 * Wrapper class for Setting classes applicable to cues.
 * 
 * @author Michal Samek, (Michal.Samek at at.redbullmediahouse.com)
 */
public class CueSettings {

    /**
     * An interface for cue settings.
     *
     * @param <T> value of the setting
     */
    public static abstract class CueSetting<T> {

        /**
         * Actual value of the setting
         */
        private final T value;

        protected CueSetting(final T value) {
            this.value = value;
        }

        public T getValue() {
            return value;
        }
    }
    
    /**
     * A WebVTT vertical text cue setting configures the cue to use vertical text layout rather than horizontal text
     * layout. Vertical text layout is sometimes used in Japanese, for example. The default is horizontal layout.
     */
    public static class VerticalTextSetting extends CueSetting<VerticalTextSetting.VerticalTextSettingValue> {

        public VerticalTextSetting(VerticalTextSettingValue value) {
            super(value);
        }

        public static enum VerticalTextSettingValue {

            RL, LR
        }
    }

    /**
     * A WebVTT line cue setting configures the offset of the cue box from the video viewport's edge in the direction
     * opposite to the writing direction. For horizontal cues, this is the vertical offset from the top of the video
     * viewport. The offset is for the start, middle, or end of the cue box, depending on the WebVTT cue line alignment
     * value - start by default. The offset can be given either as a percentage of the video dimension or as a line
     * number. Line numbers are based on the size of the first line of the cue. Positive line numbers count from the
     * start of the video viewport (the first line is numbered 0), negative line numbers from the end of the viewport
     * (the last line is numbered −1).
     */
    public static class LineSetting extends CueSetting<Pair<Number,LineSetting.LineAlignment>> {

        public LineSetting(Number value) {
            super(new Pair<>(value, LineAlignment.NONE));
        }
        
        public LineSetting(Number value, LineAlignment align) {
            super(new Pair<>(value, align));
        }

        public static enum LineAlignment {

            START, MIDDLE, END, NONE
        }
    }

    /**
     * A WebVTT position cue setting configures the indent position of the cue box in the direction orthogonal to the
     * WebVTT line cue setting. For horizontal cues, this is the horizontal position. The cue position is given as a
     * percentage of the video viewport. The positioning is for the start, middle, or end of the cue box, depending on
     * the cue's computed position alignment, which is overridden by the WebVTT position cue setting.
     */
    public static class PositionSetting extends CueSetting<Pair<Number, PositionSetting.PositionAlignment>> {

        public PositionSetting(Number value) {
            super(new Pair<>(value, PositionAlignment.NONE));
        }
        
        public PositionSetting(Number value, PositionAlignment align) {
            super(new Pair<>(value, align));
        }

        public static enum PositionAlignment {

            START, MIDDLE, END, NONE
        }
    }

    /**
     * A WebVTT size cue setting configures the size of the cue box in the same direction as the WebVTT position cue
     * setting. For horizontal cues, this is the width of the cue box. It is given as a percentage of the width of the
     * viewport.
     */
    public static class SizeSetting extends CueSetting<Percentage> {

        public SizeSetting(Percentage value) {
            super(value);
        }
    }

    /**
     * A WebVTT alignment cue setting configures the alignment of the text within the cue. The keywords are relative to
     * the text direction; for left-to-right English text, "start" means left-aligned.
     */
    public static class AlignmentSetting extends CueSetting<AlignmentSetting.AlignmentSettingValue> {

        public AlignmentSetting(AlignmentSettingValue value) {
            super(value);
        }

        public static enum AlignmentSettingValue {

            START, MIDDLE, END, LEFT, RIGHT
        }
    }

    /**
     * A WebVTT region cue setting configures a cue to become part of a region by referencing the region's identifier
     * unless the cue has a "vertical", "line" or "size" cue setting. If a cue is part of a region, its cue settings for
     * "position" and "align" are applied to the line boxes in the cue relative to the region box.
     */
    public static class RegionSetting extends CueSetting<Region> {

        public RegionSetting(Region value) {
            super(value);
        }
    }
}
