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
    DataRSS prva;
    String urlRSS;


    // Razclenjevanje podatkov
    public DataRSS parseData(InputStream inputStream) throws XmlPullParserException,
            IOException {

        String naslov = null;
       // String slika = null;
        String opis = null;

        boolean itemObj = false;
        DataRSS item = new DataRSS(naslov,null, opis);

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
                //todo: Dodaj iskanje funkcijo za pridobvanje slike in dolzine podcasta

                switch (tagName){
                    case "title":
                        //Log.d("Add", res);
                        naslov = res;

                        break;
                     /*case "enclosure":
                        mp3 = xmlPullParser.getAttributeValue(null, "url");;
                        //Log.d("Add", mp3);
                        break; */
                    case "description":
                        opis = res;
                        break;
                }

                //todo: Poskrbi da se naslov podcast strani shrani posebaj
                if (naslov != null && opis != null) {
                    if(itemObj) {
                       // Log.d("Add", naslov + opis);
                        // Dodamo oddajo samo v primeru da so vse ustrezne znacke pridobljene
                        item = new DataRSS(naslov, "test", opis);
                    }

                    naslov = null;
                    //mp3 = null;
                    opis = null;

                    itemObj = false;
                }
            }

            return item;
        } finally {
            inputStream.close();
        }
    }
/*
    // Izvajanje kode v ozadju (pridobivanje podatkov)
    private class FetchFeedTask extends AsyncTask<Void, Void, Boolean> {

        private String url;

        @Override
        // Pridobimo url
        protected void onPreExecute() {
            url = urlRSS;
            //Log.d("Add", url);
        }

        @Override
        protected Boolean doInBackground(Void... voids) {

            try {
                //Log.d("Add", url);
                URL urlConnection = new URL(url);
                InputStream inputStream = urlConnection.openConnection().getInputStream();
                // Razclenimo podatke iz povezave
                data = parseData(inputStream);
                Log.d("Add", data.naslov);
               // Log.d("Add", data.naslov);
                return true;

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (XmlPullParserException e) {
                e.printStackTrace();
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean success) {
            if (success) {
                // Shranjevanje podatkov
                 prva = data;

            } else {

            }
        }
    } */

}
