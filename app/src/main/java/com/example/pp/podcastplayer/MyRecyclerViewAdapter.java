package com.example.pp.podcastplayer;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
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


        public DataObjectHolder(View itemView) {
            super(itemView);
            label = (TextView) itemView.findViewById(R.id.textView);
            url =  (TextView) itemView.findViewById(R.id.textView3);
            dateTime = (TextView) itemView.findViewById(R.id.textView2);
            img = (ImageView)  itemView.findViewById(R.id.imageView2);
            znacka = "";
            Log.i(LOG_TAG, "Adding Listener");
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
              //  myClickListener.onItemClick(getAdapterPosition(), v);

            if (!znacka.equals("skupina")) {

                // Preverimo ce mp3 datoteka ze obstaja
                String urlMP3 =  url.getText().toString();
                Log.d("d", urlMP3);
                String[] nameMP3 = urlMP3.split("/"); // Zadnji element je ime datoteke

                String PATH = Environment.getExternalStorageDirectory().toString()+ "/downloads/mp3/" + nameMP3[nameMP3.length-1];
                Log.d("d", PATH);
                File file = new File(PATH);
                if (!file.exists()) {
                    try {
                        Downloader d = new Downloader();
                        String code = d.DownloadFile(urlMP3, "downloads/mp3", nameMP3[nameMP3.length - 1]);
                    } catch (Exception e) {
                       // Log.d("d", e.getMessage());
                        Log.d("d", nameMP3[nameMP3.length - 1]);
                    }

                } else {

                    Intent intent = new Intent(v.getContext(), MP3Activity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("path_mp3",(String) PATH);
                    bundle.putString("naslov",(String) label.getText().toString());
                   // bundle.putString("path_slika",(String) url.getText());

                    intent.putExtras(bundle);
                    v.getContext().startActivity(intent);

                }



            } else {
                Intent intent = new Intent(v.getContext(), PodcastList.class);
                Bundle bundle = new Bundle();
                bundle.putString("url",(String) url.getText());
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
}
