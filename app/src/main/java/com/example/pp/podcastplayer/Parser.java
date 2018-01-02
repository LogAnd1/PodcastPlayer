package com.example.pp.podcastplayer;

import android.os.AsyncTask;
import android.util.Log;
import android.util.Xml;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Logar on 28.12.2017.
 */

public class Parser {

    DataRSS data;
    String urlRSS;


    // Razclenjevanje podatkov
    public DataRSS parseData(InputStream inputStream) throws XmlPullParserException,
            IOException {

        String naslov = null;
        String slika = null;
        String opis = null;

        boolean itemObj = false;
        DataRSS item = new DataRSS(naslov,slika, opis);

        //List<DataRSS> items = new ArrayList<>();

        try {
            // Uporabimo xmlPullParser
            XmlPullParser xmlPullParser = Xml.newPullParser();
            xmlPullParser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            xmlPullParser.setInput(inputStream, null);

            // Pregledujemo znacke do konca dokumenta
            xmlPullParser.nextTag();
            while (xmlPullParser.next() != XmlPullParser.END_DOCUMENT) {
                // prebermo tip dogodka (zacetek dokumenta, konec)
                int event = xmlPullParser.getEventType();

                // Preberemo ime znacke (item)
                String tagName = xmlPullParser.getName();

                if( tagName == null ) {
                    continue;
                }

                // Zacetek dokumenta
                if (event == XmlPullParser.START_TAG) {
                    if(tagName.equalsIgnoreCase("channel")) {
                        itemObj = true;
                        continue;
                    }
                }

                // Konec dokumenta
                if(event == XmlPullParser.END_TAG) {
                    if(tagName.equalsIgnoreCase("channel")) {
                        itemObj = false;
                    }
                    continue;
                }


                //  Log.d("MyXmlParser", "Parsing name ==> " + name);
                String res = "";


                if (xmlPullParser.next() == XmlPullParser.TEXT) {
                    res = xmlPullParser.getText();
                    xmlPullParser.nextTag();
                }

                // Pridobivanje podatkov iz znack

                switch (tagName){
                    case "title":
                        //Log.d("Add", res);
                        naslov = res;

                        break;
                     case "itunes:image":
                         slika = xmlPullParser.getAttributeValue(null, "href");
                       // Log.d("Add", slika);
                        break;
                    case "description":
                        opis = res;
                        break;
                }


                if (naslov != null && opis != null && slika != null) {
                    if(itemObj) {
                        // Log.d("Add", naslov + opis);
                        // Dodamo oddajo samo v primeru da so vse ustrezne znacke pridobljene
                        item = new DataRSS(naslov, slika, opis);
                    }

                    naslov = null;
                    slika = null;
                    opis = null;

                    itemObj = false;
                }
            }

            return item;
        } finally {
            inputStream.close();
        }
    }

}
