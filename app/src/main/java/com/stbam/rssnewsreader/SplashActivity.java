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
import com.stbam.rssnewsreader.parser.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

// esta es la clase que abre la aplicacion cuando hay contenido que cargar
// se utiliza basicamente para cargar el contenido y para tener todo listo
// para que cuando se llegue a la actividad principal para que las noticias esten listas
// Nos basamos en la idea del tutorial de http://techiedreams.com/android-simple-rss-reader/
public class SplashActivity extends Activity {

    public static String url = "https://raw.githubusercontent.com/stbam/RSSReader/master/JSONExample.json"; // para pruebas
    public static String url2 = "http://proyecto2.cloudapp.net:8080/feeds"; // para progra
    public static ArrayList<FeedSource> lista_sources = new ArrayList<FeedSource>();
    public static ArrayList<FeedSource> lista_sources2 = new ArrayList<FeedSource>();
    public static ArrayList<FeedSource> lista_sources_viejos = new ArrayList<FeedSource>();
    public static ArrayList<FeedSource> lista_sources_viejos2 = new ArrayList<FeedSource>();
	RSSFeed feed;
	public static String fileName;
    public static String logName;
    public static String sourceName;
    JSONArray fuentessr = null;
    private static final String TAG_SOURCES = "source";
    private static final String TAG_URL = "url";
    private static final String TAG_URL_PAGINA = "urlpagina";
    private static final String TAG_NOMBRE = "nombre";
    private static final String TAG_CATEGORIA = "categoria";
    private static final String TAG_IDIOMA = "idioma";
    public static boolean sinterminar = true;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.splash);

		fileName = "TDRSSFeed.td";
        logName = "RSSReaderLog.stb";
        sourceName = "RSSReaderFeed.stb";

        File feedFile = getBaseContext().getFileStreamPath(fileName);
        File logFile = getBaseContext().getFileStreamPath(logName);
        File sourceFile = getBaseContext().getFileStreamPath(sourceName);

        // si por algun motivo se llega
        // a eliminar un source del JSON
        // entonces descomentar las siguientes dos lineas

        //escribirRegistro(logName);
        //leerRegistros(logName);

        // llamada a la funcion que recolecta los JSON y los convierte en instancias
        // de la clase FeedSource y los mete en lista_sources
        getAllSources a = new getAllSources();
        a.execute();
        int abc = 0;
        while (sinterminar)
            abc++;

		ConnectivityManager conMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		if (conMgr.getActiveNetworkInfo() == null) {

			// no hay conexion a internet
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

			// se empiezan a parsear los sources
			new AsyncLoadXMLFeed().execute();

		}

	}

    public boolean cambiaronFeedSources()
    {
        boolean bandera = false;

        if (lista_sources.size() == lista_sources_viejos.size() && lista_sources.size() != 0)
            for (int i = 0; i < lista_sources.size(); i++)
            {
                /*System.out.println(lista_sources.get(i).getNombre().equals(lista_sources_viejos.get(i).getNombre()));
                System.out.println("Nombre lista_sources[" + i + "]: " + lista_sources.get(i).getNombre());
                System.out.println("Nombre lista_sources_viejos[" + i + "]: " + lista_sources_viejos.get(i).getNombre());
                System.out.println(lista_sources.get(i).getURL().equals(lista_sources_viejos.get(i).getURL()));
                System.out.println(lista_sources.get(i).getCategoria().equals(lista_sources_viejos.get(i).getCategoria()));
                System.out.println(lista_sources.get(i).getIdioma().equals(lista_sources_viejos.get(i).getIdioma()));
                //System.out.println(lista_sources.get(i).getImg().equals(lista_sources_viejos.get(i).getImg()));
                System.out.println(lista_sources.get(i).getURLPagina().equals(lista_sources_viejos.get(i).getURLPagina()));*/

                if (lista_sources.get(i).getNombre().equals(lista_sources_viejos.get(i).getNombre()) && lista_sources.get(i).getURL().equals(lista_sources_viejos.get(i).getURL())
                        && lista_sources.get(i).getCategoria().equals(lista_sources_viejos.get(i).getCategoria()) && lista_sources.get(i).getIdioma().equals(lista_sources_viejos.get(i).getIdioma())
                        && lista_sources.get(i).getURLPagina().equals(lista_sources_viejos.get(i).getURLPagina()))
                    bandera = true;
            }

        return bandera;
    }

    // sirve para llamar otro activity, de una forma mas ordenada
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

    // clase asincrona que cargar el contenido desde los urls dados
	private class AsyncLoadXMLFeed extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... params) {

			// Obtain feed
			DOMParser myParser = new DOMParser();

            //esto sirve para que recolecte todos los links

            for (int i = 0; i < lista_sources.size(); i++)
            {
                if (lista_sources.size() > 0) {
                    FeedSource s = lista_sources.get(i);
                    if (s.isAceptado())
                        feed = myParser.parseXml(s.getURL(), s.getNombre());
                }
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

	// escribe los sources en un archivo de bitacora
	private void WriteFeed(RSSFeed data) {

		FileOutputStream fOut = null;
		ObjectOutputStream osw;

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

    // otro metodo para escribir bitacoras
    public void escribirRegistro(String file_name) {

        FileOutputStream fOut = null;
        ObjectOutputStream osw;

        try {
            fOut = openFileOutput(file_name, MODE_PRIVATE);
            osw = new ObjectOutputStream(fOut);

            for (int i = 0; i < lista_sources.size(); i++) {
                FeedSource s = lista_sources.get(i);
                osw.write(s.getURL().getBytes());
                osw.write(";".getBytes());
                osw.write(s.getNombre().getBytes());
                osw.write(";".getBytes());
                osw.write(s.getCategoria().getBytes());
                osw.write(";".getBytes());
                osw.write(s.getIdioma().getBytes());
                osw.write(";".getBytes());
                osw.write(s.getURLPagina().getBytes());
                osw.write(";".getBytes());
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
    public ArrayList<FeedSource> leerRegistros(String fName) {

        FileInputStream fIn = null;
        ObjectInputStream isr = null;
        ArrayList<FeedSource> s = new ArrayList<FeedSource>();
        FeedSource sour;
        String texto = "";
        File feedFile = getBaseContext().getFileStreamPath(fName);
        if (!feedFile.exists())
            return null;

        try {
            fIn = openFileInput(fName);
            isr = new ObjectInputStream(fIn);

            //System.out.println("Listasourcessize: " + lista_sources.size());
            for (int i = 0; i < lista_sources.size(); i++)
            {
                texto = isr.readLine();
                sour = crearListaDinamica(texto);
                s.add(sour);
                //System.out.println("Desde SplashActivity, la linea leida es: " + texto);
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

    // crea una lista, desde los archivos de bitacora leidos
    // esta lista sirve para saber si previamente un source habia sido aceptado o no
    private FeedSource crearListaDinamica(String linea)
    {
        FeedSource sour = new FeedSource();

        String[] partes = linea.split(";");
        sour.setURL(partes[0]);
        sour.setNombre(partes[1]);
        sour.setCategoria(partes[2]);
        sour.setIdioma(partes[3]);
        sour.setURLPagina(partes[4]);

        if (linea.contains("trueverdadero"))
            sour.setAceptado(true);

        else
            sour.setAceptado(false);

        return sour;
    }

	// Metodo para leer archivo de bitacora
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

    private class getAllSources extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            // Creating service handler class instance
            JSONParser sh = new JSONParser(new JSONObject());
            boolean esSeguro = false;

            // Making a request to url and getting response
            String jsonStr = sh.makeServiceCall(url2, JSONParser.GET);

            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);

                    // Getting JSON Array node
                    fuentessr = jsonObj.getJSONArray(TAG_SOURCES);

                    // looping through All Contacts
                    for (int i = 0; i < fuentessr.length(); i++) {
                        JSONObject c = fuentessr.getJSONObject(i);

                        System.out.println("Sources recolectados desde archivo JSON: " + c.getString("nombre"));

                        FeedSource s = new FeedSource();
                        s.setURL(c.getString(TAG_URL));
                        s.setURLPagina(c.getString(TAG_URL_PAGINA));
                        s.setNombre(c.getString(TAG_NOMBRE));
                        s.setCategoria(c.getString(TAG_CATEGORIA));
                        s.setIdioma(c.getString(TAG_IDIOMA));

                        lista_sources.add(s);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
               // Log.e("ServiceHandler", "Couldn't get any data from the url");
            }
            sinterminar = false;

            //

            lista_sources_viejos2 = leerRegistros(sourceName);

            if (lista_sources_viejos2 != null && lista_sources_viejos2.size() != 0)
                esSeguro = true;
            if (esSeguro)
            {
                for (int i = 0; i < lista_sources_viejos2.size(); i++)
                {
                    FeedSource g = lista_sources_viejos2.get(i);

                    for (int j = 0; j < lista_sources.size(); j++)
                    {
                        FeedSource h = lista_sources.get(i);

                        /*System.out.println("URLs iguales: " + g.getURL().equals(h.getURL()));
                        System.out.println("URLs Pagina iguales: " + g.getURLPagina().equals(h.getURLPagina()));
                        System.out.println("Nombres iguales: " + g.getNombre().equals(h.getNombre()));
                        System.out.println("Categorias iguales: " + g.getCategoria().equals(h.getCategoria()));
                        System.out.println("Idiomas iguales: " + g.getIdioma().equals(h.getIdioma()));
                        System.out.println("El source feed de lista_sources_viejos2, de nombre: " + g.getNombre() + " fue aceptado: " + g.isAceptado());*/

                        if (g.getURL().equals(h.getURL()) && g.getURLPagina().equals(h.getURLPagina()) && g.getNombre().equals(h.getNombre())
                                && g.getCategoria().equals(h.getCategoria()) && g.getIdioma().equals(h.getIdioma()) && g.isAceptado())
                        {
                            h.setAceptado(true);
                            //System.out.println("Fue aceptado el feed source de nombre: " + g.getNombre());
                        }
                    }


                }
            }

            //
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            sinterminar = false;
            // Dismiss the progress dialog

        }

    }

}
