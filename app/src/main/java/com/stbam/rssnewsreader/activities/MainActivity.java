package com.stbam.rssnewsreader.activities;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.stbam.rssnewsreader.R;
import com.stbam.rssnewsreader.image.ImageLoader;
import com.stbam.rssnewsreader.location.LocationActivity;
import com.stbam.rssnewsreader.parser.DOMParser;
import com.stbam.rssnewsreader.parser.FeedSource;
import com.stbam.rssnewsreader.parser.RSSFeed;
import com.stbam.rssnewsreader.youtube.CategoriesActivity;

import java.util.ArrayList;
import java.util.Collections;

public class MainActivity extends Activity {

    public static RSSFeed feed; // en caso de que algo falle de la nada, entonces es esta linea, quitar el public static a esta linea
    ListView lv;
    CustomListAdapter adapter;
    public static ArrayList<FeedSource> feedLink;
    public static boolean empiezaVacio;
    static MainActivity activityA;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        activityA = this; // hace que la actividad se iguale a this para luego poder terminarla

        // set the feed link for refresh
        feedLink = new SplashActivity().lista_sources;

        // Get feed form the file
        feed = (RSSFeed) getIntent().getExtras().get("feed");

        // Initialize the variables:
        lv = (ListView) findViewById(R.id.listView);
        lv.setVerticalFadingEdgeEnabled(true);



        // Set an Adapter to the ListView
        adapter = new CustomListAdapter(this);
        if (feed != null)
            lv.setAdapter(adapter);
        else
            empiezaVacio = true;

        // Set on item click listener to the ListView
        lv.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                // actions to be performed when a list item clicked
                int pos = arg2;

                // llamar a la funcion que marca el articulo como leido

                Bundle bundle = new Bundle();
                bundle.putSerializable("feed", feed);
                Intent intent = new Intent(MainActivity.this, DetailActivity.class);
                intent.putExtras(bundle);
                intent.putExtra("pos", pos);
                startActivity(intent);

            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        new MenuInflater(this).inflate(R.menu.activity_main, menu);

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.search_option).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

        int id = searchView.getContext().getResources().getIdentifier("android:id/search_src_text", null, null);
        TextView textView = (TextView) searchView.findViewById(id);
        textView.setTextColor(Color.WHITE);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.refresh_option:
                refreshList(item);
                return true;

            case R.id.add_option:
                startAddActivity();
                return true;

            case R.id.youtube_option:
                startYouTubeActivity();
                return true;

            case R.id.account_option:
                startAccountActivity();
                return true;

            case R.id.sort_option:
                if (feed != null) {
                    Collections.sort(feed.getLista());
                    lv.setAdapter(adapter);
                } else {
                    Toast toast = Toast.makeText(getApplicationContext(), "No hay elementos que ordenar", Toast.LENGTH_SHORT);
                    toast.show();
                }
                return true;

            case R.id.location_option:
                startLocationActivity();
                return true;

        }
        return super.onOptionsItemSelected(item);
    }

    public void startAddActivity() {
        Intent intent = new Intent(MainActivity.this, AddActivity.class);
        startActivity(intent);
        //this.finish();
    }

    public void startLocationActivity() {
        Intent intent = new Intent(MainActivity.this, LocationActivity.class);
        startActivity(intent);
        //this.finish();
    }

    public void startYouTubeActivity() {
        Intent intent = new Intent(MainActivity.this, CategoriesActivity.class);
        startActivity(intent);
        //this.finish();
    }

    public void startAccountActivity() {
        Intent intent = new Intent(MainActivity.this, AccountActivity.class);
        startActivity(intent);
        //this.finish();
    }

    public void refreshList(final MenuItem item) {

        feedLink = new AddActivity().feedLink;

        if (feedLink == null)
            feedLink = new SplashActivity().lista_sources2;

        // trigger feed refresh:
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                DOMParser tmpDOMParser = new DOMParser();
                feed = null;
                int itemes_feed = feedLink.size();
                boolean todoNulo = true;

                for (int i = 0; i < itemes_feed; i++) {
                    FeedSource s = feedLink.get(i);
                    //System.out.println("Nombre: " + s.getNombre() + " URL: " + s.getURL() + " Aceptado: " + s.isAceptado());
                    if (s.isAceptado()) {
                        feed = tmpDOMParser.parseXml(s.getURL(), s.getNombre());
                        todoNulo = false;
                    }
                }

                if (todoNulo)
                    feed = null;

                MainActivity.this.runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        if (feed != null && feed.getItemCount() > 0) {

                            if (empiezaVacio)
                                lv.setAdapter(adapter);

                            else {
                                lv.setAdapter(adapter);
                                adapter.notifyDataSetChanged();
                            }
                            item.setEnabled(false);
                            new CountDownTimer(3000, 1000) {
                                public void onTick(long millisUntilFinished) {
                                }

                                public void onFinish() {
                                    item.setEnabled(true);
                                }
                            }.start();
                        } else if (feed == null)
                            lv.setAdapter(null);
                    }
                });
            }
        });
        thread.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        adapter.imageLoader.clearCache();
        adapter.notifyDataSetChanged();
    }

    // List adapter class
    class CustomListAdapter extends BaseAdapter {

        private LayoutInflater layoutInflater;
        public ImageLoader imageLoader;

        public CustomListAdapter(MainActivity activity) {

            layoutInflater = (LayoutInflater) activity
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            imageLoader = new ImageLoader(activity.getApplicationContext());
        }

        @Override
        public int getCount() {

            // Set the total list item count
            return feed.getItemCount();
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            // Inflate the item layout and set the views
            View listItem = convertView;
            int pos = position;
            if (listItem == null) {
                listItem = layoutInflater.inflate(R.layout.entry_list, null);
            }

            // Initialize the views in the layout
            ImageView iv = (ImageView) listItem.findViewById(R.id.thumb);
            TextView tvTitle = (TextView) listItem.findViewById(R.id.title);
            TextView tvDate = (TextView) listItem.findViewById(R.id.date);

            // Set the views in the layout
            imageLoader.DisplayImage(feed.getItem(pos).getImage(), iv);
            tvTitle.setText(feed.getItem(pos).getTitle());
            tvDate.setText(feed.getItem(pos).get_source_page());
            return listItem;
        }

    }

}
