package com.stbam.rssnewsreader;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.stbam.rssnewsreader.image.ImageLoader;
import com.stbam.rssnewsreader.parser.FeedSource;

import junit.runner.Version;

import java.util.ArrayList;


public class AddActivity extends Activity {

    ListView lv;
    public static ArrayList<FeedSource> feedLink;
    VersionAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        // mi codigo

        feedLink = new SplashActivity().lista_sources;

        lv = (ListView) findViewById(R.id.lista_paginas);
        lv.setVerticalFadingEdgeEnabled(true);

        adapter = new VersionAdapter(this);

        lv.setAdapter(adapter);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {

                // encuentra la posiciion del item seleccionado
                // y le pone o le quita el check

                int pos = arg2;

                View child = lv.getChildAt(pos);

                ImageView checked_item = (ImageView) child.findViewById(R.id.check);

                if (checked_item.getVisibility() == View.VISIBLE)
                {
                    checked_item.setVisibility(View.INVISIBLE);
                }
                else
                    checked_item.setVisibility(View.VISIBLE);






            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.add, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }

    class VersionAdapter extends BaseAdapter {

        private LayoutInflater layoutInflater;

        public VersionAdapter(AddActivity activity) {
            layoutInflater = (LayoutInflater) activity
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return feedLink.size();
        }

        @Override
        public Object getItem(int position) {
            return feedLink.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            View listItem = convertView;
            int pos = position;
            if (listItem == null) {
                listItem = layoutInflater.inflate(R.layout.feed_source, null);
            }

            ImageView iv = (ImageView) listItem.findViewById(R.id.category_img);
            TextView tvTitle = (TextView) listItem.findViewById(R.id.source_name);

            iv.setBackgroundResource(R.drawable.ic_launcher);
            tvTitle.setText(feedLink.get(pos).getNombre());

            return listItem;
        }

    }
}
