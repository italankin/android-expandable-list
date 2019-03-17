package com.italankin.sample.adapter;

import com.hannesdorfmann.adapterdelegates4.AbsDelegationAdapter;
import com.italankin.sample.expandablelist.ExpandableList;

public class ExpandableListAdapter extends AbsDelegationAdapter<ExpandableList> {

    public ExpandableListAdapter(ExpandableList expandableList, OnItemClickListener onItemClickListener,
            OnHeaderClickListener onHeaderClickListener) {
        setItems(expandableList);
        setHasStableIds(true);
        delegatesManager.addDelegate(new ItemAdapterDelegate(onItemClickListener));
        delegatesManager.addDelegate(new Header1AdapterDelegate(onHeaderClickListener));
        delegatesManager.addDelegate(new Header2AdapterDelegate(onHeaderClickListener));
        delegatesManager.addDelegate(new Header3AdapterDelegate(onHeaderClickListener));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public long getItemId(int position) {
        return items.get(position).hashCode();
    }
}
