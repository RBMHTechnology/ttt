package com.redbullmediabase.resourcelayer.processing.ttt.webvtt.builders;

import com.redbullmediabase.resourcelayer.processing.ttt.webvtt.model.CueAbstractNode;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Michal Samek, (Michal.Samek at at.redbullmediahouse.com)
 */
public abstract class AbstractNodeBuilder<T extends CueAbstractNode, B extends AbstractNodeBuilder<T, B>> {
    protected final B thisObject;
    protected final List<String> classes;
    
    //Workaround to get subclass builder
    protected abstract B getThis();
    
    public AbstractNodeBuilder() {
            thisObject = getThis();
            classes = new ArrayList<>();
        }
    
    public B withClass(String claz) {
        this.classes.add(claz);
        return thisObject;
    }
}
