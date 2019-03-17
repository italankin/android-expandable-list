# Expandable List

![Screenshot](/art/anim.gif)

Implementation of expandable list in Android. 

## `ExpandableList`

[`ExpandableList`](/app/src/main/java/com/italankin/sample/expandablelist/ExpandableList.java) data structure supports:
* lists with any depth
* animations via `RecyclerView.Adapter.notifyItemRange***`
* can also be used with `ListView`

## Expanding/collapsing:

```java
int nodeIndex = expandableList.indexOf(node);
if (node.isExpanded()) {
    int collapsed = expandableList.collapse(node);
    if (collapsed > 0) {
        adapter.notifyItemRangeRemoved(nodeIndex + 1, collapsed);
    }
} else {
    int expanded = expandableList.expand(node);
    if (expanded > 0) {
        adapter.notifyItemRangeInserted(nodeIndex + 1, expanded);
    }
}
adapter.notifyItemChanged(nodeIndex);
```
