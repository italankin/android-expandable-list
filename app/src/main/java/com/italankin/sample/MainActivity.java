package com.italankin.sample;

import android.os.Bundle;
import android.widget.Toast;

import com.italankin.sample.adapter.ExpandableListAdapter;
import com.italankin.sample.adapter.OnHeaderClickListener;
import com.italankin.sample.adapter.OnItemClickListener;
import com.italankin.sample.expandablelist.ExpandableList;
import com.italankin.sample.expandablelist.INode;
import com.italankin.sample.items.BaseItem;
import com.italankin.sample.items.Header1;
import com.italankin.sample.items.Header2;
import com.italankin.sample.items.Header3;
import com.italankin.sample.items.Item;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

public class MainActivity extends AppCompatActivity implements OnItemClickListener, OnHeaderClickListener {

    private final ExpandableList expandableList = createList();
    private ExpandableListAdapter expandableListAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.acitivty_main);
        RecyclerView recyclerView = findViewById(R.id.list);
        expandableListAdapter = new ExpandableListAdapter(expandableList, this, this);
        recyclerView.setAdapter(expandableListAdapter);
    }

    @Override
    public void onItemClick(int position) {
        BaseItem item = (BaseItem) expandableList.get(position);
        StringBuilder desc = new StringBuilder(item.text);
        INode node = item;
        while ((node = node.getParent()) != null) {
            if (node.getParent() == null) break;
            desc.insert(0, ((BaseItem) node).text + " > ");
        }
        Toast.makeText(this, desc.toString(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onHeaderClick(int position) {
        INode node = expandableList.get(position);
        if (node.isExpanded()) {
            int collapsed = expandableList.collapse(node);
            if (collapsed > 0) {
                expandableListAdapter.notifyItemRangeRemoved(position + 1, collapsed);
            }
        } else {
            int expanded = expandableList.expand(node);
            if (expanded > 0) {
                expandableListAdapter.notifyItemRangeInserted(position + 1, expanded);
            }
        }
        expandableListAdapter.notifyItemChanged(position);
    }

    private static ExpandableList createList() {
        ExpandableList list = new ExpandableList();
        for (int i = 0; i < 5; i++) {
            // first level (root nodes)
            Header1 header1 = new Header1(i);
            for (int j = 0; j < 4; j++) {
                // second level
                Header2 header2 = new Header2(j);
                for (int k = 0; k < 3; k++) {
                    // third level
                    Header3 header3 = new Header3(k);
                    for (int n = 0, count = (int) (6 * Math.random() + 2); n < count; n++) {
                        // fourth level
                        header3.insert(new Item(n));
                    }
                    header2.insert(header3);
                }
                header1.insert(header2);
            }
            list.insert(header1);
        }
        return list;
    }
}
