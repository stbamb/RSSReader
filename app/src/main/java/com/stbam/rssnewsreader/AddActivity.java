package com.stbam.rssnewsreader;

import android.app.ActionBar;
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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;


public class AddActivity extends Activity {

    ListView lv;
    public static ArrayList<FeedSource> feedLink;
    VersionAdapter adapter;
    SplashActivity s =  new SplashActivity();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);


        // mi codigo

        feedLink = new SplashActivity().lista_sources2;

        lv = (ListView) findViewById(R.id.lista_paginas);
        lv.setVerticalFadingEdgeEnabled(true);

        adapter = new VersionAdapter(this);

        lv.setAdapter(adapter);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {

                // encuentra la posicion del item seleccionado
                // y le pone o le quita el check

                int pos = arg2;

                View child = lv.getChildAt(pos);

                ImageView checked_item = (ImageView) child.findViewById(R.id.check);

                if (checked_item.getVisibility() == View.VISIBLE)
                {
                    checked_item.setVisibility(View.INVISIBLE);
                    feedLink.get(pos).setAceptado(false);
                    escribirRegistro();
                    leerRegistros("RSSReaderLog.stb"); // linea no necesaria, solo se usa para ver registros escritos al archivo 'RSSReaderLog.stb'
                }
                else
                {
                    checked_item.setVisibility(View.VISIBLE);
                    feedLink.get(pos).setAceptado(true);
                    escribirRegistro();
                    leerRegistros("RSSReaderLog.stb"); // linea no necesaria, solo se usa para ver registros escritos al archivo 'RSSReaderLog.stb'
                }
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
        switch (item.getItemId()) {

            case android.R.id.home:
                // app icon in action bar clicked; finish activity to go home
                finish();
                return true;
        }
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
               // holder = new ViewHolder();
                listItem = layoutInflater.inflate(R.layout.feed_source, null);

                //holder.imgViewLogo = (ImageView) findViewById(R.id.check);
                //listItem.setTag(holder);
            }
            else
            {
               // holder = (ViewHolder)convertView.getTag();
            }

            ImageView iv = (ImageView) listItem.findViewById(R.id.category_img);
            TextView tvTitle = (TextView) listItem.findViewById(R.id.source_name);
            ImageView checked_item = (ImageView) listItem.findViewById(R.id.check);

            if (feedLink.get(pos).isAceptado()) {
                checked_item.setVisibility(View.VISIBLE);
            }

            else if (!feedLink.get(pos).isAceptado()) {
                checked_item.setVisibility(View.INVISIBLE);
            }
            iv.setBackgroundResource(R.drawable.ic_launcher);
            tvTitle.setText(feedLink.get(pos).getNombre());

            return listItem;
        }

    }

    public void escribirRegistro() {

        FileOutputStream fOut = null;
        ObjectOutputStream osw = null;

        try {
            fOut = openFileOutput("RSSReaderLog.stb", MODE_PRIVATE);
            osw = new ObjectOutputStream(fOut);

            for (int i = 0; i < feedLink.size(); i++)
            {
                FeedSource s = feedLink.get(i);
                osw.write(s.getURL().getBytes());
                osw.write("; ".getBytes());
                osw.write(s.getNombre().getBytes());
                osw.write("; ".getBytes());
                if (s.isAceptado())
                    osw.write("trueverdadero".getBytes());
                else
                    osw.write("falsefalso".getBytes());
                osw.write(";".getBytes());
                osw.write("\n".getBytes());
            }

            osw.flush();
        }

        catch (Exception e) {
            //e.printStackTrace();
        }

        finally {
            try {
                fOut.close();
            } catch (IOException e) {
                //e.printStackTrace();
            }
        }
    }

    // si existe entonces lee el registro
    // para cargar los sources aceptados
    public  ArrayList<FeedSource> leerRegistros(String fName) {

        FileInputStream fIn = null;
        ObjectInputStream isr = null;
        ArrayList<FeedSource> s = new ArrayList<FeedSource>();
        FeedSource sour = new FeedSource();
        String texto = null;
        File feedFile = getBaseContext().getFileStreamPath("RSSReaderLog.stb");
        if (!feedFile.exists())
            return null;

        try {
            fIn = openFileInput(fName);
            isr = new ObjectInputStream(fIn);

            //System.out.println("Listasourcessize: " + lista_sources.size());
            for (int i = 0; i < feedLink.size(); i++)
            {
                texto = isr.readLine();
                sour = crearListaDinamica(texto);
                s.add(sour);
                System.out.println("Linea leida desde AddActivity: " + texto);
            }
        }

        catch (Exception e) {
            //e.printStackTrace();
        }

        finally {
            try {
                fIn.close();
            } catch (IOException e) {
                //e.printStackTrace();
            }
        }

        return s;

    }

    private FeedSource crearListaDinamica(String linea)
    {
        FeedSource sour = new FeedSource();

        String[] partes = linea.split(";");
        sour.setURL(partes[0]);
        sour.setNombre(partes[1]);

        // System.out.println("Verdadero/Falso:" + partes[2]);

        if (linea.contains("trueverdadero"))
            sour.setAceptado(true);

        else
            sour.setAceptado(false);

        return sour;
    }
}
