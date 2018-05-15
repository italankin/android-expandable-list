package com.italankin.sample;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import com.italankin.sample.expandablelist.ExpandableList;
import com.italankin.sample.expandablelist.INode;
import com.italankin.sample.items.BaseItem;
import com.italankin.sample.items.Header1;
import com.italankin.sample.items.Header2;
import com.italankin.sample.items.Header3;
import com.italankin.sample.items.Item;

public class MainActivity extends AppCompatActivity implements ExpandableListAdapter.Listener {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.acitivty_main);
        RecyclerView list = findViewById(R.id.list);
        list.setAdapter(new ExpandableListAdapter(this, createList(), this));
    }

    @Override
    public void onListItemClick(int pos, INode item) {
        StringBuilder desc = new StringBuilder(((BaseItem) item).text);
        INode node = item;
        while ((node = node.getParent()) != null) {
            desc.insert(0, ((BaseItem) node).text + " > ");
        }
        Toast.makeText(this, desc.toString(), Toast.LENGTH_SHORT).show();
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
        Log.d("MainActivity", list.toString());
        return list;
    }
}
