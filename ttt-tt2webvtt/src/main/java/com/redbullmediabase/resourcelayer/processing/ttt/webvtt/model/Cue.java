package com.redbullmediabase.resourcelayer.processing.ttt.webvtt.model;

import com.redbullmediabase.resourcelayer.processing.ttt.webvtt.model.CueSettings.CueSetting;
import java.util.Collections;
import java.util.NoSuchElementException;
import java.util.Set;

/**
 * Class representing cues in the webvtt document.
 *
 * A WebVTT timestamp is always interpreted relative to the current playback position of the media data that the WebVTT
 * file is to be synchronized with.
 *
 * @author Michal Samek, (Michal.Samek at at.redbullmediahouse.com)
 */
public class Cue implements CueOrNote {

    /**
     * A WebVTT cue identifier can be used to reference a specific cue, for example from script or CSS.
     */
    private final String id;

    /**
     * The WebVTT cue timings give the start and end offsets of the WebVTT cue block. Different cues can overlap. Cues
     * are always listed ordered by their start time.
     */
    private final CueTiming timing;

    private final Set<CueSetting> settings;
    private final CuePayload payload;

    public Cue(
            final String id,
            final CueTiming timing,
            final Set<CueSetting> settings,
            final CuePayload payload) {

        assert (timing != null && payload != null);

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
