package com.example.pp.podcastplayer;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class MyRecyclerViewAdapter extends RecyclerView
        .Adapter<MyRecyclerViewAdapter
        .DataObjectHolder> {
    private static String LOG_TAG = "MyRecyclerViewAdapter";
    private ArrayList<DataObject> mDataset;
    private static MyClickListener myClickListener;

    public static class DataObjectHolder extends RecyclerView.ViewHolder
            implements View
            .OnClickListener {
        TextView label;
        TextView dateTime;
        ImageView img;
        TextView url;
        String znacka;
        String image;
        View Vv;


        public DataObjectHolder(View itemView) {
            super(itemView);
            label = (TextView) itemView.findViewById(R.id.textView);
            url =  (TextView) itemView.findViewById(R.id.textView3);
            dateTime = (TextView) itemView.findViewById(R.id.textView2);
            img = (ImageView)  itemView.findViewById(R.id.imageView2);
            znacka = "";
            image="";
            Log.i(LOG_TAG, "Adding Listener");
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
              //  myClickListener.onItemClick(getAdapterPosition(), v);
            Vv=v;
            if (!znacka.equals("skupina")) {

                // Preverimo ce mp3 datoteka ze obstaja
                String urlMP3 =  url.getText().toString();
                String[] nameMP3 = urlMP3.split("/"); // Zadnji element je ime datoteke

                String PATH = Environment.getExternalStorageDirectory().toString()+ "/downloads/mp3/" + nameMP3[nameMP3.length-1];
                String PATH2 = Environment.getExternalStorageDirectory().toString()+ "/downloads/mp3";
                File file = new File(PATH);
                if (!file.exists()) {
                    try {
                        //Downloader d = new Downloader();
                        //String code = d.DownloadFile(urlMP3, "downloads/mp3", nameMP3[nameMP3.length - 1]);
                        //downloadFile(urlMP3, nameMP3[nameMP3.length - 1],PATH2);
                        DownloadObjects Doo = new DownloadObjects(v,urlMP3,nameMP3[nameMP3.length - 1],label.getText().toString(),image);
                        new DownloadFilesTask().execute(Doo);

                    } catch (Exception e) {
                       // Log.d("d", e.getMessage());
                        Log.d("d", nameMP3[nameMP3.length - 1]);
                    }

                } else {


                    Intent intent = new Intent(v.getContext(), MP3Activity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("mp3",(String) PATH);
                    bundle.putString("naslov",(String) label.getText().toString());
                    String PATH3 = Environment.getExternalStorageDirectory().toString()+ "/downloads/images/" + image;
                    bundle.putString("slika",(String) PATH3);

                    intent.putExtras(bundle);
                    v.getContext().startActivity(intent);

                }



            } else {
                Intent intent = new Intent(v.getContext(), PodcastList.class);
                Bundle bundle = new Bundle();
                bundle.putString("url",(String) url.getText());
                bundle.putString("slika",(String) image);
                intent.putExtras(bundle);
                v.getContext().startActivity(intent);
            }
        }
    }

    public void setOnItemClickListener(MyClickListener myClickListener) {
        this.myClickListener = myClickListener;
    }

    public MyRecyclerViewAdapter(ArrayList<DataObject> myDataset) {
        mDataset = myDataset;
    }

    @Override
    public DataObjectHolder onCreateViewHolder(ViewGroup parent,
                                               int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_view_row, parent, false);

        DataObjectHolder dataObjectHolder = new DataObjectHolder(view);
        return dataObjectHolder;
    }

    @Override
    public void onBindViewHolder(DataObjectHolder holder, int position) {
        holder.label.setText(mDataset.get(position).getmText1());
        holder.dateTime.setText(mDataset.get(position).getmText2());
        Bitmap bitmap = BitmapFactory.decodeFile(Environment.getExternalStorageDirectory().getAbsolutePath()+"/downloads/images/"+mDataset.get(position).getmImg1());
        holder.img.setImageBitmap(bitmap);
        holder.url.setText(mDataset.get(position).getmUrl());
        holder.znacka = mDataset.get(position).getmZnacka();
        holder.image = mDataset.get(position).getmImg1();
    }

    public void addItem(DataObject dataObj, int index) {
        mDataset.add(index, dataObj);
        notifyItemInserted(index);
    }

    public void deleteItem(int index) {
        mDataset.remove(index);
        notifyItemRemoved(index);
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    public interface MyClickListener {
        public void onItemClick(int position, View v);
    }

    static void downloadFile(String dwnload_file_path, String fileName,
                             String pathToSave) {
        int downloadedSize = 0;
        int totalSize = 0;

        try {
            URL url = new URL(dwnload_file_path);
            HttpURLConnection urlConnection = (HttpURLConnection) url
                    .openConnection();

            urlConnection.setRequestMethod("POST");
            urlConnection.setDoOutput(true);

            // connect
            urlConnection.connect();

            File myDir;
            myDir = new File(pathToSave);
            myDir.mkdirs();

            // create a new file, to save the downloaded file

            String mFileName = fileName;
            File file = new File(myDir, mFileName);

            FileOutputStream fileOutput = new FileOutputStream(file);

            // Stream used for reading the data from the internet
            InputStream inputStream = urlConnection.getInputStream();

            // this is the total size of the file which we are downloading
            totalSize = urlConnection.getContentLength();

            // runOnUiThread(new Runnable() {
            // public void run() {
            // pb.setMax(totalSize);
            // }
            // });

            // create a buffer...
            byte[] buffer = new byte[1024];
            int bufferLength = 0;

            while ((bufferLength = inputStream.read(buffer)) > 0) {
                fileOutput.write(buffer, 0, bufferLength);
                downloadedSize += bufferLength;
                // update the progressbar //
                // runOnUiThread(new Runnable() {
                // public void run() {
                // pb.setProgress(downloadedSize);
                // float per = ((float)downloadedSize/totalSize) * 100;
                // cur_val.setText("Downloaded " + downloadedSize + "KB / " +
                // totalSize + "KB (" + (int)per + "%)" );
                // }
                // });
            }
            // close the output stream when complete //
            fileOutput.close();
            // runOnUiThread(new Runnable() {
            // public void run() {
            // // pb.dismiss(); // if you want close it..
            // }
            // });

        } catch (final MalformedURLException e) {
            Log.d("Error","Error : MalformedURLException " + e);
            e.printStackTrace();
        } catch (final IOException e) {
            Log.d("Error","Error : IOException " + e);
            e.printStackTrace();
        } catch (final Exception e) {
            Log.d("Error","Error : Please check your internet connection " + e);
        }
    }
    public static class DownloadObjects {
        private View v;
        private String url;
        private String name;
        private String naslov;
        private String image;

        DownloadObjects(View v, String url, String name, String naslov, String image){
            this.v=v;
            this.url=url;
            this.name=name;
            this.naslov=naslov;
            this.image=image;
        }
    }

    private static class DownloadFilesTask extends AsyncTask<DownloadObjects, Integer, Long> {
        View vi;
        String naslov;
        String name;
        String image;
        protected Long doInBackground(DownloadObjects... object) {
            int count = object.length;
            long totalSize = 0;
            for (int i = 0; i < count; i++) {
                Downloader d = new Downloader();
                String code = d.DownloadFile(object[i].url, "downloads/mp3", object[i].name);
                vi=object[i].v;
                naslov=object[i].naslov;
                name=object[i].name;
                image=object[i].image;
                if (isCancelled()) break;
            }
            return totalSize;
        }

        protected void onProgressUpdate(Integer... progress) {
        }

        protected void onPostExecute(Long result) {
            Intent intent = new Intent(vi.getContext(), MP3Activity.class);
            Bundle bundle = new Bundle();
            String PATH = Environment.getExternalStorageDirectory().toString()+ "/downloads/mp3/" + name;
            bundle.putString("mp3",(String) PATH);
            bundle.putString("naslov",naslov);
            String PATH3 = Environment.getExternalStorageDirectory().toString()+ "/downloads/images/" + image;
            bundle.putString("slika",(String) PATH3);

            intent.putExtras(bundle);
            vi.getContext().startActivity(intent);





        }
    }
}
