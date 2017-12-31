package com.example.pp.podcastplayer;

/**
 * Created by Luka on 28. 12. 2017.
 */

public class DataObject {
    private String mText1;
    private String mText2;
    private String mImg1;

    DataObject (String text1, String text2, String img1){
        mText1 = text1;
        mText2 = text2;
        mImg1 = img1;
    }

    public String getmText1() {
        return mText1;
    }

    public void setmText1(String mText1) {
        this.mText1 = mText1;
    }

    public String getmText2() {
        return mText2;
    }

    public void setmText2(String mText2) {
        this.mText2 = mText2;
    }

    public String getmImg1() {
        return mImg1;
    }

    public void setmImg1(String mImg1) {
        this.mImg1 = mImg1;
    }
}