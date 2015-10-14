package com.redbullmediabase.resourcelayer.processing.ttt.webvtt.builders;

import com.redbullmediabase.resourcelayer.processing.ttt.webvtt.model.Region;
import com.redbullmediabase.resourcelayer.processing.ttt.webvtt.model.RegionSettings;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author Michal Samek, (Michal.Samek at at.redbullmediahouse.com)
 */
public class RegionBuilder {
    
    private String id;
    private final Set<RegionSettings.RegionSetting> settings;
    
    private RegionBuilder() {
        settings = new HashSet<>();
    }
    
    public static RegionBuilder create() {
        return new RegionBuilder();
    }
    
    public Region build() {
        return new Region(id, settings);
    }
    
    public RegionBuilder withId(String id) {
        this.id = id;
        return this;
    }
    
    /**
     * 
     * @param setting
     * @throws IllegalStateException when adding a setting that was already defined
     * @return 
     */
    public RegionBuilder withSetting(RegionSettings.RegionSetting setting) {
        if (!settings.add(setting)) {
            throw new IllegalStateException("Setting " + setting + " is already defined.");
        }
        return this;
    }
    
}
