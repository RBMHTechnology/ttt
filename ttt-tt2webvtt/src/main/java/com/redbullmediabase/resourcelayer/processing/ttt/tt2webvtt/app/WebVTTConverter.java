package com.redbullmediabase.resourcelayer.processing.ttt.tt2webvtt.app;

import com.redbullmediabase.resourcelayer.processing.ttt.webvtt.builders.*;
import com.redbullmediabase.resourcelayer.processing.ttt.webvtt.model.*;
import com.redbullmediabase.resourcelayer.processing.ttt.webvtt.model.helper.*;
import com.redbullmediabase.resourcelayer.processing.ttt.webvtt.output.Printer;
import com.skynav.ttv.model.Model;
import com.skynav.ttv.model.ebuttd.EBUTTD.EBUTTDModel;
import com.skynav.ttv.model.ttml1.tt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Serializable;
import java.net.URI;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.JAXBIntrospector;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.ValidationEvent;
import javax.xml.bind.ValidationEventHandler;
import javax.xml.parsers.ParserConfigurationException;

/**
 *
 * @author Michal Samek, (Michal.Samek at at.redbullmediahouse.com)
 */
public class WebVTTConverter {

    //global state
    private static final Charset encoding = Charset.defaultCharset();
    private static final Model model = new EBUTTDModel();

    private final ObjectFactory objectFactory = new ObjectFactory();

    //per-resource state
    private InputStream resourceStream;
    private TimedText rootBinding;
    private Map<String, com.skynav.ttv.model.ttml1.tt.Style> originalStyleMap;
    private Map<String, com.redbullmediabase.resourcelayer.processing.ttt.webvtt.model.Region> transformedRegionMap;
    private List<com.redbullmediabase.resourcelayer.processing.ttt.webvtt.model.Region> transformedRegions;
    private List<com.redbullmediabase.resourcelayer.processing.ttt.webvtt.model.Cue> cues;

    public WebVTTConverter() {

    }

    public static void main(String[] args) {

    }

    public static void main(URI input, URI output) {
        File resourceFile = new File(input);
        WebVTTConverter converter = new WebVTTConverter();
    }

    public void run(InputStream resourceStream, OutputStream transformedStream, OutputStream errorStream) throws Exception {
        setState(resourceStream);
        rootBinding = resourceUnmarshall(resourceStream);

        originalStyleMap = StyleHelper.extractStyleMap(rootBinding);
        StyleHelper.inheritStyleFromBody(rootBinding.getBody());
        transformedRegions = transformRegions(rootBinding);
        cues = transformCues(rootBinding);

        Document webvtt = DocumentBuilder.create()
                .withCues(cues)
                .build();

        try (Printer webvttPrinter = new Printer(new OutputStreamWriter(transformedStream, encoding))) {
            webvttPrinter.print(webvtt);
        }
    }

    private void setState(InputStream resourceStream) {
        this.resourceStream = resourceStream;
        transformedRegionMap = new HashMap<>();
        transformedRegions = new ArrayList<>();
        cues = new ArrayList<>();
    }

    private TimedText resourceUnmarshall(InputStream resourceStream) throws FileNotFoundException, JAXBException, ParserConfigurationException {
        JAXBContext jc;
        Unmarshaller unmarshaller;
        Object root;

        Reader reader = new InputStreamReader(resourceStream, encoding);

        jc = JAXBContext.newInstance(model.getJAXBContextPath());
        unmarshaller = jc.createUnmarshaller();

        //Fail on non-mapped elements/attributes
        unmarshaller.setEventHandler(new ValidationEventHandler() {

            @Override
            public boolean handleEvent(ValidationEvent event) {
                return false;
            }
        });

        root = JAXBIntrospector.getValue(unmarshaller.unmarshal(reader));

        if (root instanceof TimedText) {
            return (TimedText) root;
        } else {
            throw new JAXBException("Unmarshalled object is of incorrect type");
        }
    }

    /**
     *
     * @param root
     * @return
     */
    private List<com.redbullmediabase.resourcelayer.processing.ttt.webvtt.model.Region> transformRegions(TimedText root) {
        List<com.redbullmediabase.resourcelayer.processing.ttt.webvtt.model.Region> transformed = new ArrayList<>();

        for (com.skynav.ttv.model.ttml1.tt.Region region : root.getHead().getLayout().getRegion()) {
            transformed.add(
                    RegionBuilder.create()
                    .withId(region.getId())
                    .build()
            );
        }

        return transformed;
    }

    /**
     *
     * @param root
     * @return
     */
    private List<Cue> transformCues(TimedText root) {
        List<Cue> transformed = new ArrayList<>();

        //for each "div"
        for (Division div : root.getBody().getDiv()) {
            //for each "p"
            for (Object block : div.getBlockClass()) {
                if (JAXBIntrospector.getValue(block) instanceof Paragraph) {
                    Paragraph p = (Paragraph) block;
                    generateAnonymousSpans(p);
                    transformed.addAll(transformParagraph(p));
                }
            }
        }

        return transformed;
    }

    /**
     * Ensures that all the "textual" content (String or Break) of a Paragraph is wrapped in a Span object. Modifies the
     * input argument!
     *
     * @param par Paragraph object that is to be modified
     */
    private Paragraph generateAnonymousSpans(Paragraph par) {
        List<Serializable> contents = par.getContent();
        for (Serializable s : contents) {
            //If content is of type String or JAXBElement<Break>, wrap it inside a Span
            if (s instanceof String
                    || (JAXBIntrospector.getValue(s) instanceof Break)) {
                Span newSpan = objectFactory.createSpan();
                newSpan.getContent().add(s);
                contents.set(contents.indexOf(s), objectFactory.createSpan(newSpan));
            }
        }
        return par;
    }

    private Body generateStyleReferences(Body body) {
        List bodyStyles = body.getStyleAttribute();

        return body;
    }

    /**
     *
     * @param par
     * @return
     */
    private List<Cue> transformParagraph(Paragraph par) {
        Timestamp startTime = Timestamp.fromString(par.getBegin());
        Timestamp endTime = Timestamp.fromString(par.getEnd());
        List<Cue> transformedSpans = new ArrayList<>();
        
        for (Serializable object : par.getContent()) {
            if (JAXBIntrospector.getValue(object) instanceof Span) {
                Span span = (Span) JAXBIntrospector.getValue(object);
                transformedSpans.add(transformSpan(span, par.getId(), startTime, endTime));
            }
        }
        
        return mergeCuesById(transformedSpans);
    }

    private Cue transformSpan(Span span, String id, Timestamp start, Timestamp end) {
        //If timing information was not defined on the parent paragraph element, it has to be defined here
        if (start == null) {
            start = Timestamp.fromString(span.getBegin());
            end = Timestamp.fromString(span.getEnd());
        }

        //If id was no defined on the parant paragraph element, it has to be defined here
        if (id == null) {
            id = span.getId();
        }

        StringBuilder sb = new StringBuilder();
        for (Serializable s : span.getContent()) {
            if (s instanceof String) {
                sb.append((String) s);
            } else if (JAXBIntrospector.getValue(s) instanceof Break) {
                sb.append(System.lineSeparator());
            }
        }
        return CueBuilder.create()
                .withId(id)
                .withTiming(start, end)
                .withNode(CueTextNodeBuilder.create().withText(sb.toString()).build())
                .build();
    }

    
    private List<Cue> mergeCuesById(List<Cue> cues) {
        Map<String, List<Cue>> cueIdMap = new HashMap<>();
        for (Cue cue : cues) {
            if (cueIdMap.get(cue.getId()) == null) {
                cueIdMap.put(cue.getId(), new ArrayList<Cue>());
            }
            cueIdMap.get(cue.getId()).add(cue);
        }
        
        List<Cue> processed = new ArrayList<>();
        for (String id : cueIdMap.keySet()) {
            boolean first = true;
            Cue current = null;
            for (Cue cc : cueIdMap.get(id)) {
                if (first) {
                    current = cc;
                    first = false;
                } else {
                    current = CueBuilder.create(current).withNodes(cc.getPayload()).build();
                }
            }
            processed.add(current);
        }

        return processed;
    }
}
