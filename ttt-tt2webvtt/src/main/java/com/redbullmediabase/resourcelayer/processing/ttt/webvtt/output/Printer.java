package com.redbullmediabase.resourcelayer.processing.ttt.webvtt.output;

import com.redbullmediabase.resourcelayer.processing.ttt.webvtt.model.HeaderMetadata;
import com.redbullmediabase.resourcelayer.processing.ttt.webvtt.model.Region;
import com.redbullmediabase.resourcelayer.processing.ttt.webvtt.model.CueTiming;
import com.redbullmediabase.resourcelayer.processing.ttt.webvtt.model.CueOrNote;
import com.redbullmediabase.resourcelayer.processing.ttt.webvtt.model.Cue;
import com.redbullmediabase.resourcelayer.processing.ttt.webvtt.model.CueTimestampNode;
import com.redbullmediabase.resourcelayer.processing.ttt.webvtt.model.CuePayload;
import com.redbullmediabase.resourcelayer.processing.ttt.webvtt.model.CueTextNode;
import com.redbullmediabase.resourcelayer.processing.ttt.webvtt.model.CueHtmlNode;
import com.redbullmediabase.resourcelayer.processing.ttt.webvtt.model.CueRubyNode;
import com.redbullmediabase.resourcelayer.processing.ttt.webvtt.model.Document;
import com.redbullmediabase.resourcelayer.processing.ttt.webvtt.model.Note;
import com.redbullmediabase.resourcelayer.processing.ttt.webvtt.model.CueHtmlNode.NodeType;
import com.redbullmediabase.resourcelayer.processing.ttt.webvtt.model.CueSettings.CueSetting;
import com.redbullmediabase.resourcelayer.processing.ttt.webvtt.model.RegionSettings.RegionSetting;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import org.apache.commons.lang.StringUtils;

/**
 *
 * @author Michal Samek, (Michal.Samek at at.redbullmediahouse.com)
 */
public class Printer extends AbstractPrinter implements AutoCloseable {

    private final BufferedWriter writer;
    private final StringBuilder sb;

    public Printer(OutputStreamWriter writer) {
        this.writer = new BufferedWriter(writer);
        this.sb = new StringBuilder();
    }

    @Override
    public void print(Document webvtt) throws IOException {
        sb.append(Constants.WEBVTT_TAG);
        sb.append(Constants.LT);

        printMetadataBlock(webvtt.getMetadataBlock());
        sb.append(Constants.LT).append(Constants.LT);

        printCueBlock(webvtt.getCueBlock());
        sb.append(Constants.LT);

        writer.append(sb.toString());
        writer.flush();
    }

    private void printMetadataBlock(List<HeaderMetadata> metadataBlock) {
//        metadataBlock.stream()
//                .filter(m -> m.getClass().equals(Region.class))
//                .map(r -> Region.class.cast(r))
//                .forEach(r ->
//                    printRegion(r)
//                );
        for (HeaderMetadata m : metadataBlock) {
            if (m instanceof Region) {
                printRegion((Region) m);
            }
        }
    }

    private void printRegion(Region region) {
        sb.append(Constants.REGION_TAG)
                .append(Constants.METADATA_HEADER_VALUE_DELIMITER)
                .append(Constants.REGION_SETTINGS_DELIMITER);
        sb.append(Constants.REGION_ID_NAME)
                .append(Constants.REGION_SETTING_VALUE_DELIMITER)
                .append(region.getId())
                .append(Constants.REGION_SETTINGS_DELIMITER);

//        String settings = region.getSettings().stream()
//                //sort the settings, so that the order is deterministic
//                .sorted((a,b) -> a.getClass().getCanonicalName().compareTo(b.getClass().getCanonicalName()))
//                .map(setting -> RegionSettingPrinter.print(setting))
//                .reduce("", (a, b) -> a + Constants.REGION_SETTINGS_DELIMITER + b);
        
        List<RegionSetting> settingsList = new ArrayList<>(region.getSettings());
        //sort the settings, so that the order is deterministic
        Collections.sort(settingsList, new RegionSettingComparator());
        
        List<String> settingStrings = new ArrayList<>();
        for (RegionSetting s : settingsList) {
            settingStrings.add(RegionSettingPrinter.print(s));
        }
        String settings = StringUtils.join(settingStrings, Constants.REGION_SETTINGS_DELIMITER);
        
        sb.append(settings);
        sb.append(Constants.LT);
    }

    private void printCueBlock(List<CueOrNote> cueBlock) {
//        cueBlock.stream().forEach(
//                cueOrNote -> {
//                    if (cueOrNote instanceof Cue) {
//                        printCue((Cue) cueOrNote);
//                    } else {
//                        printNote((Note) cueOrNote);
//                    }
//                });
        for (CueOrNote cueOrNote : cueBlock) {
            if (cueOrNote instanceof Cue) {
                printCue((Cue) cueOrNote);
            } else {
                printNote((Note) cueOrNote);
            }
        }
    }

    private void printCue(Cue cue) {
        //Cue identifier
        String id = cue.getId();
        if (id != null && !id.isEmpty()) {
            sb.append(id).append(Constants.LT);
        }

        //Cue timing
        printCueTiming(cue.getTiming());

        //Cue settings
        sb.append(Constants.CUE_SETTINGLIST_DELIMITER);
//        String settings = cue.getSettings().stream()
//                //sort the settings, so that the order is deterministic
//                .sorted((a,b) -> a.getClass().getCanonicalName().compareTo(b.getClass().getCanonicalName()))
//                .map(setting -> CueSettingPrinter.print(setting))
//                .reduce("", (a, b) -> a + Constants.CUE_SETTINGS_DELIMITER + b);
        List<CueSetting> settingsList = new ArrayList<>(cue.getSettings());
        Collections.sort(settingsList, new CueSettingComparator());
        List<String> settingStrings = new ArrayList<>();
        for (CueSetting s : settingsList) {
            settingStrings.add(CueSettingPrinter.print(s));
        }
        
        sb.append(StringUtils.join(settingStrings, Constants.CUE_SETTINGS_DELIMITER));
        sb.append(Constants.LT);

        //Cue payload
//        cue.getPayload().stream().forEach(p -> printCueNode(p));
        for (CuePayload payload : cue.getPayload()) {
            printCueNode(payload);
        }
        sb.append(Constants.LT).append(Constants.LT);

    }

    private void printCueTiming(CueTiming timing) {
        sb.append(timing.getStart())
                .append(Constants.CUE_TIMING_INNER_DELIMITER)
                .append(Constants.CUE_TIMING_DELIMITER)
                .append(Constants.CUE_TIMING_INNER_DELIMITER)
                .append(timing.getEnd());
    }

    private void printNote(Note note) {
        sb.append(Constants.NOTE_TAG).append(Constants.NOTE_DELIMITER);
        sb.append(note.getText());
        sb.append(Constants.LT).append(Constants.LT);
    }

    private void printCueNode(CuePayload node) {
        if (node instanceof CueHtmlNode) {
            printCueHtmlNode((CueHtmlNode) node);
        } else if (node instanceof CueTextNode) {
            printCueTextNode((CueTextNode) node);
        } else if (node instanceof CueTimestampNode) {
            printCueTimestampNode((CueTimestampNode) node);
        } else if (node instanceof CueRubyNode) {
            printCueRubyNode((CueRubyNode) node);
        } else {
            throw new IllegalArgumentException();
        }
    }

    private void printCueHtmlNode(CueHtmlNode node) {
        NodeType type = node.getType();
        String tagName = getNodeTagName(type);
//        String classes = node.getClasses().stream().reduce("", (a, b) -> a + Constants.CUE_NODE_CLASS_DELIMITER + b);
        String classes = StringUtils.join(node.getClasses(), Constants.CUE_NODE_CLASS_DELIMITER);
        String annotation = node.requiresAnnotation() ? " " + escapeText(node.getAnnotation()) : "";

        sb.append("<")
                .append(tagName)
                .append(classes)
                .append(annotation)
                .append(">");

//        node.getChildren().stream().forEach(n -> printCueNode(n));
        for (CuePayload child : node.getChildren()) {
            printCueNode(child);
        }

        sb.append("</")
                .append(tagName)
                .append(">");
    }

    private void printCueTextNode(CueTextNode node) {
        sb.append(node.getText());
    }
    
    private void printCueTimestampNode(CueTimestampNode node) {
        sb.append("<").append(node.getTimestamp()).append(">");
//        node.getChildren().stream().forEach(n -> printCueNode(n));
        for (CuePayload child : node.getChildren()) {
            printCueNode(child);
        }
    }
    
    private void printCueRubyNode(CueRubyNode node) {
        
    }

    private String getNodeTagName(NodeType type) {
        return type.name().toLowerCase().substring(0, 1);
    }

    private String escapeText(String text) {
        return text.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\u200E", "&lrm;")
                .replace("\u200F", "&rlm;")
                .replace(" ", "&nbsp;");
    }

    @Override
    public void close() throws Exception {
        writer.flush();
        writer.close();
    }
    
    
    private static class CueSettingComparator implements Comparator<CueSetting> {

        @Override
        public int compare(CueSetting o1, CueSetting o2) {
            return o1.getClass().getCanonicalName().compareTo(o2.getClass().getCanonicalName());
        }
        
    }
    
    private static class RegionSettingComparator implements Comparator<RegionSetting> {

        @Override
        public int compare(RegionSetting o1, RegionSetting o2) {
            return o1.getClass().getCanonicalName().compareTo(o2.getClass().getCanonicalName());
        }
        
    }
}
