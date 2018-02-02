package com.radio.daniel.radio;

import android.content.Context;

public class SongMetaDate {

    private String title = "";
    private String artist = "";
    private String radioStation = "";

    public SongMetaDate(){}

    public SongMetaDate(SongMetaDate songMetaDate) {
        this.title = songMetaDate.title;
        this.artist = songMetaDate.getArtist();
        this.radioStation = songMetaDate.getRadioStation();
    }

    public String toString(){
        return "title: " + title + "\nartist: " + artist + "\nradio station: " + radioStation;
    }


    public SongMetaDate(String title, String artist){
        this.title = title;
        this.artist = artist;
    }

    public String getTitle(Context context) {

        if(title.equals(""))
            return context.getString(R.string.missing_title);

        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getRadioStation() {
        return radioStation;
    }

    public void setRadioStation(String radioStation) {
        this.radioStation = radioStation;
    }

}
