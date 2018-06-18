package com.italankin.sample.expandablelist;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class ExpandableListTest {

    static INode last(ExpandableList list) {
        return list.size() > 0 ? list.get(list.size() - 1) : null;
    }

    @Test
    public void get_vs_getParent() {
        ExpandableList list = new ListBuilder()
                .add(new NodeBuilder(true)
                        .add(3))
                .add(new NodeBuilder(true)
                        .add(7))
                .build();
        assertEquals(3, list.get(0).getChildren().size());
        assertEquals(3, list.getChild(0).getChildren().size());
        assertEquals(0, list.get(1).getChildren().size());
        assertEquals(7, list.getChild(1).getChildren().size());
    }

    @Test
    public void size_vs_getChildCount() {
        ExpandableList list = new ListBuilder()
                .add(new NodeBuilder(true)
                        .add(5))
                .build();
        assertEquals(1, list.getChildCount());
        assertEquals(6, list.size());
    }

    @Test
    public void insert_single() {
        ExpandableList list = new ExpandableList();
        assertEquals(1, list.insert(new Node()));
        assertEquals(1, list.size());
        assertEquals(1, list.absoluteSize());

        assertEquals(1, list.insert(new Node()));
        assertEquals(2, list.size());
        assertEquals(2, list.absoluteSize());
    }

    @Test
    public void insert_nestedNode_collapsed() {
        ExpandableList list = new ExpandableList();
        INode node = new Node();
        node.insert(new Node());
        assertEquals(1, list.insert(node));
        assertEquals(1, list.size());
        assertEquals(2, list.absoluteSize());
    }

    @Test
    public void insert_nestedNode_expanded() {
        ExpandableList list = new ExpandableList();
        INode node = new Node();
        node.insert(new Node());
        node.setExpanded(true);
        assertEquals(2, list.insert(node));
        assertEquals(2, list.size());
        assertEquals(2, list.absoluteSize());
    }

    @Test
    public void insert_nestedNode_expandAfterInsertion() {
        ExpandableList list = new ExpandableList();
        INode node = new Node();
        node.insert(new Node());
        assertEquals(1, list.insert(node));
        assertEquals(1, list.size());
        assertEquals(2, list.absoluteSize());
        node.setExpanded(true);
        assertEquals(2, list.size());
    }

    @Test
    public void insert_nestedNode_collapseAfterInsertion() {
        ExpandableList list = new ExpandableList();
        INode node = new Node();
        node.insert(new Node());
        node.setExpanded(true);
        assertEquals(2, list.insert(node));
        assertEquals(2, list.size());
        assertEquals(2, list.absoluteSize());
        node.setExpanded(false);
        assertEquals(1, list.size());
    }

    @Test(expected = IllegalStateException.class)
    public void insert_childWithParent() {
        INode node = new Node();
        INode parent = new Node();
        parent.insert(node);
        new ExpandableList().insert(node);
    }

    @Test
    public void nested_expand() {
        ExpandableList list = new ListBuilder()
                .add(new NodeBuilder(false)
                        .add(2))
                .build();
        assertEquals(3, list.absoluteSize());
        INode node2 = list.getChild(0).getChildren().get(0);
        node2.insert(new Node());
        node2.insert(new Node());
        assertEquals(1, list.size());
        assertEquals(5, list.absoluteSize());
        INode node3 = list.getChild(0)
                .getChildren().get(0)
                .getChildren().get(1);
        node3.insert(new Node());
        assertEquals(1, list.size());
        assertEquals(6, list.absoluteSize());
        node2.setExpanded(true);
        assertEquals(1, list.size());
        list.expandAll();
        assertEquals(5, list.size());
    }

    @Test
    public void nested_collapse() {
        ExpandableList list = new ListBuilder()
                .add(new NodeBuilder(true)
                        .add(2))
                .build();
        assertEquals(3, list.absoluteSize());
        INode node2 = list.getChild(0).getChildren().get(0);
        node2.insert(new Node());
        node2.insert(new Node());
        node2.setExpanded(true);
        assertEquals(5, list.size());
        assertEquals(5, list.absoluteSize());
        INode node3 = list.getChild(0)
                .getChildren().get(0)
                .getChildren().get(1);
        node3.insert(new Node());
        assertEquals(5, list.size());
        assertEquals(6, list.absoluteSize());
        list.collapseAll();
        assertEquals(1, list.size());
    }

    @Test
    public void prettyPrint() {
        ExpandableList list = new ListBuilder()
                .add(new NodeBuilder(false)
                        .add(2))
                .add(new NodeBuilder(false)
                        .add(3)
                        .add(new NodeBuilder(true)
                                .add(new NodeBuilder(false)
                                        .add(3)))
                        .add(1))
                .add(new Node())
                .add(new NodeBuilder(false)
                        .add(1)
                        .add(new NodeBuilder(true)
                                .add(3))
                        .add(3)
                        .add(new NodeBuilder(false)
                                .add(new NodeBuilder(true)
                                        .add(new NodeBuilder(false)
                                                .add(new Node())))))
                .add(new Node())
                .build();
        String s = list.toString();
        assertNotNull(s);
    }

    @Test
    public void delete_collapsed() {
        ExpandableList list = new ListBuilder()
                .add(new NodeBuilder(false)
                        .add(1))
                .add(new NodeBuilder(false)
                        .add(2))
                .add(new Node())
                .build();
        assertEquals(1, list.delete(0));
        assertEquals(2, list.size());
    }

    @Test
    public void delete_expanded() {
        ExpandableList list = new ListBuilder()
                .add(new NodeBuilder(true)
                        .add(1))
                .add(new NodeBuilder(true)
                        .add(2))
                .add(new NodeBuilder(true))
                .build();
        assertEquals(6, list.size());
        assertEquals(3, list.delete(1));
        assertEquals(3, list.size());
    }

    @Test
    public void delete_empty() {
        ExpandableList list = new ListBuilder()
                .add(new NodeBuilder(true))
                .build();
        assertEquals(1, list.size());
        assertEquals(1, list.delete(0));
        assertTrue(list.isEmpty());
    }

    @Test
    public void nodeClear() {
        ExpandableList list = new ListBuilder()
                .add(new NodeBuilder(true)
                        .add(4)
                        .add(new NodeBuilder(true)
                                .add(2)))
                .build();
        assertEquals(8, list.size());
        INode node = list.getChild(0).getChildren().get(4);
        node.clear();
        assertEquals(6, list.size());
        list.getChild(0).clear();
        assertEquals(1, list.size());
        assertEquals(1, list.absoluteSize());
    }

    @Test
    public void nodeDelete() {
        ExpandableList list = new ListBuilder()
                .add(new NodeBuilder(true)
                        .add(new NodeBuilder(true)
                                .add(1)))
                .build();
        INode node3 = list.getChild(0).getChildren().get(0).getChildren().get(0);
        node3.getParent().delete(node3);
        assertEquals(2, list.absoluteSize());
    }

    @Test
    public void expand() {
        ExpandableList list = new ListBuilder()
                .add(new NodeBuilder(false)
                        .add(2))
                .add(new NodeBuilder(false)
                        .add(1))
                .add(new Node())
                .build();
        assertEquals(6, list.absoluteSize());
        // expand first node
        assertEquals(2, list.expand(list.getChild(0)));
        assertEquals(5, list.size());
        // expand already expanded node
        assertEquals(0, list.expand(list.getChild(0)));
        // expand empty node
        assertEquals(0, list.expand(last(list)));
    }

    @Test(expected = IllegalArgumentException.class)
    public void expand_nonMember() {
        ExpandableList list = new ExpandableList();
        list.expand(new Node());
    }

    @Test
    public void collapse() {
        ExpandableList list = new ListBuilder()
                .add(new NodeBuilder(true)
                        .add(2))
                .add(new NodeBuilder(true)
                        .add(1))
                .add(new NodeBuilder(true))
                .build();
        assertEquals(6, list.absoluteSize());
        // collapse first node
        assertEquals(2, list.collapse(list.getChild(0)));
        assertEquals(4, list.size());
        // collapse already collapsed node
        assertEquals(0, list.collapse(list.getChild(0)));
        // collapse empty node
        assertEquals(0, list.collapse(last(list)));
    }

    @Test(expected = IllegalArgumentException.class)
    public void collapse_nonMember() {
        ExpandableList list = new ExpandableList();
        list.collapse(new Node());
    }

    @Test
    public void isChild() {
        INode node1 = new Node();
        INode node2 = new Node();
        node1.insert(node2);
        Node node3 = new Node();
        node2.insert(node3);
        ExpandableList list = new ExpandableList();
        assertFalse(list.isChild(node3));
        list.insert(node1);
        assertTrue(list.isChild(node3));
    }

    @Test
    public void expandAll_fromCollapsed() {
        ExpandableList list = new ListBuilder()
                .add(new NodeBuilder(false)
                        .add(1))
                .add(new Node())
                .add(new NodeBuilder(false)
                        .add(3))
                .build();
        assertEquals(3, list.size());
        assertEquals(7, list.absoluteSize());
        list.expandAll();
        assertEquals(7, list.size());
    }

    @Test
    public void expandAll_fromExpanded() {
        ExpandableList list = new ListBuilder()
                .add(new NodeBuilder(true)
                        .add(1))
                .add(new NodeBuilder(true))
                .add(new NodeBuilder(true)
                        .add(3))
                .build();
        assertEquals(7, list.size());
        assertEquals(7, list.absoluteSize());
        list.expandAll();
        assertEquals(7, list.size());
    }

    @Test
    public void collapseAll_fromCollapsed() {
        ExpandableList list = new ListBuilder()
                .add(new NodeBuilder(false)
                        .add(1))
                .add(new Node())
                .add(new NodeBuilder(false)
                        .add(3))
                .build();
        assertEquals(3, list.size());
        assertEquals(7, list.absoluteSize());
        list.collapseAll();
        assertEquals(3, list.size());
    }

    @Test
    public void collapseAll_fromExpanded() {
        ExpandableList list = new ListBuilder()
                .add(new NodeBuilder(true)
                        .add(1))
                .add(new NodeBuilder(true))
                .add(new NodeBuilder(true)
                        .add(3))
                .build();
        assertEquals(7, list.size());
        assertEquals(7, list.absoluteSize());
        list.collapseAll();
        assertEquals(3, list.size());
    }

    @Test
    public void expandAll_mixed() {
        ExpandableList list = new ListBuilder()
                .add(new NodeBuilder(true)
                        .add(1))
                .add(new NodeBuilder(false))
                .add(new NodeBuilder(false)
                        .add(3))
                .build();
        assertEquals(4, list.size());
        assertEquals(7, list.absoluteSize());
        list.expandAll();
        assertEquals(7, list.size());
    }

    @Test
    public void collapseAll_mixed() {
        ExpandableList list = new ListBuilder()
                .add(new NodeBuilder(true)
                        .add(1))
                .add(new NodeBuilder(false))
                .add(new NodeBuilder(false)
                        .add(3))
                .build();
        assertEquals(4, list.size());
        assertEquals(7, list.absoluteSize());
        list.collapseAll();
        assertEquals(3, list.size());
    }

    @Test
    public void isExpanded() {
        ExpandableList list = new ListBuilder()
                .add(new NodeBuilder(true)
                        .add(1))
                .add(new NodeBuilder(false)
                        .add(1))
                .add(new NodeBuilder(true))
                .build();
        assertFalse(list.isExpanded());
        ExpandableList list2 = new ListBuilder()
                .add(new NodeBuilder(false)
                        .add(1))
                .add(new NodeBuilder(false)
                        .add(1))
                .add(new NodeBuilder(false))
                .build();
        assertFalse(list2.isExpanded());
        ExpandableList list3 = new ListBuilder()
                .add(new NodeBuilder(true)
                        .add(1))
                .add(new NodeBuilder(true)
                        .add(1))
                .add(new NodeBuilder(true))
                .build();
        assertTrue(list3.isExpanded());
        assertTrue(new ExpandableList().isExpanded());
    }

    @Test
    public void setExpandedDeep() {
        ExpandableList list = new ListBuilder()
                .add(new NodeBuilder(false)
                        .add(new NodeBuilder(false)
                                .add(1)))
                .build();

        list.setExpandedDeep(true);
        assertTrue(list.isExpanded());
        assertTrue(list.getChild(0).isExpanded());
        assertTrue(list.getChild(0)
                .getChildren().get(0)
                .isExpanded());
        assertTrue(list.getChild(0)
                .getChildren().get(0)
                .getChildren().get(0)
                .isExpanded());

        list.setExpandedDeep(false);
        assertFalse(list.isExpanded());
        assertFalse(list.getChild(0).isExpanded());
        assertFalse(list.getChild(0)
                .getChildren().get(0)
                .isExpanded());
        assertFalse(list.getChild(0)
                .getChildren().get(0)
                .getChildren().get(0)
                .isExpanded());
    }

    @Test
    public void add() {
        ExpandableList list = new ExpandableList();
        list.insert(new Node());
        assertEquals(1, list.size());
        assertEquals(1, list.absoluteSize());
    }

    @Test
    public void addAll() {
        ExpandableList list = new ExpandableList();
        list.insertAll(Arrays.asList(new Node(), new Node(), new Node()));
        assertEquals(3, list.size());
        assertEquals(3, list.absoluteSize());
        // insert at index
        INode node = new Node();
        node.insert(new Node());
        list.insertAll(1, Collections.singleton(node));
        assertEquals(4, list.size());
        assertEquals(5, list.absoluteSize());
        assertEquals(1, list.getChild(1).getChildren().size());
    }

    @Test
    public void addAll_order() {
        class Node1 extends Node {
        }
        class Node2 extends Node {
        }
        class Node3 extends Node {
        }
        List<INode> nodes = Arrays.asList(new Node1(), new Node2(), new Node3());
        ExpandableList list = new ExpandableList(nodes);
        assertEquals(Node1.class, list.getChild(0).getClass());
        assertEquals(Node2.class, list.getChild(1).getClass());
        assertEquals(Node3.class, list.getChild(2).getClass());

        class Node4 extends Node {
        }
        class Node5 extends Node {
        }
        List<INode> nodes2 = Arrays.asList(new Node4(), new Node5());
        list.insertAll(1, nodes2);
        assertEquals(Node1.class, list.getChild(0).getClass());
        assertEquals(Node4.class, list.getChild(1).getClass());
        assertEquals(Node5.class, list.getChild(2).getClass());
        assertEquals(Node2.class, list.getChild(3).getClass());
        assertEquals(Node3.class, list.getChild(4).getClass());
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void addAll_indexOutOfBounds() {
        new ExpandableList().insertAll(1, Collections.emptyList());
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void addAll_indexOutOfBounds2() {
        new ExpandableList().insertAll(-1, Collections.emptyList());
    }

    @Test
    public void listIterator() {
        ExpandableList list = new ListBuilder()
                .add(new NodeBuilder(false)
                        .add(2))
                .add(new NodeBuilder(false)
                        .add(1))
                .build();
        ListIterator<INode> iter = list.listIterator();

        assertTrue(iter.hasNext());
        assertFalse(iter.hasPrevious());
        assertEquals(0, iter.nextIndex());
        assertEquals(-1, iter.previousIndex());
        assertNotNull(iter.next());

        assertTrue(iter.hasPrevious());
        assertFalse(iter.hasNext());
        assertEquals(1, iter.nextIndex());
        assertEquals(0, iter.previousIndex());
        assertNotNull(iter.next());

        assertNotNull(iter.previous());
        assertFalse(iter.hasNext());
        assertTrue(iter.hasPrevious());
        assertEquals(1, iter.nextIndex());
        assertEquals(0, iter.previousIndex());

        assertNotNull(iter.previous());
        assertTrue(iter.hasNext());
        assertFalse(iter.hasPrevious());
        assertEquals(0, iter.nextIndex());
        assertEquals(-1, iter.previousIndex());
    }

    @Test
    public void listIterator_index() {
        ExpandableList list = new ListBuilder()
                .add(new Node())
                .add(new Node())
                .build();
        ListIterator<INode> iter = list.listIterator(1);
        assertTrue(iter.hasPrevious());
        assertFalse(iter.hasNext());
        assertNotNull(iter.next());
        assertFalse(iter.hasNext());
        assertTrue(iter.hasPrevious());

        assertNotNull(list.listIterator(list.size()));
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void listIterator_index_error() {
        new ExpandableList().listIterator(1);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void listIterator_index_error2() {
        new ExpandableList().listIterator(-1);
    }

    @Test
    public void listIterator_empty() {
        ListIterator<INode> iter = new ExpandableList().listIterator();
        assertFalse(iter.hasNext());
        assertFalse(iter.hasPrevious());
    }

    @Test(expected = NoSuchElementException.class)
    public void listIterator_next_error() {
        ListIterator<INode> iter = new ExpandableList().listIterator();
        iter.next();
    }

    @Test(expected = NoSuchElementException.class)
    public void listIterator_previous_error() {
        ListIterator<INode> iter = new ExpandableList().listIterator();
        iter.previous();
    }

    @Test(expected = NoSuchElementException.class)
    public void listIterator_next_error2() {
        ExpandableList list = new ListBuilder()
                .add(new Node())
                .build();
        ListIterator<INode> iter = list.listIterator();
        iter.next();
        iter.next();
    }

    @Test
    public void indexOf() {
        Node target = new Node();
        INode node = new NodeBuilder(false).add(target).build();
        ExpandableList list = new ListBuilder()
                .add(new Node())
                .add(node)
                .add(new Node())
                .build();
        assertEquals(-1, list.indexOf(target));
        assertEquals(-1, list.indexOf((Object) target));
        node.setExpanded(true);
        assertEquals(2, list.indexOf(target));
        assertEquals(2, list.indexOf((Object) target));
    }

    @Test
    public void observer_onInserted() {
        class TestObserver implements INode.Observer {
            final List<INode> inserted = new ArrayList<>();

            @Override
            public void onInserted(INode child) {
                inserted.add(child);
            }

            @Override
            public void onChanged(INode node) {
            }

            @Override
            public void onDeleted(INode oldParent, INode child) {
            }
        }
        ExpandableList list = new ExpandableList();
        TestObserver observer = new TestObserver();
        list.addObserver(observer);
        list.insert(new Node());
        assertEquals(1, observer.inserted.size());

        INode node = new Node();
        list.insert(node);
        assertEquals(2, observer.inserted.size());

        node.insert(new Node());
        assertEquals(3, observer.inserted.size());

        assertEquals(3, list.absoluteSize());
    }

    @Test
    public void observer_onDeleted() {
        class TestObserver implements INode.Observer {
            final List<INode> deleted = new ArrayList<>();

            @Override
            public void onInserted(INode child) {
            }

            @Override
            public void onChanged(INode node) {
            }

            @Override
            public void onDeleted(INode oldParent, INode child) {
                deleted.add(child);
            }
        }
        ExpandableList list = new ListBuilder()
                .add(new NodeBuilder(false)
                        .add(new Node()))
                .build();
        TestObserver observer = new TestObserver();
        list.addObserver(observer);
        INode child = list.getChild(0).getChildren().get(0);
        child.getParent().delete(child);
        assertEquals(1, observer.deleted.size());

        INode node = new NodeBuilder(false)
                .add(3)
                .build();
        list.getChild(0).insert(node);
        list.delete(0);
        assertEquals(2, observer.deleted.size());

        assertEquals(0, list.absoluteSize());
    }

    @Test
    public void observer_onChanged() {
        class TestObserver implements INode.Observer {
            final List<INode> changed = new ArrayList<>();

            @Override
            public void onInserted(INode child) {
            }

            @Override
            public void onChanged(INode node) {
                changed.add(node);
            }

            @Override
            public void onDeleted(INode oldParent, INode child) {
            }
        }
        ExpandableList list = new ListBuilder()
                .add(new NodeBuilder(false)
                        .add(3))
                .add(new NodeBuilder(false)
                        .add(2))
                .add(new Node())
                .build();
        TestObserver observer = new TestObserver();
        list.addObserver(observer);

        list.getChild(0).setExpanded(false);
        assertEquals(0, observer.changed.size());

        list.getChild(0).setExpanded(true);
        assertEquals(1, observer.changed.size());

        list.getChild(0).getChildren().get(0).setExpanded(true);
        assertEquals(2, observer.changed.size());
    }
}