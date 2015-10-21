package com.redbullmediabase.resourcelayer.processing.ttt.webvtt.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Represent whole WebVTT document. This is root element in the WebVTT model.
 * 
 * @author Michal Samek, (Michal.Samek at at.redbullmediahouse.com)
 */
public class Document {

    private final List<HeaderMetadata> metadataBlock;
    private final List<CueOrNote> cueBlock;
    
    public Document(final List<HeaderMetadata> metadata, final List<CueOrNote> cues) {
        metadataBlock = metadata != null
                ? Collections.unmodifiableList(metadata)
                : Collections.EMPTY_LIST;
        cueBlock = cues != null
                ? Collections.unmodifiableList(cues)
                : Collections.EMPTY_LIST;
    }
    
    public List<HeaderMetadata> getMetadataBlock() {
        return metadataBlock;
    }
    
    public List<CueOrNote> getCueBlock() {
        return cueBlock;
    }
    
    public List<Region> getRegions() {
        List<Region> regions = new ArrayList<>();
        for (HeaderMetadata m : metadataBlock) {
            if (m instanceof Region) {
                regions.add((Region) m);
            }
        }
        return regions;
    }
    
    public List<Cue> getCues() {
        List<Cue> cues = new ArrayList<>();
        for (CueOrNote cn : cueBlock) {
            if (cn instanceof Cue) {
                cues.add((Cue) cn);
            }
        }
        return cues;
    }
}
