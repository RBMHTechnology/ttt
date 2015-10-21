package com.redbullmediabase.resourcelayer.processing.ttt.tt2webvtt.app;

import com.redbullmediabase.resourcelayer.processing.ttt.tt2webvtt.AbstractTestCase;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
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
public class ConverterTestCase extends AbstractTestCase {

    WebVTTConverter converter;
    ByteArrayOutputStream out;

    @Before
    public void setup() throws Exception {
        converter = new WebVTTConverter();
        out = new ByteArrayOutputStream();

        converter.run(getClass().getResourceAsStream("BBC123456_MainTitle_EpisodeTitle_S01E02_EN_UK_sub.ttml.xml"), out, out);
    }

    @Test
    public void verify() throws IOException {
        String outString = out.toString();
        List<String> outLines = new ArrayList<>(Arrays.asList(outString.split(System.lineSeparator())));
        List<String> manifestLines = new ArrayList<>(readFile("BBC123456_MainTitle_EpisodeTitle_S01E02_EN_UK_sub.vtt"));

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

        System.out.println(out.toString());

        Assert.assertEquals(manifestLines, outLines);
    }
}
