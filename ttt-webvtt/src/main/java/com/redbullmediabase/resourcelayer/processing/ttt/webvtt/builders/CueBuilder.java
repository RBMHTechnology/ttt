package com.redbullmediabase.resourcelayer.processing.ttt.webvtt.builders;

import com.redbullmediabase.resourcelayer.processing.ttt.webvtt.model.Cue;
import com.redbullmediabase.resourcelayer.processing.ttt.webvtt.model.CueAbstractNode;
import com.redbullmediabase.resourcelayer.processing.ttt.webvtt.model.CuePayload;
import com.redbullmediabase.resourcelayer.processing.ttt.webvtt.model.CueSettings.CueSetting;
import com.redbullmediabase.resourcelayer.processing.ttt.webvtt.model.CueTiming;
import com.redbullmediabase.resourcelayer.processing.ttt.webvtt.model.helper.Timestamp;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author Michal Samek, (Michal.Samek at at.redbullmediahouse.com)
 */
public class CueBuilder {

    private String id;
    private CueTiming timing;
    private final Set<CueSetting> settings;
    private CuePayload payload;

    private CueBuilder() {
        settings = new HashSet<>();
    }

    public static CueBuilder create() {
        return new CueBuilder();
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

    public CueBuilder withNode(CuePayload node) {
        payload = node;
        return this;
    }

    public CueBuilder withText(String text) {
        payload = CueTextNodeBuilder.create().withText(text).build();
        return this;
    }
}
