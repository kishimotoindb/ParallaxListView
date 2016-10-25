package com.bearabitcf.parallaxeffectheaderlistview;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private ParallaxEffectHeaderListView mPEFListView;
    private String[] words = {"1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14"};
    private MyAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        init();
    }

    private void init() {
        mPEFListView = (ParallaxEffectHeaderListView) findViewById(R.id.main_pefListView);
        mPEFListView.setHeaderImageByResId(R.drawable.parallax_img);
        mAdapter = new MyAdapter();
        mPEFListView.setAdapter(mAdapter);

    }

    class MyAdapter extends BaseAdapter {
        public MyAdapter() {
            super();
        }

        @Override
        public int getCount() {
            return words.length;
        }

        @Override
        public Object getItem(int position) {
            return words[position];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TextView title = null;
            if (convertView == null) {
                convertView = View.inflate(MainActivity.this, R.layout.item_home_recyclerview, null);
                title = (TextView) convertView.findViewById(R.id.item_front_title);
                convertView.setTag(title);
            } else {
                title = (TextView) convertView.getTag();
            }
            title.setText(words[position]);
            return convertView;
        }
    }
}
