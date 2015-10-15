package com.redbullmediabase.resourcelayer.processing.ttt.webvtt.output;

import com.redbullmediabase.resourcelayer.processing.ttt.webvtt.model.Document;
import java.io.IOException;
import java.nio.charset.Charset;

/**
 *
 * @author Michal Samek, (Michal.Samek at at.redbullmediahouse.com)
 */
public abstract class AbstractPrinter {

    public static Charset DEFAULT_CHARSET = Charset.forName("UTF-8");
    
    public abstract void print(Document webvtt) throws IOException;
    
}
