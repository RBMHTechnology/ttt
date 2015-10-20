package com.redbullmediabase.resourcelayer.processing.ttt.webvtt.builders;

import com.redbullmediabase.resourcelayer.processing.ttt.webvtt.model.CueHtmlNode;
import com.redbullmediabase.resourcelayer.processing.ttt.webvtt.model.CueHtmlNode.NodeType;
import com.redbullmediabase.resourcelayer.processing.ttt.webvtt.model.CuePayload;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 *
 * @author Michal Samek, (Michal.Samek at at.redbullmediahouse.com)
 */
public class CueHtmlNodeBuilder {

    private NodeType nodeType;
    private String nodeAnnotation;
    private final List<CuePayload> children;
    private final List<String> classes;

    protected CueHtmlNodeBuilder() {
        children = new ArrayList<>();
        classes = new ArrayList<>();
    }

    public static CueHtmlNodeBuilder create() {
        return new CueHtmlNodeBuilder();
    }

    public CueHtmlNode build() {
        return new CueHtmlNode(nodeType, nodeAnnotation, children, classes);
    }

    public CueHtmlNodeBuilder withType(NodeType type) {
        this.nodeType = type;
        return this;
    }

    public CueHtmlNodeBuilder withAnnotation(String annotation) {
        this.nodeAnnotation = annotation;
        return this;
    }

    public CueHtmlNodeBuilder withChild(CuePayload child) {
        this.children.add(child);
        return this;
    }

    public CueHtmlNodeBuilder withChildren(Collection<CuePayload> children) {
        this.children.addAll(children);
        return this;
    }
    
    public CueHtmlNodeBuilder withClass(String claz) {
        this.classes.add(claz);
        return this;
    }

}
