package com.italankin.sample.expandablelist;

import java.util.ArrayList;
import java.util.List;

class ListBuilder {
    private final List<INode> nodes = new ArrayList<>();

    public ListBuilder add(INode node) {
        nodes.add(node);
        return this;
    }

    public ListBuilder add(NodeBuilder builder) {
        nodes.add(builder.build());
        return this;
    }

    public ExpandableList build() {
        return new ExpandableList(nodes);
    }
}

class NodeBuilder {
    private final INode node = new Node();

    public NodeBuilder(boolean expanded) {
        node.setExpanded(expanded);
    }

    public NodeBuilder add(INode child) {
        node.insert(child);
        return this;
    }

    public NodeBuilder add(NodeBuilder builder) {
        return add(builder.build());
    }

    public NodeBuilder add(int childrenCount) {
        while (childrenCount-- > 0) {
            node.insert(new Node());
        }
        return this;
    }

    public INode build() {
        return node;
    }
}