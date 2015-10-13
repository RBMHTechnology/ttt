package com.redbullmediabase.resourcelayer.processing.ttt.webvtt.output;

import com.redbullmediabase.resourcelayer.processing.ttt.webvtt.model.metadata.Region;
import com.redbullmediabase.resourcelayer.processing.ttt.webvtt.model.cue.CueSettings.*;
import com.redbullmediabase.resourcelayer.processing.ttt.webvtt.model.cue.CueSettings.AlignmentSetting.AlignmentSettingValue;
import com.redbullmediabase.resourcelayer.processing.ttt.webvtt.model.cue.CueSettings.VerticalTextSetting.VerticalTextSettingValue;
import com.redbullmediabase.resourcelayer.processing.ttt.webvtt.model.helper.Pair;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 *
 * @author Michal Samek, (Michal.Samek at at.redbullmediahouse.com)
 */
public class CueSettingPrinter {

    private final static Map<Class<? extends CueSetting>, String> nameMap = new HashMap<>();

    static {
        nameMap.put(VerticalTextSetting.class, Constants.CUE_VERTICAL_TEXT_SETTING_NAME);
        nameMap.put(LineSetting.class, Constants.CUE_LINE_SETTING_NAME);
        nameMap.put(PositionSetting.class, Constants.CUE_POSITION_SETTING_NAME);
        nameMap.put(SizeSetting.class, Constants.CUE_SIZE_SETTING_NAME);
        nameMap.put(AlignmentSetting.class, Constants.CUE_ALIGNMENT_SETTING_NAME);
        nameMap.put(RegionSetting.class, Constants.CUE_REGION_SETTING_NAME);
    }

    private final static Map<Class<? extends CueSetting>, Function<Object, String>> valueMap = new HashMap<>();

    static {
        valueMap.put(VerticalTextSetting.class, new VerticalTextSettingPrinter());
        valueMap.put(LineSetting.class, new LineSettingValuePrinter());
        valueMap.put(PositionSetting.class, new PositionSettingPrinter());
        valueMap.put(SizeSetting.class, new SizeSettingPrinter());
        valueMap.put(AlignmentSetting.class, new AlignmentSettingPrinter());
        valueMap.put(RegionSetting.class, new RegionSettingPrinter());
    }

    public static String print(CueSetting setting) {
        return printName(setting) + Constants.CUE_SETTING_VALUE_DELIMITER + printValue(setting);
    }

    public static String printName(CueSetting setting) {
        return nameMap.get(setting.getClass());
    }

    public static String printValue(CueSetting setting) {
        return valueMap.get(setting.getClass()).apply(setting.getValue());
    }

    private static class VerticalTextSettingPrinter implements Function<Object, String> {

        @Override
        public String apply(Object v) {
            VerticalTextSettingValue value = (VerticalTextSettingValue) v;
            return value.name().toLowerCase();
        }

    }

    private static class LineSettingValuePrinter implements Function<Object, String> {

        @Override
        public String apply(Object v) {
            Pair<Number, LineSetting.LineAlignment> value = (Pair<Number, LineSetting.LineAlignment>) v;
            return value.getFirst().toString()
                    + (value.getSecond() == null
                            ? ""
                            : Constants.CUE_SETTING_VALUE_INNER_DELIMITER + value.getSecond().name().toLowerCase());
        }

    }

    private static class PositionSettingPrinter implements Function<Object, String> {

        @Override
        public String apply(Object v) {
            Pair<Number, PositionSetting.PositionAlignment> value = (Pair<Number, PositionSetting.PositionAlignment>) v;
            return value.getFirst().toString()
                    + (value.getSecond() == null
                            ? ""
                            : Constants.CUE_SETTING_VALUE_INNER_DELIMITER + value.getSecond().name().toLowerCase());
        }

    }

    private static class SizeSettingPrinter implements Function<Object, String> {

        @Override
        public String apply(Object t) {
            Double value = (Double) t;
            return value.toString() + "%";
        }

    }

    private static class AlignmentSettingPrinter implements Function<Object, String> {

        @Override
        public String apply(Object t) {
            AlignmentSettingValue value = (AlignmentSettingValue) t;
            return value.name().toLowerCase();
        }

    }

    private static class RegionSettingPrinter implements Function<Object, String> {

        @Override
        public String apply(Object t) {
            Region value = (Region) t;
            return value.getId();
        }

    }
}
