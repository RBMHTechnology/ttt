package com.redbullmediabase.resourcelayer.processing.ttt.webvtt.model.cue;

import com.redbullmediabase.resourcelayer.processing.ttt.webvtt.model.helper.Timestamp;
import java.util.List;

/**
 * The timestamp must be greater that the cue's start timestamp, greater than any previous timestamp in the cue payload,
 * and less than the cue's end timestamp. The active text is the text between the timestamp and the next timestamp or to
 * the end of the payload if there is not another timestamp in the payload. Any text before the active text in the
 * payload is previous text . Any text beyond the active text is future text . This enables karaoke style captions.
 *
 * @author Michal Samek, (Michal.Samek at at.redbullmediahouse.com)
 */
public class CueTimestampNode extends CueAbstractNode {

    private final Timestamp timestamp;

    public CueTimestampNode(Timestamp timestamp, List<CueNode> children) {
        super(children);
        this.timestamp = timestamp;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    @Override
    public boolean requiresAnnotation() {
        return false;
    }
}
