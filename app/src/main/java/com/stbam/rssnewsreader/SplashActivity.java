package com.stbam.rssnewsreader;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import com.stbam.rssnewsreader.parser.*;

public class SplashActivity extends Activity {

    // llamar a la funcion que recolecta los JSON y los convierte en instancias
    // de la clase FeedSource

    /*int largo = 0;

    for (int i = 0; i < largo; i++)
    {
        FeedSource nuevo = new FeedSource();
        nuevo.setURL();
        nuevo.setNombre();
        nuevo.setCategoria();
        nuevo.setIdioma();
    }*/

    public static ArrayList<FeedSource> lista_sources = new ArrayList<FeedSource>();
	RSSFeed feed;
	String fileName;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

        FeedSource fuente = new FeedSource();

        fuente.setURL("http://www.theverge.com/rss/frontpage");
        fuente.setNombre("The Verge");
        fuente.setCategoria("Tecnologia");
        fuente.setIdioma("English");

        FeedSource fuente2 = new FeedSource();

        fuente2.setURL("http://www.polygon.com/rss/index.xml");
        fuente2.setNombre("Polygon");
        fuente2.setCategoria("Gaming");
        fuente2.setIdioma("English");

        lista_sources.add(fuente);
        lista_sources.add(fuente2);

		super.onCreate(savedInstanceState);

		setContentView(R.layout.splash);

		fileName = "TDRSSFeed.td";

		File feedFile = getBaseContext().getFileStreamPath(fileName);

        //Log.d("Path de archivo", feedFile.getAbsolutePath());

		ConnectivityManager conMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		if (conMgr.getActiveNetworkInfo() == null) {

			// No connectivity. Check if feed File exists
			if (!feedFile.exists()) {

				// No connectivity & Feed file doesn't exist: Show alert to exit
				// & check for connectivity
				AlertDialog.Builder builder = new AlertDialog.Builder(this);
				builder.setMessage(
						"Unable to reach server, \nPlease check your connectivity.")
						.setTitle("RSS Reader")
						.setCancelable(false)
						.setPositiveButton("Exit",
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
											int id) {
										finish();
									}
								});

				AlertDialog alert = builder.create();
				alert.show();
			} else {

				feed = ReadFeed(fileName);
				startLisActivity(feed);
			}

		} else {

			// Connected - Start parsing
			new AsyncLoadXMLFeed().execute();

		}

	}

	private void startLisActivity(RSSFeed feed) {

		Bundle bundle = new Bundle();
		bundle.putSerializable("feed", feed);

		// launch List activity
		Intent intent = new Intent(SplashActivity.this, MainActivity.class);
		intent.putExtras(bundle);
		startActivity(intent);

		// kill this activity
		finish();

	}

	private class AsyncLoadXMLFeed extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... params) {

			// Obtain feed
			DOMParser myParser = new DOMParser();

            //esto sirve para que recolecte todos los links

            for (int i = 0; i < lista_sources.size(); i++)
            {
                if (lista_sources.get(i).isAceptado())
                    feed = myParser.parseXml(lista_sources.get(i).getURL());
            }

			if (feed != null && feed.getItemCount() > 0)
				WriteFeed(feed);
			return null;

		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);

			startLisActivity(feed);
		}

	}

	// Method to write the feed to the File
	private void WriteFeed(RSSFeed data) {

		FileOutputStream fOut = null;
		ObjectOutputStream osw = null;

		try {
			fOut = openFileOutput(fileName, MODE_PRIVATE);
			osw = new ObjectOutputStream(fOut);
			osw.writeObject(data);
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

	// Method to read the feed from the File
	private RSSFeed ReadFeed(String fName) {

		FileInputStream fIn = null;
		ObjectInputStream isr = null;

		RSSFeed _feed = null;
		File feedFile = getBaseContext().getFileStreamPath(fileName);
		if (!feedFile.exists())
			return null;

		try {
			fIn = openFileInput(fName);
			isr = new ObjectInputStream(fIn);

			_feed = (RSSFeed) isr.readObject();
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

		return _feed;

	}

}
