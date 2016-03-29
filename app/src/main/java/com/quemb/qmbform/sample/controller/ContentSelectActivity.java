package com.quemb.qmbform.sample.controller;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CheckedTextView;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.quemb.qmbform.sample.R;
import com.quemb.qmbform.sample.model.MockContent;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by alvinzeng on 3/29/16.
 */
public class ContentSelectActivity extends AppCompatActivity {

    ListView listView;
    ArrayList<MockContent> mockContents;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_content_select);

        mockContents = (ArrayList<MockContent>) getIntent().getSerializableExtra("SELECTED_VALUES");
        if (mockContents == null) {
            mockContents = new ArrayList<>();
        }

        listView = (ListView)findViewById(R.id.listView);
        ArrayList<MockContent> contents = new ArrayList<>();
        contents.add(new MockContent("AAA"));
        contents.add(new MockContent("BBB"));
        contents.add(new MockContent("CCC"));
        contents.add(new MockContent("DDD"));
        listView.setAdapter(new ContentListAdapter(this, android.R.layout.simple_list_item_multiple_choice, contents));
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MockContent item = (MockContent) parent.getAdapter().getItem(position);
                if (findContent(item)) {
                    removeContent(item);
                } else {
                    mockContents.add(item);
                }

                ((ContentListAdapter)parent.getAdapter()).notifyDataSetChanged();
            }
        });
    }

    public void onDoneClick(View v) {
        Intent intent = new Intent();
        intent.putExtra("SELECTED_VALUES", mockContents);
        setResult(RESULT_OK, intent);
        finish();
    }

    private void removeContent(MockContent content) {
        for (MockContent item : mockContents) {
            if (item.title.equals(content.title)) {
                mockContents.remove(item);
                break;
            }
        }
    }

    private boolean findContent(MockContent content) {
        boolean result = false;

        for (MockContent item : mockContents) {
            if (item.title.equals(content.title)) {
                result = true;
                break;
            }
        }

        return result;
    }

    class ContentListAdapter extends ArrayAdapter<MockContent> {

        public ContentListAdapter(Context context, int resource, List<MockContent> objects) {
            super(context, resource, objects);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            convertView =  super.getView(position, convertView, parent);

            MockContent item = getItem(position);
            CheckedTextView box = (CheckedTextView)convertView.findViewById(android.R.id.text1);
            if (findContent(item)) {
                box.setChecked(true);
            } else {
                box.setChecked(false);
            }

            return convertView;
        }
    }
}
