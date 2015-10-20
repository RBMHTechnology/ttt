package com.redbullmediabase.resourcelayer.processing.ttt.webvtt.model;

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
        return metadataBlock.stream()
                .filter(m -> m.getClass().equals(Region.class))
                .map(m -> Region.class.cast(m))
                .collect(Collectors.toList());
    }
    
    public List<Cue> getCues() {
        return cueBlock.stream()
                .filter(c -> c.getClass().equals(Cue.class))
                .map(c -> Cue.class.cast(c))
                .collect(Collectors.toList());
    }
}
