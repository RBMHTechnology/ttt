package com.redbullmediabase.resourcelayer.processing.ttt.webvtt.model.cue;

import com.redbullmediabase.resourcelayer.processing.ttt.webvtt.model.cue.CueSettings.CueSetting;
import java.util.Collections;
import java.util.NoSuchElementException;
import java.util.Set;

/**
 *
 * @author Michal Samek, (Michal.Samek at at.redbullmediahouse.com)
 */
public class Cue implements CueOrNote {

    private final String id;
    private final CueTiming timing;
    
    private final Set<CueSetting> settings;
    private final CuePayload payload;

    public Cue(
            final String id,
            final CueTiming timing,
            final Set<CueSetting> settings,
            final CuePayload payload) {
        this.id = id;
        this.timing = timing;
        this.settings = settings != null
                ? Collections.unmodifiableSet(settings)
                : Collections.EMPTY_SET;
        this.payload = payload;
    }

    public String getId() {
        return id;
    }

    public CueTiming getTiming() {
        return timing;
    }

    public Set<CueSetting> getSettings() {
        return settings;
    }
    
    public <T extends CueSetting> T getSetting(Class<T> setting) {
        T ret;
        try {
        ret = settings.stream()
                .filter(s -> s.getClass().equals(setting))
                .map(s -> setting.cast(s))
                .findFirst().get();
        } catch (NoSuchElementException ex) {
            ret = null;
        }
        return ret;
    }

    public CuePayload getPayload() {
        return payload;
    }
}
