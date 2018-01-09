package com.example.pp.podcastplayer;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class PodcastList extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private static String LOG_TAG = "CardViewActivity";

    DataRSSmp3 data;

    String naslov;
    String urlMP3;
    String opis;
    String[] slika;
    String urlRSS;
    String slika_url;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_podcast_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
       // String myString = "";
        Intent intent = getIntent();

        loadStart();

        if (intent != null) {
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                urlRSS = bundle.getString("url");
                if (urlRSS == null) {
                    urlRSS = "Bongo";
                } else {
                    new PodcastList.FetchFeedTask().execute((Void) null);
                }
            }
        }
        //TextView tv = (TextView) findViewById(R.id.textView3);
        //tv.setText(myString);


    }

    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }

    public void loadStart() {

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        setContentView(R.layout.activity_main);


        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

    }


    ArrayList results = new ArrayList<DataObject>();

    private ArrayList<DataObject> getDataSet() {
        int i = 0;

        DataObject obj = new DataObject(naslov, opis, slika[slika.length - 1], urlMP3);
        results.add(i, obj);
        i++;

        return results;
    }


    // Izvajanje kode v ozadju (pridobivanje podatkov)
    private class FetchFeedTask extends AsyncTask<Void, Void, Boolean> {

        private String url;
        int i = 0;
        ParserMP3 pMP3 = new ParserMP3();

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
                data = pMP3.parseData(inputStream);
                //Log.d("Add", data.naslov);
                // Log.d("Add", data.naslov);


                naslov = data.naslov;
                opis = data.opis;
                urlMP3 = data.mp3;


                Log.d("d", urlMP3);

                if (slika_url == null) {
                    slika_url = "https://upload.wikimedia.org/wikipedia/commons/c/c9/Moon.jpg";
                    slika = slika_url.split("/"); // Zadnji element je ime slike
                    // String str_result= new Downloadimages().execute(slika_url).get();
                    Downloader d = new Downloader();
                    String code = d.DownloadFile(slika_url, "downloads/images", slika[slika.length - 1]);

                } else {
                    slika = slika_url.split("/");
                    // String str_result= new Downloadimages().execute(slika_url).get();
                    Downloader d = new Downloader();
                    String code = d.DownloadFile(slika_url, "downloads/images", slika[slika.length - 1]);
                }

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

                mAdapter = new MyRecyclerViewAdapter(getDataSet());
                mRecyclerView.setAdapter(mAdapter);

            }
        }
    }

}
