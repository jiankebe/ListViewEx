package com.example.overscroll.listview;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
    MyAdapter adapter;
    List<String> strings = new ArrayList<String>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ListViewEx listViewEx = new ListViewEx(getApplicationContext());
        setContentView(listViewEx);
        for (int i = 0; i < 20; i++) {
            strings.add("jianchuanli" + i);
            // TextView tv = new TextView(getApplicationContext());
            // tv.setText("jianchuanli"+i);
            // linearLayout.addView(tv);
        }
        adapter = new MyAdapter();
        listViewEx.setAdapter(adapter);
    }

    class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return strings.size();
        }

        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return strings.get(position);
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            // TODO Auto-generated method stub
            TextView tView = new TextView(getApplicationContext());
            // ((ViewGroup) convertView).addView(tView);
            tView.setText(strings.get(position));
            tView.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    Toast.makeText(getApplicationContext(), strings.get(position), Toast.LENGTH_SHORT).show();
                }
            });
            return tView;
        }

    }

}
