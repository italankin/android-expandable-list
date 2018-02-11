package com.italankin.sample.items;

import com.italankin.sample.expandablelist.ExpandableList;

public abstract class BaseItem extends ExpandableList.Node<Integer> {
    public final String text;

    public BaseItem(String text) {
        this.text = text;
    }
}
