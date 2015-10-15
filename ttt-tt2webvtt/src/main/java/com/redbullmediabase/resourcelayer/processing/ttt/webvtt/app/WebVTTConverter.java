package com.redbullmediabase.resourcelayer.processing.ttt.webvtt.app;

import com.redbullmediabase.resourcelayer.processing.ttt.webvtt.builders.CueBuilder;
import com.redbullmediabase.resourcelayer.processing.ttt.webvtt.builders.DocumentBuilder;
import com.redbullmediabase.resourcelayer.processing.ttt.webvtt.model.*;
import com.redbullmediabase.resourcelayer.processing.ttt.webvtt.model.helper.Timestamp;
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
    
    // global state
    private static final Charset encoding = Charset.defaultCharset();
    private static final Model model = new EBUTTDModel();
    
    // per-resource state
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
                .withCues(transformedCues.toArray(new Cue[transformedCues.size()]))
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
        root = JAXBIntrospector.getValue(unmarshaller.unmarshal(reader));
        
        if (root instanceof TimedText) {
            this.rootBinding = (TimedText) root;
        } else {
            throw new JAXBException("Unmarshalled object is of incorrect type");
        }
    }
    
    
    private List<Cue> transformCues() {
        List<Cue> transformed = new ArrayList<>();
        
        for (Division div : rootBinding.getBody().getDiv()) {
            for (Object block : div.getBlockClass()) {
                if (block instanceof Paragraph) {
                    Paragraph p = (Paragraph) block;
                    Cue cue = CueBuilder.create()
                            .withId(p.getId())
                            .withTiming(Timestamp.fromString(p.getBegin()), Timestamp.fromString(p.getEnd()))
                            .withText(
                                    p.getContent().stream()
                                            .map(s -> s.toString())
                                            .reduce("", (a,b) -> a + b)
                            )
                            .build();
                    transformed.add(cue);
                }
            }
        }
        
        return transformed;
    }
}
