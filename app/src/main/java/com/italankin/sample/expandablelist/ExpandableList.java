package com.italankin.sample.expandablelist;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * Utility structure to represent expandable list.
 */
public class ExpandableList<T> implements Iterable<ExpandableList.Node<T>> {

    /**
     * List of parent nodes attached to this list
     */
    private final List<Node<T>> directNodes = new LinkedList<>();
    /**
     * All nodes in the list (actual list)
     */
    private final List<Node<T>> nodes = new LinkedList<>();

    private final Observer<T> nodeObserver = new Observer<T>() {
        @Override
        public void onChanged(Node<T> node) {
            invalidate();
            notifyObservers(node);
        }
    };
    private final List<Observer<T>> observers = new ArrayList<>(0);

    public ExpandableList() {
    }

    public ExpandableList(Collection<Node<T>> nodes) {
        directNodes.addAll(nodes);
        invalidate();
    }

    /**
     * Add parent node to this tree.
     *
     * @param node node
     * @return count of nodes added to current list
     */
    public int add(Node<T> node) {
        return add(directNodes.size(), node);
    }

    /**
     * Add parent node to this tree.
     *
     * @param index index at which the node is to be inserted
     * @param node  node
     * @return count of nodes added to current list
     */
    public int add(int index, Node<T> node) {
        if (directNodes.indexOf(node) != -1) {
            return 0;
        }
        try {
            directNodes.add(index, node);
            node.addObserver(nodeObserver);
            return addInternal(node);
        } finally {
            notifyObservers(node);
        }
    }

    /**
     * Remove parent node from this tree.
     *
     * @param node node
     * @return count of nodes removed from the current list
     */
    public int remove(Node<T> node) {
        if (!directNodes.remove(node)) {
            return 0;
        }
        try {
            node.removeObserver(nodeObserver);
            return removeInternal(node);
        } finally {
            notifyObservers(node);
        }
    }

    /**
     * Expand parent node.
     *
     * @param node node
     * @return count of nodes added to the current list (returns {@code 0} if parent was already expanded)
     */
    public int expand(Node<T> node) {
        if (!node.isExpanded()) {
            try {
                int old = nodes.size();
                node.setExpanded(true);
                return nodes.size() - old;
            } finally {
                notifyObservers(node);
            }
        }
        return 0;
    }

    /**
     * Collapse parent node.
     *
     * @param node node
     * @return count of nodes removed from the current list (returns {@code 0} if parent was already collapsed)
     */
    public int collapse(Node<T> node) {
        if (node.isExpanded()) {
            try {
                int old = nodes.size();
                node.setExpanded(false);
                return old - nodes.size();
            } finally {
                notifyObservers(node);
            }
        }
        return 0;
    }

    /**
     * Collapse all parent nodes.
     */
    public void collapseAll() {
        expandNodes(false);
    }

    /**
     * Expand all parent nodes.
     */
    public void expandAll() {
        expandNodes(true);
    }

    /**
     * Retrieve a payload for the node at {@code index}.
     *
     * @param index index of node to get payload from
     * @return payload of node at {@code index}
     */
    public T getPayload(int index) {
        return getNode(index).getPayload();
    }

    /**
     * Get list node at {@code index}.
     *
     * @param index [0, {@link #listSize()})
     * @return node at {@code index}
     */
    public Node<T> getNode(int index) {
        return nodes.get(index);
    }

    /**
     * Get parent at {@code index}.
     *
     * @param index [0; {@link #parentsSize()})
     * @return parent at {@code index}
     */
    public Node<T> getParent(int index) {
        return directNodes.get(index);
    }

    /**
     * Index of parent node in parent's list.
     *
     * @param parent parent
     * @return index of parent
     * @see #parentsSize()
     * @see #getParent(int)
     */
    public int indexOfParent(Node<T> parent) {
        return directNodes.indexOf(parent);
    }

    /**
     * Get index of node within list.
     *
     * @param node node
     * @return index of node
     * @see #listSize()
     * @see #getNode(int)
     */
    public int indexOf(Node<T> node) {
        return nodes.indexOf(node);
    }

    /**
     * @return total number of parents in this tree
     */
    public int parentsSize() {
        return directNodes.size();
    }

    /**
     * @return total number of list nodes in current state
     */
    public int listSize() {
        return nodes.size();
    }

    /**
     * @return total number of all nodes (including collapsed)
     */
    public int absoluteSize() {
        return computeAbsoluteSize();
    }

    /**
     * Invalidate tree to recalculate list content and positions.
     * This can be used if some of parents have been changed manually.
     */
    public void invalidate() {
        nodes.clear();
        for (Node<T> parent : directNodes) {
            addInternal(parent);
        }
    }

    @Override
    public Iterator<Node<T>> iterator() {
        return nodes.iterator();
    }

    ///////////////////////////////////////////////////////////////////////////
    // Observers
    ///////////////////////////////////////////////////////////////////////////

    public void addObserver(Observer<T> observer) {
        if (observers.indexOf(observer) == -1) {
            observers.add(observers.size(), observer);
        }
    }

    public void removeObserver(Observer<T> observer) {
        observers.remove(observer);
    }

    public interface Observer<T> {
        void onChanged(Node<T> node);
    }

    ///////////////////////////////////////////////////////////////////////////
    // Internal
    ///////////////////////////////////////////////////////////////////////////

    private int addInternal(Node<T> node) {
        nodes.add(node);
        int count = 1;
        if (!node.expanded) {
            return count;
        }
        for (Node<T> child : node.children) {
            count += addInternal(child);
        }
        return count;
    }

    private int removeInternal(Node<T> node) {
        nodes.remove(node);
        int count = 1;
        if (!node.expanded) {
            return count;
        }
        for (Node<T> child : node.children) {
            count += removeInternal(child);
        }
        return count;
    }

    private int computeAbsoluteSize() {
        int count = 0;
        for (Node<T> node : directNodes) {
            count += node.internalSize();
        }
        return count;
    }

    private void notifyObservers(Node<T> node) {
        for (int i = observers.size() - 1; i >= 0; i--) {
            observers.get(i).onChanged(node);
        }
    }

    private void expandNodes(boolean expanded) {
        for (Node<T> node : directNodes) {
            node.setExpanded(expanded);
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    // Node
    ///////////////////////////////////////////////////////////////////////////

    /**
     * List node
     */
    public static class Node<T> implements Observer<T> {

        private T payload;

        private Node<T> parent = null;
        private boolean expanded = false;
        private final List<Node<T>> children = new LinkedList<>();

        private final List<Observer<T>> observers = new ArrayList<>(0);

        /**
         * Construct a new node with empty payload.
         */
        public Node() {
        }

        /**
         * Construct a new node with given payload.
         *
         * @param payload payload
         */
        public Node(T payload) {
            setPayload(payload);
        }

        /**
         * Expand or collapse node.
         *
         * @param expanded new state
         */
        public void setExpanded(boolean expanded) {
            if (this.expanded != expanded) {
                this.expanded = expanded;
                if (!children.isEmpty()) {
                    onChanged(this);
                }
            }
        }

        /**
         * Detach this node from the parent.
         */
        public boolean detach() {
            return parent != null && parent.remove(this);
        }

        /**
         * Add child to node.
         * If node is attached to a tree, it's required to call {@link #invalidate()} to update tree state.
         *
         * @param child child node
         */
        public void add(Node<T> child) {
            add(children.size(), child);
        }


        /**
         * Add child to node.
         * If node is attached to a tree, it's required to call {@link #invalidate()} to update tree state.
         *
         * @param index index at which child is to be insterted
         * @param child child node
         */
        public void add(int index, Node<T> child) {
            if (child.parent != this) {
                children.add(index, child);
                child.parent = this;
                child.addObserver(this);
                onChanged(this);
            }
        }

        /**
         * Remove child from node.
         *
         * @param child child node
         */
        private boolean remove(Node<T> child) {
            if (removeInternal(child)) {
                onChanged(this);
                return true;
            }
            return false;
        }

        /**
         * Remove all child nodes.
         */
        public void removeChildren() {
            if (!children.isEmpty()) {
                for (int i = children.size() - 1; i >= 0; i--) {
                    removeInternal(children.get(i));
                }
                onChanged(this);
            }
        }

        /**
         * Get index of child in parent.
         *
         * @param child node
         * @return index of {@code child}, or {@code -1} if not found in parent's list
         */
        public int indexOf(Node<T> child) {
            if (child == this || child.parent != this) {
                return -1;
            }
            return children.indexOf(child);
        }

        /**
         * Get current node state.
         *
         * @return current state
         */
        public boolean isExpanded() {
            return expanded;
        }

        /**
         * @return the parent of this node. Can be {@link null} if node has no parent.
         */
        public Node<T> getParent() {
            return parent;
        }

        /**
         * Set node payload.
         *
         * @param payload new payload
         */
        public void setPayload(T payload) {
            this.payload = payload;
        }

        /**
         * Get node payload.
         *
         * @return node payload
         */
        public T getPayload() {
            return payload;
        }

        public int getParentIndex() {
            return parent != null ? parent.indexOf(this) : -1;
        }

        /**
         * Get child node by index.
         *
         * @param index index of node to get
         * @return node at {@code index}
         */
        public Node<T> getChild(int index) {
            return children.get(index);
        }

        /**
         * @return count of child nodes
         */
        public int getChildCount() {
            return children.size();
        }

        /**
         * @return internal size of the node, including nested
         */
        public int internalSize() {
            int count = 1;
            for (Node<T> child : children) {
                count += child.internalSize();
            }
            return count;
        }

        ///////////////////////////////////////////////////////////////////////////
        // Internal
        ///////////////////////////////////////////////////////////////////////////

        private boolean removeInternal(Node<T> child) {
            if (children.remove(child)) {
                child.removeObserver(this);
                child.parent = null;
                return true;
            }
            return false;
        }

        ///////////////////////////////////////////////////////////////////////////
        // NodeObserver
        ///////////////////////////////////////////////////////////////////////////

        public void addObserver(Observer<T> observer) {
            if (observers.indexOf(observer) == -1) {
                observers.add(observers.size(), observer);
            }
        }

        public void removeObserver(Observer<T> observer) {
            observers.remove(observer);
        }

        @Override
        public void onChanged(Node<T> node) {
            for (int i = observers.size() - 1; i >= 0; i--) {
                observers.get(i).onChanged(node);
            }
        }
    }

}