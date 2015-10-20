package com.redbullmediabase.resourcelayer.processing.ttt.tt2webvtt;

import java.io.File;
import java.io.IOException;
import java.util.List;
import org.apache.commons.io.FileUtils;

/**
 *
 * @author Michal Samek, (Michal.Samek at at.redbullmediahouse.com)
 */
public class AbstractTestCase {
    
    protected List<String> readFile(String filename) throws IOException {
        final File targetFile = new File(this.getClass().getResource(filename).getFile());
        return FileUtils.readLines(targetFile, "UTF-8");
    }
    
}
