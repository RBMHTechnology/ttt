package com.redbullmediabase.resourcelayer.processing.ttt.webvtt.builders;

import com.redbullmediabase.resourcelayer.processing.ttt.webvtt.model.Cue;
import com.redbullmediabase.resourcelayer.processing.ttt.webvtt.model.CuePayload;
import com.redbullmediabase.resourcelayer.processing.ttt.webvtt.model.CueSettings.CueSetting;
import com.redbullmediabase.resourcelayer.processing.ttt.webvtt.model.CueTiming;
import com.redbullmediabase.resourcelayer.processing.ttt.webvtt.model.helper.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 *
 * @author Michal Samek, (Michal.Samek at at.redbullmediahouse.com)
 */
public class CueBuilder {

    private String id;
    private CueTiming timing;
    private Set<CueSetting> settings;
    private List<CuePayload> payload;

    private CueBuilder() {
        settings = new HashSet<>();
        payload = new ArrayList<>();
    }

    public static CueBuilder create() {
        return new CueBuilder();
    }
    
    public static CueBuilder create(Cue other) {
        CueBuilder builder = create();
        builder.id = other.getId();
        builder.timing = other.getTiming();
        builder.settings = new HashSet<>(other.getSettings());
        builder.payload = new ArrayList<>(other.getPayload());
        return builder;
    }

    public Cue build() {
        return new Cue(id, timing, settings, payload);
    }

    public CueBuilder withId(String cueId) {
        this.id = cueId;
        return this;
    }

    public CueBuilder withTiming(CueTiming timing) {
        this.timing = timing;
        return this;
    }

    public CueBuilder withTiming(Timestamp start, Timestamp end) {
        this.timing = new CueTiming(start, end);
        return this;
    }

    /**
     *
     * @param setting
     * @throws IllegalStateException when adding a setting that was already defined
     * @return
     */
    public CueBuilder withSetting(CueSetting setting) {
        if (!settings.add(setting)) {
            throw new IllegalStateException("Setting " + setting + " is already defined.");
        }

        return this;
    }
    
    public CueBuilder withSettings(Collection<CueSetting> settings) {
        if (!settings.addAll(settings)) {
            throw new IllegalStateException("Setting " + settings + " is already defined.");
        }

        return this;
    }

    public CueBuilder withNode(CuePayload node) {
        payload.add(node);
        return this;
    }
    
    public CueBuilder withNodes(Collection<CuePayload> nodes) {
        payload.addAll(nodes);
        return this;
    }

}
