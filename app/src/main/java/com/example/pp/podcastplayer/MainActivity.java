package com.example.pp.podcastplayer;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import org.xmlpull.v1.XmlPullParserException;

import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private static String LOG_TAG = "CardViewActivity";
    String urlRSS;
    DataRSS data;
    String naslov;
    String opis;
    String slika_url;
    String[] slika;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadStart();
        int permRead = checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE);
        int permWrite = checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permRead != PackageManager.PERMISSION_GRANTED || permWrite != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    this, // Aktivnost, ki zahteva pravice.
                    new String[]{ // Tabela zahtevanih pravic.
                            Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE
                    },
                    221
            );
        } else {
            loadMain();
        }

        // Branje iz datoteke (linki do podcastov)
        String line = "";
        try {
            FileInputStream inputStream = openFileInput("links.txt");
            Scanner scanner = new Scanner(inputStream);
            while (scanner.hasNext()) {
                line = scanner.next();

                urlRSS = line;
                new FetchFeedTask().execute((Void) null);

            }
            scanner.close();
            inputStream.close();
        } catch (IOException | InputMismatchException e) {
            Log.d("IO Error", "Branje neuspešno.");
            e.printStackTrace();
        }

    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    // Menu funkcionalnosti
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        // Odpiranje download zaslona iz menu-ja
        else if (id == R.id.action_add) {
            Intent intent = new Intent(this, AddActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_manage) {
            // Handle action
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void loadMain() {
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        //mAdapter = new MyRecyclerViewAdapter(getDataSet());
        //mRecyclerView.setAdapter(mAdapter);
    }

    public void loadStart() {
        setContentView(R.layout.activity_main2);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode, // koda zahtevka
            String permissions[], // tabela zahtevanih pravic
            int[] grantResults) // tabela odobritev
    {
        if (requestCode == 221) { // Če je št. zahtevka enaka 1234.
            if (grantResults.length == 0) {
            } else {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    loadMain();
                }
            }
        }
    }

    public void tryAgain(View view) {
        int permRead = checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE);
        int permWrite = checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permRead != PackageManager.PERMISSION_GRANTED || permWrite != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    this, // Aktivnost, ki zahteva pravice.
                    new String[]{ // Tabela zahtevanih pravic.
                            Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE
                    },
                    221
            );
        }
    }

    ArrayList results = new ArrayList<DataObject>();

    private ArrayList<DataObject> getDataSet() {
        int i = 0;


        DataObject obj = new DataObject(naslov, opis, slika[slika.length - 1]);
        results.add(i, obj);
        i++;


        return results;
    }


    // Izvajanje kode v ozadju (pridobivanje podatkov)
    private class FetchFeedTask extends AsyncTask<Void, Void, Boolean> {

        private String url;
        int i = 0;
        Parser p = new Parser();

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
                data = p.parseData(inputStream);
                //Log.d("Add", data.naslov);
                // Log.d("Add", data.naslov);


                naslov = data.naslov;
                opis = data.opis;

                slika_url = data.slika;
                Log.d("aaa", slika_url);

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

