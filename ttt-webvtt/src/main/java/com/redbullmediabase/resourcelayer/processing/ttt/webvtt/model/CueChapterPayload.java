package com.redbullmediabase.resourcelayer.processing.ttt.webvtt.model;

/**
 * This class represents nodes that specify chapter titles.
 * 
 * They differ from CueTextNode in escaping the actual payload:
 * <ul>
 *      <li> &amp;amp;
 *      <li> &amp;lt;
 *      <li> &amp;gt;
 *      <li> &amp;lrm;
 *      <li> &amp;rlm;
 *      <li> &amp;nbsp;
 * </ul>
 * 
 * When printing the CueChapterPayload the text needs to be appropriately escaped.
 * 
 * @author Michal Samek, (Michal.Samek at at.redbullmediahouse.com)
 */
public class CueChapterPayload extends CueTextualPayload {
    
    public CueChapterPayload(final String title) {
        super(title);
    }

}
