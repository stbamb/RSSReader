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

        String theverge_burner = "http://feeds.feedburner.com/theverge/MJyr";
        String polygon_burner = "http://feeds.feedburner.com/polygon/yhac";
        String theverge = "http://www.theverge.com/rss/frontpage";
        String polygon = "http://www.polygon.com/rss/index.xml";

        URL url = null;
        //System.out.println(xml);
        try {
            url = new URL(xml);
        } catch (MalformedURLException e1) {
            //e1.printStackTrace();
        }

        if (xml == theverge || xml == theverge_burner || xml == polygon || xml == polygon_burner)
            leerPolygonYTheVerge(url, nombre);
        else
            leerOtrosFeed(url, nombre);


        // Return the final feed once all the Items are added to the RSSFeed
        // Object(_feed).
        return _feed;
    }

    private static String getValue(String tag, Element element) {
        NodeList nodeList = element.getElementsByTagName(tag).item(0).getChildNodes();
        Node node = (Node) nodeList.item(0);
        return node.getNodeValue();
    }

    private void leerOtrosFeed(URL url, String nombre)
    {
        try {
            // Create required instances
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();

            // Parse the xml
            Document doc = db.parse(new InputSource(url.openStream()));
            doc.getDocumentElement().normalize();

            // Get all <item> tags.
            NodeList nl = doc.getElementsByTagName("item");
            int length = nl.getLength();

            for (int i = 0; i < length; i++) {
                Node currentNode = nl.item(i);
                RSSItem _item = new RSSItem();

                NodeList nchild = currentNode.getChildNodes();
                int clength = nchild.getLength();

                // Get the required elements from each Item
                for (int j = 0; j < clength; j = j + 1) {

                    Node thisNode = nchild.item(j);
                    String theString = null;
                    String nodeName = thisNode.getNodeName();

                    theString = nchild.item(j).getFirstChild().getNodeValue();

                    if (theString != null) {
                        if ("title".equals(nodeName)) {
                            // Node name is equals to 'title' so set the Node
                            // value to the Title in the RSSItem.
                            _item.setTitle(theString);
                        }

                        else if ("description".equals(nodeName)) {
                            _item.setDescription(theString);

                            // Parse the html description to get the image url
                            String html = theString;
                            org.jsoup.nodes.Document docHtml = Jsoup
                                    .parse(html);
                            Elements imgEle = docHtml.select("img");
                            _item.setImage(imgEle.attr("src"));
                        }

                        else if ("link".equals(nodeName)) {
                            _item.setLink(theString);
                        }

                        else if ("pubDate".equals(nodeName)) {

                            // We replace the plus and zero's in the date with
                            // empty string
                            String formatedDate = theString.replace(" +0000",
                                    "");
                            _item.setDate(formatedDate);
                        }

                    }
                }

                // add item to the list
                _feed.addItem(_item);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void leerPolygonYTheVerge(URL url, String nombre)
    {
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
    }
}
