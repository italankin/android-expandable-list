# Expandable List

![Screenshot](/art/scr01.png)

Implementation of expandable list in Android. 

## `ExpandableList`

[`ExpandableList`](/app/src/main/java/com/italankin/sample/expandablelist/ExpandableList.java) data structure supports:
* lists with any depth
* animations via `RecyclerView.Adapter.notifyItemRange***`
* can also be used with `ListView`

## Expanding/collapsing:

```java
int nodeIndex = dataset.indexOf(node);
if (node.isExpanded()) {
    int collapsed = dataset.collapse(node);
    if (collapsed > 0) {
        notifyItemRangeRemoved(nodeIndex + 1, collapsed);
    }
} else {
    int expanded = dataset.expand(node);
    if (expanded > 0) {
        notifyItemRangeInserted(nodeIndex + 1, expanded);
    }
}
notifyItemChanged(nodeIndex);
```

Example implementation of `RecyclerView.Adapter` can be found in [`ExpandableListAdapter`](/app/src/main/java/com/italankin/sample/ExpandableListAdapter.java).