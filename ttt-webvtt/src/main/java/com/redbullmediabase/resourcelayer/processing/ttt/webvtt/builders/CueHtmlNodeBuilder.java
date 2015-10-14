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
public class CueHtmlNodeBuilder extends AbstractNodeBuilder<CueHtmlNode, CueHtmlNodeBuilder> {

    private NodeType nodeType;
    private String nodeAnnotation;
    private final List<CuePayload> children;

    @Override
    protected CueHtmlNodeBuilder getThis() {
        return this;
    }

    protected CueHtmlNodeBuilder() {
        children = new ArrayList<>();
    }

    public static CueHtmlNodeBuilder create() {
        return new CueHtmlNodeBuilder();
    }

    public CueHtmlNode build() {
        return new CueHtmlNode(nodeType, nodeAnnotation, children, classes);
    }

    public CueHtmlNodeBuilder withType(NodeType type) {
        this.nodeType = type;
        return thisObject;
    }

    public CueHtmlNodeBuilder withAnnotation(String annotation) {
        this.nodeAnnotation = annotation;
        return thisObject;
    }

    public CueHtmlNodeBuilder withChild(CuePayload child) {
        this.children.add(child);
        return thisObject;
    }

    public CueHtmlNodeBuilder withChildren(Collection<CuePayload> children) {
        this.children.addAll(children);
        return thisObject;
    }

    public CueHtmlNodeBuilder withText(String text) {
        this.children.add(CueTextNodeBuilder.create().withText(text).build());
        return thisObject;
    }
}
