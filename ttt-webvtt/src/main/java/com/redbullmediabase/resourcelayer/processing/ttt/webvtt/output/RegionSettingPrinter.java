package com.redbullmediabase.resourcelayer.processing.ttt.webvtt.output;

import com.redbullmediabase.resourcelayer.processing.ttt.webvtt.model.metadata.RegionSettings.*;
import com.redbullmediabase.resourcelayer.processing.ttt.webvtt.model.metadata.RegionSettings.ScrollSetting.ScrollSettingValue;
import com.redbullmediabase.resourcelayer.processing.ttt.webvtt.model.helper.Pair;
import com.redbullmediabase.resourcelayer.processing.ttt.webvtt.model.helper.Percentage;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 *
 * @author Michal Samek, (Michal.Samek at at.redbullmediahouse.com)
 */
public class RegionSettingPrinter {

    private final static Map<Class<? extends RegionSetting>, String> nameMap = new HashMap<>();
    static {
        nameMap.put(WidthSetting.class, Constants.REGION_WIDTH_SETTING_NAME);
        nameMap.put(LinesSetting.class, Constants.REGION_LINES_SETTING_NAME);
        nameMap.put(AnchorSetting.class, Constants.REGION_ANCHOR_SETTING_NAME);
        nameMap.put(ViewportAnchorSetting.class, Constants.REGION_VIEWPORT_ANCHOR_SETTING_NAME);
        nameMap.put(ScrollSetting.class, Constants.REGION_SCROLL_SETTING_NAME);
    }

    private final static Map<Class<? extends RegionSetting>, Function<Object, String>> valueMap = new HashMap<>();
    static {
        valueMap.put(WidthSetting.class, new WidthSettingPrinter());
        valueMap.put(LinesSetting.class, new LinesSettingPrinter());
        valueMap.put(AnchorSetting.class, new AnchorSettingPrinter());
        valueMap.put(ViewportAnchorSetting.class, new ViewportAnchorSettingPrinter());
        valueMap.put(ScrollSetting.class, new ScrollSettingPrinter());
    }
    
    public static String print(RegionSetting setting) {
        return printName(setting) + "=" + printValue(setting);
    }

    private static String printName(RegionSetting setting) {
        return nameMap.get(setting.getClass());
    }
    
    private static String printValue(RegionSetting setting) {
        return valueMap.get(setting.getClass()).apply(setting.getValue());
    }
    
    
    private static class WidthSettingPrinter implements Function<Object, String> {

        @Override
        public String apply(Object t) {
            Percentage value = (Percentage) t;
            return value.toString();
        }
    }

    private static class LinesSettingPrinter implements Function<Object, String> {

        @Override
        public String apply(Object t) {
            Integer value = (Integer) t;
            return value.toString();
        }
    }

    private static class AnchorSettingPrinter implements Function<Object, String> {

        @Override
        public String apply(Object t) {
            Pair<Percentage,Percentage> value = (Pair<Percentage,Percentage>) t;
            return value.getFirst() + "," + value.getSecond();
        }
    }

    private static class ViewportAnchorSettingPrinter implements Function<Object, String> {

        @Override
        public String apply(Object t) {
            Pair<Percentage,Percentage> value = (Pair<Percentage,Percentage>) t;
            return value.getFirst() + "," + value.getSecond();
        }
    }

    private static class ScrollSettingPrinter implements Function<Object, String> {

        @Override
        public String apply(Object t) {
            ScrollSettingValue value = (ScrollSettingValue) t;
            return value.name().toLowerCase();
        }
    }

}
