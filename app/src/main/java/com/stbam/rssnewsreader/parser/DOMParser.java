package com.stbam.rssnewsreader.parser;

import android.util.Log;

import com.stbam.rssnewsreader.SplashActivity;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.jsoup.Jsoup;
import org.jsoup.select.Elements;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

/**
 * Created by Esteban on 07-Oct-14.
 */
public class DOMParser {

    private RSSFeed _feed = new RSSFeed();

    public RSSFeed parseXml(String xml, String nombre) {

        URL url = null;
        try {
            url = new URL(xml);
        } catch (MalformedURLException e1) {
            //e1.printStackTrace();
        }

        try {
            // Create required instances
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();

            // Parse the xml
            Document doc = db.parse(new InputSource(url.openStream()));
            doc.getDocumentElement().normalize();

            // Get all <item> tags.
            NodeList nl = doc.getElementsByTagName("entry");
            int largo = nl.getLength();


            for (int i = 0; i < largo; i++)
            {
                Node entrada = nl.item(i);

                if (entrada.getNodeType() == Node.ELEMENT_NODE) {

                    Element element2 = (Element) entrada;

                    RSSItem entry = new RSSItem();

                    entry.setTitle(getValue("title", element2));
                    entry.setDescription(getValue("content", element2));
                    entry.setDate(getValue("published", element2));
                    entry.setLink(getValue("id", element2));

                    // esto sirve para sacar el link de la imagen

                    String html = entry.getDescription();
                    org.jsoup.nodes.Document docHtml = Jsoup
                            .parse(html);
                    Elements imgEle = docHtml.select("img");
                    entry.setImage(imgEle.attr("src"));
                    entry.set_source_page(nombre);

                    //Log.d("Formato fecha:", entry.getDate());

                    // se agrega el item recien creado a la lista para devolverla
                    _feed.addItem(entry);

                }
            }


        } catch (Exception e) {
            e.printStackTrace();
        }



        // Return the final feed once all the Items are added to the RSSFeed
        // Object(_feed).
        return _feed;
    }

    private static String getValue(String tag, Element element) {
        NodeList nodeList = element.getElementsByTagName(tag).item(0).getChildNodes();
        Node node = (Node) nodeList.item(0);
        return node.getNodeValue();
    }
}
