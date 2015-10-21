package com.redbullmediabase.resourcelayer.processing.ttt.webvtt.builders;

import com.redbullmediabase.resourcelayer.processing.ttt.tt2webvtt.AbstractTestCase;
import com.redbullmediabase.resourcelayer.processing.ttt.webvtt.model.Cue;
import com.redbullmediabase.resourcelayer.processing.ttt.webvtt.model.CueHtmlNode;
import com.redbullmediabase.resourcelayer.processing.ttt.webvtt.model.CueSettings;
import com.redbullmediabase.resourcelayer.processing.ttt.webvtt.model.Document;
import com.redbullmediabase.resourcelayer.processing.ttt.webvtt.model.Note;
import com.redbullmediabase.resourcelayer.processing.ttt.webvtt.model.Region;
import com.redbullmediabase.resourcelayer.processing.ttt.webvtt.model.RegionSettings;
import com.redbullmediabase.resourcelayer.processing.ttt.webvtt.model.helper.Pair;
import com.redbullmediabase.resourcelayer.processing.ttt.webvtt.model.helper.Timestamp;
import com.redbullmediabase.resourcelayer.processing.ttt.webvtt.output.Printer;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author Michal Samek, (Michal.Samek at at.redbullmediahouse.com)
 */
public class WebVTTBuilderTestCase extends AbstractTestCase {

    private ByteArrayOutputStream out = new ByteArrayOutputStream();

    @Before
    public void setup() throws IOException {
        Region topRegion = RegionBuilder.create()
                .withId("top")
                .withSetting(new RegionSettings.ViewportAnchorSetting(new Pair<>(80.0, 100.0)))
                .build();

        Region bottomRegion = RegionBuilder.create()
                .withId("bottom")
                .withSetting(new RegionSettings.ViewportAnchorSetting(new Pair<>(0.0, 100.0)))
                .build();

        Cue cue1 = CueBuilder.create()
                .withId("first")
                .withTiming(Timestamp.fromString("00:00.001"), Timestamp.fromString("00:01.000"))
                .withNode(CueTextNodeBuilder.create().withText("First cue").build())
                .withSetting(new CueSettings.RegionSetting(topRegion))
                .build();

        Cue boldCue = CueBuilder.create()
                .withId("bold")
                .withTiming(Timestamp.fromString("00:02.000"), Timestamp.fromString("00:05.233"))
                .withSetting(new CueSettings.RegionSetting(bottomRegion))
                .withNode(CueHtmlNodeBuilder.create()
                        .withType(CueHtmlNode.NodeType.BOLD)
                        .withChild(CueTextNodeBuilder.create().withText("Bold cue").build())
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
                .withCues(Arrays.asList(new Cue[]{cue1, boldCue, cue3}))
                .withRegion(topRegion)
                .withRegion(bottomRegion)
                .build();

        OutputStreamWriter writer = new OutputStreamWriter(out);
        Printer printer = new Printer(writer);
        printer.print(doc);
    }

    @Test
    public void verify() throws IOException {
        String outString = out.toString();
        List<String> outLines = new ArrayList<>(Arrays.asList(outString.split(System.lineSeparator())));
        List<String> manifestLines = new ArrayList<>(readFile("sample.vtt"));

//        outLines = outLines.stream().filter(s -> !s.isEmpty()).collect(Collectors.toList());
//        manifestLines = manifestLines.stream().filter(s -> !s.isEmpty()).collect(Collectors.toList());
        for (Iterator<String> it = outLines.iterator(); it.hasNext(); ) {
            String line = it.next();
            if (line.isEmpty()) {
                it.remove();
            }
        }

        for (Iterator<String> it = manifestLines.iterator(); it.hasNext(); ) {
            String line = it.next();
            if (line.isEmpty()) {
                it.remove();
            }
        }

        System.out.println(outString);
        
        Assert.assertEquals(manifestLines, outLines);
    }

}
