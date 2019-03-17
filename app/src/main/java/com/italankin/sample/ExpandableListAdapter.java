package com.italankin.sample;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.italankin.sample.expandablelist.ExpandableList;
import com.italankin.sample.expandablelist.INode;
import com.italankin.sample.items.BaseItem;
import com.italankin.sample.items.Header1;
import com.italankin.sample.items.Header2;
import com.italankin.sample.items.Header3;
import com.italankin.sample.items.Item;

import androidx.recyclerview.widget.RecyclerView;

public class ExpandableListAdapter extends RecyclerView.Adapter<Holder> {
    private static final int TYPE_HEADER1 = 0;
    private static final int TYPE_HEADER2 = 1;
    private static final int TYPE_HEADER3 = 2;
    private static final int TYPE_ITEM = 3;

    private final ExpandableList dataset;
    private final LayoutInflater inflater;
    private final Listener listener;

    public ExpandableListAdapter(Context context, ExpandableList dataset, Listener listener) {
        this.inflater = LayoutInflater.from(context);
        this.dataset = dataset;
        this.listener = listener;
        setHasStableIds(true);
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        int layoutRes;
        switch (viewType) {
            case TYPE_HEADER1:
                layoutRes = R.layout.item_header1;
                break;
            case TYPE_HEADER2:
                layoutRes = R.layout.item_header2;
                break;
            case TYPE_HEADER3:
                layoutRes = R.layout.item_header3;
                break;
            case TYPE_ITEM:
            default:
                layoutRes = R.layout.item;
        }
        final Holder holder = new Holder(inflater.inflate(layoutRes, parent, false));
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int pos = holder.getAdapterPosition();
                if (pos != RecyclerView.NO_POSITION) {
                    INode node = dataset.get(pos);
                    switch (getItemViewType(pos)) {
                        case TYPE_HEADER1:
                        case TYPE_HEADER2:
                        case TYPE_HEADER3:
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
                            break;
                        case TYPE_ITEM:
                        default:
                            listener.onListItemClick(pos, node);
                    }
                }
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(Holder holder, int position) {
        BaseItem item = (BaseItem) dataset.get(position);
        holder.text.setText(item.text);
        if (!(item instanceof Item)) {
            int end = item.isExpanded() ? R.drawable.ic_keyboard_arrow_up : R.drawable.ic_keyboard_arrow_down;
            holder.text.setCompoundDrawablesWithIntrinsicBounds(0, 0, end, 0);
        }
    }

    @Override
    public int getItemViewType(int position) {
        INode node = dataset.get(position);
        if (node instanceof Header1) {
            return TYPE_HEADER1;
        }
        if (node instanceof Header2) {
            return TYPE_HEADER2;
        }
        if (node instanceof Header3) {
            return TYPE_HEADER3;
        }
        return TYPE_ITEM;
    }

    @Override
    public long getItemId(int position) {
        return dataset.get(position).hashCode();
    }

    @Override
    public int getItemCount() {
        return dataset.size();
    }

    public interface Listener {
        void onListItemClick(int pos, INode item);
    }
}

class Holder extends RecyclerView.ViewHolder {
    final TextView text;

    Holder(View itemView) {
        super(itemView);
        text = (TextView) itemView;
    }
}