package com.italankin.sample.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hannesdorfmann.adapterdelegates4.AdapterDelegate;
import com.italankin.sample.R;
import com.italankin.sample.expandablelist.ExpandableList;
import com.italankin.sample.items.BaseItem;
import com.italankin.sample.items.Header1;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class Header1AdapterDelegate extends AdapterDelegate<ExpandableList> {
    private final OnHeaderClickListener listener;

    public Header1AdapterDelegate(OnHeaderClickListener listener) {
        this.listener = listener;
    }

    @Override
    protected boolean isForViewType(@NonNull ExpandableList items, int position) {
        return items.get(position) instanceof Header1;
    }

    @NonNull
    @Override
    protected RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_header1, parent, false);
        ViewHolder holder = new ViewHolder(view);
        view.setOnClickListener(v -> {
            int pos = holder.getAdapterPosition();
            if (pos != RecyclerView.NO_POSITION) {
                listener.onHeaderClick(pos);
            }
        });
        return holder;
    }

    @Override
    protected void onBindViewHolder(@NonNull ExpandableList items, int position, @NonNull RecyclerView.ViewHolder holder, @NonNull List<Object> payloads) {
        BaseItem item = (BaseItem) items.get(position);
        ViewHolder viewHolder = (ViewHolder) holder;
        viewHolder.text.setText(item.text);
        int end = item.isExpanded() ? R.drawable.ic_keyboard_arrow_up : R.drawable.ic_keyboard_arrow_down;
        viewHolder.text.setCompoundDrawablesWithIntrinsicBounds(0, 0, end, 0);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        final TextView text;

        ViewHolder(View itemView) {
            super(itemView);
            text = (TextView) itemView;
        }
    }
}
