package com.redbullmediabase.resourcelayer.processing.ttt.tt2webvtt.app;

import com.redbullmediabase.resourcelayer.processing.ttt.webvtt.builders.*;
import com.redbullmediabase.resourcelayer.processing.ttt.webvtt.model.*;
import com.redbullmediabase.resourcelayer.processing.ttt.webvtt.model.helper.*;
import com.redbullmediabase.resourcelayer.processing.ttt.webvtt.output.Printer;
import com.skynav.ttv.model.Model;
import com.skynav.ttv.model.ebuttd.EBUTTD.EBUTTDModel;
import com.skynav.ttv.model.ttml1.tt.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Serializable;
import java.net.URI;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.JAXBIntrospector;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.ParserConfigurationException;

/**
 *
 * @author Michal Samek, (Michal.Samek at at.redbullmediahouse.com)
 */
public class WebVTTConverter {

    //global state
    private static final Charset encoding = Charset.defaultCharset();
    private static final Model model = new EBUTTDModel();

    //per-resource state
    private File resourceFile;
    private TimedText rootBinding;

    public WebVTTConverter() {

    }

    public void run(URI uri, OutputStream transformedStream, OutputStream errorStream) throws Exception {
        this.resourceFile = new File(uri);

        resourceUnmarshall();

        OutputStreamWriter transformedWriter = new OutputStreamWriter(transformedStream, encoding);
//        OutputStreamWriter errorWriter = new OutputStreamWriter(errorStream, encoding);

        List<Cue> transformedCues = transformCues();

        Document webvtt = DocumentBuilder.create()
                .withCues(transformedCues)
                .build();

        try (Printer webvttPrinter = new Printer(transformedWriter)) {
            webvttPrinter.print(webvtt);
        }
    }

    private void resourceUnmarshall() throws FileNotFoundException, JAXBException, ParserConfigurationException {
        JAXBContext jc;
        Unmarshaller unmarshaller;
        Object root;

        Reader reader = new InputStreamReader(new FileInputStream(resourceFile), encoding);

        jc = JAXBContext.newInstance(model.getJAXBContextPath());
        unmarshaller = jc.createUnmarshaller();

        //Fail on non-mapped elements/attributes
        unmarshaller.setEventHandler(event -> false);

        root = JAXBIntrospector.getValue(unmarshaller.unmarshal(reader));

        if (root instanceof TimedText) {
            this.rootBinding = (TimedText) root;
        } else {
            throw new JAXBException("Unmarshalled object is of incorrect type");
        }
    }

    private List<Cue> transformCues() {
        List<Cue> transformed = new ArrayList<>();

        List<Paragraph> paragraphs = new ArrayList<>();

        rootBinding.getBody().getDiv().stream().forEach((div) -> {
            div.getBlockClass().stream().filter((block) -> (block instanceof Paragraph)).map((block) -> (Paragraph) block).forEach((p) -> {
                paragraphs.add(p);
            });
        });

        for (Paragraph p : paragraphs) {
            //Extract the timing information
            Timestamp startTime = Timestamp.fromString(p.getBegin());
            Timestamp endTime = Timestamp.fromString(p.getEnd());
            if (startTime != null) {
                //If timing information is specified, await mixed content ...
                List<CuePayload> cueNodes = new ArrayList<>();
                for (Serializable c : p.getContent()) {
                    if (c instanceof String) {
                        cueNodes.add(CueTextNodeBuilder.create().withText((String) c).build());
                    } else if (c instanceof Break) {
                        cueNodes.add(CueTextNodeBuilder.create().withText(System.lineSeparator()).build());
                    } else if (c instanceof Span) {
                        Span span = (Span) c;
                        CueHtmlNodeBuilder spanNodeBuilder = CueHtmlNodeBuilder.create();
                        for (Serializable cspan : span.getContent()) {
                            if (c instanceof String) {
                                spanNodeBuilder.withChild(CueTextNodeBuilder.create().withText((String) c).build());
                            } else if (c instanceof Break) {
                                spanNodeBuilder.withChild(CueTextNodeBuilder.create().withText(System.lineSeparator()).build());
                            }
                        }
                        cueNodes.add(spanNodeBuilder.withType(CueHtmlNode.NodeType.CLASS).build());
                    }
                }

                //Build the cue object
                transformed.add(CueBuilder.create()
                        .withId(p.getId())
                        .withTiming(startTime, endTime)
                        .withNodes(cueNodes)
                        .build()
                );
            } else {
                //If timing information was NOT specified, await only spans with timing ...
                for (Serializable c : p.getContent()) {
                    assert c instanceof Span;
                    Span span = (Span) c;
                    startTime = Timestamp.fromString(span.getBegin());
                    endTime = Timestamp.fromString(span.getEnd());
                    StringBuilder sb = new StringBuilder();
                    for (Serializable cspan : span.getContent()) {
                        if (cspan instanceof String) {
                            sb.append((String) cspan);
                        } else if (cspan instanceof Break) {
                            sb.append(System.lineSeparator());
                        }
                    }
                    transformed.add(CueBuilder.create()
                            .withTiming(startTime, endTime)
                            .withNode(CueHtmlNodeBuilder.create()
                                    .withChild(CueTextNodeBuilder.create().withText(sb.toString()).build())
                                    .build()
                            )
                            .build()
                    );
                }
            }
        }
        return transformed;
    }

}
