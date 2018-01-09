package com.example.pp.podcastplayer;

import android.os.Environment;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;


public class Downloader {

    public static String DownloadFile(String url2, String path, String name){
        int count;
        try {
            System.out.println(url2);
            URL url = new URL((String) url2);

            String PATH = Environment.getExternalStorageDirectory().toString()+ "/"+path+"/"+name;
            System.out.println(PATH);
            File folder = new File(PATH);
            if(!folder.exists()){
               folder.getParentFile().mkdirs();//If there is no folder it will be created.
            }

            URLConnection conection = url.openConnection();
            conection.connect();

            // this will be useful so that you can show a tipical 0-100%
            // progress bar
            int lenghtOfFile = conection.getContentLength();
            System.out.println(lenghtOfFile);

            // download the file
            InputStream input = new BufferedInputStream(url.openStream());

            // Output stream
            OutputStream output = new FileOutputStream(PATH);

            byte data[] = new byte[4096];

            long total = 0;

            while ((count = input.read(data)) != -1) {
                total += count;
                // publishing the progress....
                // After this onProgressUpdate will be called

                // writing data to file
                output.write(data, 0, count);
            }

            // flushing output
            output.flush();

            // closing streams
            output.close();
            input.close();

            return "Done";

        } catch (Exception e) {
            Log.e("Error: "," " +  e.getMessage());
        }
        return "Fail";
    }
}
