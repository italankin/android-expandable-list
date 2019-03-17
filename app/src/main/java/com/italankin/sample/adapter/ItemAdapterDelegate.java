package com.italankin.sample.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hannesdorfmann.adapterdelegates4.AdapterDelegate;
import com.italankin.sample.R;
import com.italankin.sample.expandablelist.ExpandableList;
import com.italankin.sample.items.Item;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ItemAdapterDelegate extends AdapterDelegate<ExpandableList> {
    private final OnItemClickListener listener;

    public ItemAdapterDelegate(OnItemClickListener listener) {
        this.listener = listener;
    }

    @Override
    protected boolean isForViewType(@NonNull ExpandableList items, int position) {
        return items.get(position) instanceof Item;
    }

    @NonNull
    @Override
    protected RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item, parent, false);
        ViewHolder holder = new ViewHolder(view);
        view.setOnClickListener(v -> {
            int pos = holder.getAdapterPosition();
            if (pos != RecyclerView.NO_POSITION) {
                listener.onItemClick(pos);
            }
        });
        return holder;
    }

    @Override
    protected void onBindViewHolder(@NonNull ExpandableList items, int position, @NonNull RecyclerView.ViewHolder holder, @NonNull List<Object> payloads) {
        Item item = (Item) items.get(position);
        ((ViewHolder) holder).text.setText(item.text);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        final TextView text;

        ViewHolder(View itemView) {
            super(itemView);
            text = (TextView) itemView;
        }
    }

}
