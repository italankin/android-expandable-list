package com.italankin.sample.expandablelist;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * Base implementation of list node.
 */
public class Node implements INode, INode.Observer, Iterable<INode> {

    protected INode parent = null;
    protected boolean expanded = false;
    protected final List<INode> children = new LinkedList<>();

    private final List<Observer> observers = new ArrayList<>(0);

    @Override
    public void setExpanded(boolean expanded) {
        if (this.expanded != expanded) {
            this.expanded = expanded;
            onChanged(this);
        }
    }

    @Override
    public int insert(INode child) {
        return insert(children.size(), child);
    }

    @Override
    public int insert(int index, INode child) {
        if (child.getParent() != null) {
            throw new IllegalStateException(child + " already has parent");
        }
        children.add(index, child);
        child.setParent(this);
        child.addObserver(this);
        int inserted = countVisible(child);
        onInserted(child);
        return inserted;
    }

    public int delete(int index) {
        return delete(children.get(index));
    }

    @Override
    public int delete(INode child) {
        if (child.getParent() != this) {
            throw new IllegalStateException(child + " is not a member of this node");
        }
        if (!children.remove(child)) {
            return 0;
        }
        child.setParent(null);
        child.removeObserver(this);
        int removed = countVisible(child);
        onDeleted(this, child);
        return removed;
    }

    @Override
    public void clear() {
        if (!children.isEmpty()) {
            for (int i = children.size() - 1; i >= 0; i--) {
                onDeleted(this, children.remove(i));
            }
        }
    }

    /**
     * Get index of child in parent.
     *
     * @param child node
     * @return index of {@code child}, or {@code -1} if not found in parent's list
     */
    public int indexOf(INode child) {
        return children.indexOf(child);
    }

    @Override
    public boolean isExpanded() {
        return expanded;
    }

    @Override
    public void setParent(INode parent) {
        this.parent = parent;
    }

    @Override
    public INode getParent() {
        return parent;
    }

    @Override
    public List<? extends INode> getChildren() {
        return Collections.unmodifiableList(children);
    }

    @Override
    public void addObserver(Observer observer) {
        if (observers.indexOf(observer) == -1) {
            observers.add(observers.size(), observer);
        }
    }

    @Override
    public void removeObserver(Observer observer) {
        observers.remove(observer);
    }

    @Override
    public Iterator<INode> iterator() {
        return children.iterator();
    }

    ///////////////////////////////////////////////////////////////////////////
    // Internal
    ///////////////////////////////////////////////////////////////////////////

    /**
     * Count visible child nodes of a given {@code node}.
     *
     * @param node node
     * @return number of nodes, respecting {@link INode#isExpanded() expanded} state
     */
    private int countVisible(INode node) {
        int count = 1;
        if (!node.isExpanded()) {
            return count;
        }
        for (INode child : node.getChildren()) {
            count += countVisible(child);
        }
        return count;
    }

    ///////////////////////////////////////////////////////////////////////////
    // Observer
    ///////////////////////////////////////////////////////////////////////////

    @Override
    public void onInserted(INode child) {
        for (int i = observers.size() - 1; i >= 0; i--) {
            observers.get(i).onInserted(child);
        }
    }

    @Override
    public void onChanged(INode node) {
        for (int i = observers.size() - 1; i >= 0; i--) {
            observers.get(i).onChanged(node);
        }
    }

    @Override
    public void onDeleted(INode fromParent, INode child) {
        for (int i = observers.size() - 1; i >= 0; i--) {
            observers.get(i).onDeleted(fromParent, child);
        }
    }
}
