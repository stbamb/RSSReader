package com.stbam.rssnewsreader;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Array;
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
    public static ArrayList<FeedSource> lista_sources2 = new ArrayList<FeedSource>();
	RSSFeed feed;
	public static String fileName;
    public static String logName;

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
        logName = "RSSReaderLog.stb";

		File feedFile = getBaseContext().getFileStreamPath(fileName);
        File logFile = getBaseContext().getFileStreamPath(logName);

        if (!logFile.exists())
        {
            escribirRegistro();
            Log.d("Esta creando el arhivo nuevo", "");
        }
        lista_sources2 = leerRegistros(logName);

        if (lista_sources2 != null)
        {
            for (int i = 0; i < lista_sources2.size(); i++)
            {
                Log.d("Esta funcionando:", "" + lista_sources2.get(i).getURL());
                Log.d("Esta funcionando:", "" + lista_sources2.get(i).getNombre());
                Log.d("Esta funcionando:", "" + lista_sources2.get(i).isAceptado());
            }
        }

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
				startListActivity(feed);
			}

		} else {

			// Connected - Start parsing
			new AsyncLoadXMLFeed().execute();

		}

	}

	private void startListActivity(RSSFeed feed) {

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
                FeedSource s = lista_sources2.get(i);
                if (s.isAceptado())
                    feed = myParser.parseXml(s.getURL(), s.getNombre());
            }

			if (feed != null && feed.getItemCount() > 0)
            {
                WriteFeed(feed);
            }
			return null;

		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);

			startListActivity(feed);
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

    public void escribirRegistro() {

        FileOutputStream fOut = null;
        ObjectOutputStream osw = null;

        try {
            fOut = openFileOutput(logName, MODE_PRIVATE);
            osw = new ObjectOutputStream(fOut);

            for (int i = 0; i < lista_sources.size(); i++)
            {
                FeedSource s = lista_sources.get(i);
                osw.write(s.getURL().getBytes());
                osw.write("; ".getBytes());
                osw.write(s.getNombre().getBytes());
                osw.write("; ".getBytes());
                if (s.isAceptado())
                    osw.write("true".getBytes());
                else
                    osw.write("false".getBytes());
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
        char t;
        File feedFile = getBaseContext().getFileStreamPath(logName);
        if (!feedFile.exists())
            return null;

        try {
            fIn = openFileInput(fName);
            isr = new ObjectInputStream(fIn);

            for (int i = 0; i < lista_sources.size(); i++)
            {
                texto = isr.readLine();
                sour = crearListaDinamica(texto);
                s.add(sour);
                //Log.d("Linea leida:", "" + texto);
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

        if (linea.contains("true"))
            sour.setAceptado(true);

        else
            sour.setAceptado(false);

        return sour;
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
