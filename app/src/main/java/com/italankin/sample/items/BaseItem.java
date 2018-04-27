package com.italankin.sample.items;

import com.italankin.sample.expandablelist.Node;

public abstract class BaseItem extends Node {
    public final String text;

    public BaseItem(String text) {
        this.text = text;
    }
}
