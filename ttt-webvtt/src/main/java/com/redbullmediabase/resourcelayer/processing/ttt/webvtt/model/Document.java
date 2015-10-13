package com.redbullmediabase.resourcelayer.processing.ttt.webvtt.model;

import com.redbullmediabase.resourcelayer.processing.ttt.webvtt.model.metadata.HeaderMetadata;
import com.redbullmediabase.resourcelayer.processing.ttt.webvtt.model.metadata.Region;
import com.redbullmediabase.resourcelayer.processing.ttt.webvtt.model.cue.Cue;
import com.redbullmediabase.resourcelayer.processing.ttt.webvtt.model.cue.CueOrNote;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 *
 * @author Michal Samek, (Michal.Samek at at.redbullmediahouse.com)
 */
public class Document {

    public static Charset DEFAULT_CHARSET = Charset.forName("UTF-8");
    public static String MIME_TYPE = "text/vtt";
    
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
