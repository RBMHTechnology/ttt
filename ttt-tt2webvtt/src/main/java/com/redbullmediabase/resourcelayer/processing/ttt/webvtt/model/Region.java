package com.redbullmediabase.resourcelayer.processing.ttt.webvtt.model;

import com.redbullmediabase.resourcelayer.processing.ttt.webvtt.model.RegionSettings.RegionSetting;
import java.util.Collections;
import java.util.NoSuchElementException;
import java.util.Set;

/**
 * Represents regions.
 * 
 * @author Michal Samek, (Michal.Samek at at.redbullmediahouse.com)
 */
public class Region implements HeaderMetadata {

    /**
     * The WebVTT region identifier setting gives a name to the region so it can be referenced by the cues that belong
     * to the region.
     */
    private final String id;

    /**
     * The WebVTT region setting list gives configuration options regarding the dimensions, positioning and anchoring of
     * the region. For example, it allows a group of cues within a region to be anchored in the center of the region and
     * the center of the video viewport. In this example, when the font size grows, the region grows uniformly in all
     * directions from the center.
     */
    private final Set<RegionSetting> settings;

    public Region(final String id, final Set<RegionSetting> settings) {
        this.id = id;
        this.settings = settings != null
                ? Collections.unmodifiableSet(settings)
                : Collections.EMPTY_SET;
    }

    public String getId() {
        return id;
    }

    public Set<RegionSetting> getSettings() {
        return settings;
    }
    
    public <T extends RegionSetting> T getSetting(Class<T> setting) {
        for (RegionSetting s : settings) {
            if (s.getClass().equals(setting)) {
                return (T) s;
            }
        }
        return null;
    }

}
