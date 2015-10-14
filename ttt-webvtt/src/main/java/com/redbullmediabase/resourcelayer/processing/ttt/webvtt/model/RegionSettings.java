package com.redbullmediabase.resourcelayer.processing.ttt.webvtt.model;

import com.redbullmediabase.resourcelayer.processing.ttt.webvtt.model.helper.Pair;
import com.redbullmediabase.resourcelayer.processing.ttt.webvtt.model.helper.Percentage;

/**
 * Wrapper class for Setting classes applicable to regions.
 * 
 * @author Michal Samek, (Michal.Samek at at.redbullmediahouse.com)
 */
public class RegionSettings {

    /**
     * A base type for classes representing specific region-related setting.
     * 
     * The WebVTT region settings give configuration options regarding the dimensions, positioning and anchoring of
     * the region. For example, it allows a group of cues within a region to be anchored in the center of the region and
     * the center of the video viewport. In this example, when the font size grows, the region grows uniformly in all
     * directions from the center.
     *
     * @param <T> a value of the setting
     */
    public static abstract class RegionSetting<T> {

        private final T value;

        protected RegionSetting(final T value) {
            this.value = value;
        }

        public T getValue() {
            return value;
        }
    }

    /**
     * The WebVTT region width setting provides a fixed width as a percentage of the video width for the region into
     * which cues are rendered and based on which alignment is calculated.
     */
    public static class WidthSetting extends RegionSetting<Percentage> {

        public WidthSetting(Percentage value) {
            super(value);
        }
    }

    /**
     * The WebVTT region lines setting provides a fixed height as a number of lines for the region into which cues are
     * rendered. As such, it defines the height of the roll-up region if it is a scroll region.
     */
    public static class LinesSetting extends RegionSetting<Integer> {

        public LinesSetting(Integer value) {
            super(value);
        }
    }

    /**
     * The WebVTT region anchor setting provides a tuple of two percentages that specify the point within the region box
     * that is fixed in location. The first percentage measures the x-dimension and the second percentage y-dimension
     * from the top left corner of the region box. If no WebVTT region anchor setting is given, the anchor defaults to
     * 0%, 100% (i.e. the bottom left corner).
     */
    public static class AnchorSetting extends RegionSetting<Pair<Double, Double>> {

        public AnchorSetting(Pair<Double, Double> value) {
            super(value);
        }

    }

    /**
     * The WebVTT region viewport anchor setting provides a tuple of two percentages that specify the point within the
     * video viewport that the region anchor point is anchored to. The first percentage measures the x-dimension and the
     * second percentage measures the y-dimension from the top left corner of the video viewport box. If no viewport
     * anchor is given, it defaults to 0%, 100% (i.e. the bottom left corner).
     */
    public static class ViewportAnchorSetting extends RegionSetting<Pair<Double, Double>> {

        public ViewportAnchorSetting(Pair<Double, Double> value) {
            super(value);
        }

    }

    /**
     * The WebVTT region scroll setting specifies whether cues rendered into the region are allowed to move out of their
     * initial rendering place and roll up, i.e. move towards the top of the video viewport. If the scroll setting is
     * omitted, cues do not move from their rendered position.
     */
    public static class ScrollSetting extends RegionSetting<ScrollSetting.ScrollSettingValue> {

        public static enum ScrollSettingValue {

            UP
        }

        public ScrollSetting(ScrollSettingValue value) {
            super(value);
        }

    }
}
