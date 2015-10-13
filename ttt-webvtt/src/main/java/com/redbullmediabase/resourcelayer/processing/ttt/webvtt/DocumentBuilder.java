package com.redbullmediabase.resourcelayer.processing.ttt.webvtt;

import com.redbullmediabase.resourcelayer.processing.ttt.webvtt.model.Document;
import com.redbullmediabase.resourcelayer.processing.ttt.webvtt.model.metadata.HeaderMetadata;
import com.redbullmediabase.resourcelayer.processing.ttt.webvtt.model.Note;
import com.redbullmediabase.resourcelayer.processing.ttt.webvtt.model.metadata.Region;
import com.redbullmediabase.resourcelayer.processing.ttt.webvtt.model.cue.Cue;
import com.redbullmediabase.resourcelayer.processing.ttt.webvtt.model.cue.CueOrNote;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author Michal Samek, (Michal.Samek at at.redbullmediahouse.com)
 */
public class DocumentBuilder {

    private final List<HeaderMetadata> metadata;
    private final List<CueOrNote> cues;

    private DocumentBuilder() {
        metadata = new ArrayList<>();
        cues = new ArrayList<>();
    }

    public static DocumentBuilder create() {
        return new DocumentBuilder();
    }
    
    public Document build() {
        return new Document(metadata, cues);
    }

    public DocumentBuilder withCue(Cue cue) {
        cues.add(cue);
        return this;
    }
    
    public DocumentBuilder withCues(Cue[] cues) {
        this.cues.addAll(Arrays.asList(cues));
        return this;
    }

    public DocumentBuilder withNote(Note note) {
        cues.add(note);
        return this;
    }

    public DocumentBuilder withRegion(Region region) {
        metadata.add(region);
        return this;
    }
}
