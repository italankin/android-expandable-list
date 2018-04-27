package com.italankin.sample.expandablelist;

import java.util.List;

public interface INode {

    /**
     * Expand or collapse node.
     *
     * @param expanded new state
     */
    void setExpanded(boolean expanded);

    /**
     * Add child to node.
     *
     * @param child child node
     */
    int insert(INode child);

    /**
     * Add child to node.
     *
     * @param index index at which child is to be insterted
     * @param child child node
     */
    int insert(int index, INode child);

    /**
     * Remove child from node.
     *
     * @param child child node
     */
    int delete(INode child);

    /**
     * Remove all child nodes.
     */
    void clear();

    /**
     * Get current node state.
     *
     * @return current state
     */
    boolean isExpanded();

    /**
     * Set parent for this node.
     * <br>
     * <b>This should only be called by parents and only on their direct children.</b>
     *
     * @param parent new parent for this node
     */
    void setParent(INode parent);

    /**
     * @return the parent of this node. Can be {@code null} if node has no parent.
     */
    INode getParent();

    /**
     * Get children attached to this node.
     *
     * @return attached children
     */
    List<? extends INode> getChildren();

    /**
     * Add an observer to this node.
     *
     * @param observer observer
     */
    void addObserver(Observer observer);

    /**
     * Remove an observer to this node.
     *
     * @param observer observer
     */
    void removeObserver(Observer observer);

    /**
     * Observer for events.
     */
    interface Observer {
        /**
         * Called when node is inserted to it's {@link INode#getParent() parent}.
         *
         * @param child inserted child
         */
        void onInserted(INode child);

        /**
         * Called when {@link INode#isExpanded() state} of the node is changed.
         *
         * @param child changed node
         */
        void onChanged(INode child);

        /**
         * Called when {@code child} is removed from {@code oldParent}.
         *
         * @param oldParent old parent
         * @param child     removed child
         */
        void onDeleted(INode oldParent, INode child);
    }
}
