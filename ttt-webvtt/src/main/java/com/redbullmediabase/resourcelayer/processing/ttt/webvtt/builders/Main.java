package com.redbullmediabase.resourcelayer.processing.ttt.webvtt.builders;

import com.redbullmediabase.resourcelayer.processing.ttt.webvtt.model.Document;
import com.redbullmediabase.resourcelayer.processing.ttt.webvtt.model.Note;
import com.redbullmediabase.resourcelayer.processing.ttt.webvtt.model.Region;
import com.redbullmediabase.resourcelayer.processing.ttt.webvtt.model.RegionSettings.ViewportAnchorSetting;
import com.redbullmediabase.resourcelayer.processing.ttt.webvtt.model.Cue;
import com.redbullmediabase.resourcelayer.processing.ttt.webvtt.model.helper.Timestamp;
import com.redbullmediabase.resourcelayer.processing.ttt.webvtt.model.CueHtmlNode;
import com.redbullmediabase.resourcelayer.processing.ttt.webvtt.model.CueSettings;
import com.redbullmediabase.resourcelayer.processing.ttt.webvtt.model.helper.Pair;
import com.redbullmediabase.resourcelayer.processing.ttt.webvtt.output.Printer;
import java.io.IOException;
import java.io.OutputStreamWriter;

/**
 *
 * @author Michal Samek, (Michal.Samek at at.redbullmediahouse.com)
 */
public class Main {

    public static void main(String[] args) throws IOException {

        Region topRegion = RegionBuilder.create()
                .withId("top")
                .withSetting(new ViewportAnchorSetting(new Pair<>(80.0, 100.0)))
                .build();

        Region bottomRegion = RegionBuilder.create()
                .withId("bottom")
                .withSetting(new ViewportAnchorSetting(new Pair<>(0.0, 100.0)))
                .build();

        Cue cue1 = CueBuilder.create()
                .withId("first")
                .withTiming(Timestamp.fromString("00:00.001"), Timestamp.fromString("00:01.000"))
                .withText("First cue")
                .withSetting(new CueSettings.RegionSetting(topRegion))
                .build();

        Cue boldCue = CueBuilder.create()
                .withId("bold")
                .withTiming(Timestamp.fromString("00:02.000"), Timestamp.fromString("00:05.233"))
                .withSetting(new CueSettings.RegionSetting(bottomRegion))
                .withNode(CueHtmlNodeBuilder.create()
                        .withType(CueHtmlNode.NodeType.BOLD)
                        .withText("Bold cue")
                        .build()
                )
                .build();

        Cue cue3 = CueBuilder.create()
                .withId("last")
                .withTiming(Timestamp.fromString("00:10.000"), Timestamp.fromString("00:20.000"))
                .withNode(
                        CueTextNodeBuilder.create()
                        .withText("Text cue")
                        .build()
                )
                .build();

        Document doc = DocumentBuilder.create()
                .withNote(new Note("A demonstration of WebVTT library"))
                .withCues(new Cue[]{cue1, boldCue, cue3})
                .withRegion(topRegion)
                .withRegion(bottomRegion)
                .build();

        OutputStreamWriter writer = new OutputStreamWriter(System.out);
        Printer printer = new Printer(writer);
        printer.print(doc);
    }

}
