package com.example.pp.podcastplayer;

import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.util.Xml;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class AddActivity extends AppCompatActivity {

    private String urlRSS = "";
    private List<DataRSS> data;


    // Razclenjevanje podatkov
    public List<DataRSS> parseData(InputStream inputStream) throws XmlPullParserException,
            IOException {

        String naslov = "";
        String mp3 = "";
        String opis = "";

        boolean itemObj = false;

        List<DataRSS> items = new ArrayList<>();

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
                    if(tagName.equalsIgnoreCase("item")) {
                        itemObj = true;
                        continue;
                    }
                }

                // Konec dokumenta
                if(event == XmlPullParser.END_TAG) {
                    if(tagName.equalsIgnoreCase("item")) {
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
                    case "enclosure":
                        mp3 = xmlPullParser.getAttributeValue(null, "url");;
                        //Log.d("Add", mp3);
                        break;
                    case "description":
                        opis = res;
                        break;
                }

                //todo: Poskrbi da se naslov podcast strani shrani posebaj
                if (naslov != null && mp3 != null && opis != null) {
                    if(itemObj) {
                        // Dodamo oddajo samo v primeru da so vse ustrezne znacke pridobljene
                        DataRSS item = new DataRSS(naslov, mp3, opis);
                        items.add(item);
                    }

                    naslov = null;
                    mp3 = null;
                    opis = null;

                    itemObj = false;
                }
            }

            return items;
        } finally {
            inputStream.close();
        }
    }

    

    // Izvajanje kode v ozadju (pridobivanje podatkov)
    private class FetchFeedTask extends AsyncTask<Void, Void, Boolean> {

        private String url;

        @Override
        // Pridobimo url
        protected void onPreExecute() {
            url = urlRSS;
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            // Preverimo pravilen vnos url-ja
            if ((TextUtils.isEmpty(url)) || (url.length() < 3))
                return false;

            try {
                // Preverimo začetek url-ja
                if(!url.startsWith("https://") && !url.startsWith("http://"))
                    url = "http://" + url;

                //Log.d("Add", url);
                URL urlConnection = new URL(url);
                InputStream inputStream = urlConnection.openConnection().getInputStream();
                // Razclenimo podatke iz povezave
                data = parseData(inputStream);
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

                DataRSS prva = data.get(0);

                String test = prva.naslov;

                // Izpis prvege oddaje test
                Log.d("Add", test);


                Toast.makeText(AddActivity.this,
                        "Podcast successfully added!",
                        Toast.LENGTH_LONG).show();

               /*  mRecyclerView.setAdapter(new DataRSSAdapter(mFeedModelList)); */

            } else {
                // V primeru ne uspešnega pridobivanja podatkov
                Toast.makeText(AddActivity.this,
                        "Please enter a valid RSS feed url!",
                        Toast.LENGTH_LONG).show();
            }
        }
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        Button gumbRSS = (Button) findViewById(R.id.button_rss);

        gumbRSS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final EditText txtUrl = new EditText(AddActivity.this);

                txtUrl.setHint("http://");

                new AlertDialog.Builder(AddActivity.this)
                        .setTitle("Add new podcast")
                        .setMessage("Please add RSS url feed")
                        .setView(txtUrl)
                        .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                urlRSS = txtUrl.getText().toString();
                                // Pridobivanje podatkov
                                new FetchFeedTask().execute((Void) null);

                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                            }
                        })
                        .show();
            }
        });

    }

}
