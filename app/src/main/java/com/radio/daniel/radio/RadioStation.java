package com.radio.daniel.radio;

import com.google.gson.annotations.SerializedName;



public class RadioStation {


    @SerializedName("name")
    private String name;
    @SerializedName("imageURL")
    private String imageURL = "";
    @SerializedName("URL")
    private String URL;
    @SerializedName("streamURL")
    private String streamURL;
    @SerializedName("lowQualityStreamURL")
    private String lowQualityStreamURL;
    @SerializedName("favourite")
    private boolean favourite;
    @SerializedName("hidden")
    private boolean hidden;
    @SerializedName("databaseVersionAdded")
    private int databaseVersionAdded;

    public RadioStation(String name, String imageURL, String URL, String streamURL, String lowQualityStreamURL, boolean favourite, boolean hidden, int databaseVersionAdded) {
        this.name = name;
        this.imageURL = imageURL;
        this.URL = URL;
        this.streamURL = streamURL;
        this.lowQualityStreamURL = lowQualityStreamURL;
        this.favourite = favourite;
        this.hidden = hidden;
        this.databaseVersionAdded = databaseVersionAdded;
    }

    public RadioStation() {

    }

    public String toString() {
        return "name: " + name + "\nimageURL: " + imageURL + "\nURL: " + URL
                + "\nstreamURL: " + streamURL + "\nlqStreamURL: " + streamURL
                + "\nfavourite: " + favourite + "\nhidden: " + hidden
                + "\ndatabaseVersionAdded: " + databaseVersionAdded;

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public String getURL() {
        return URL;
    }

    public void setURL(String URL) {
        this.URL = URL;
    }

    public String getStreamURL() {
        return streamURL;
    }

    public void setStreamURL(String streamURL) {
        this.streamURL = streamURL;
    }

    public boolean isFavourite() {
        return favourite;
    }

    public void setFavourite(boolean favourite) {
        this.favourite = favourite;
    }


    public String getLowQualityStreamURL() {
        return lowQualityStreamURL;
    }

    public void setLowQualityStreamURL(String lowQualityStreamURL) {
        this.lowQualityStreamURL = lowQualityStreamURL;
    }

    public boolean isHidden() {
        return hidden;
    }

    public void setHidden(boolean hidden) {
        this.hidden = hidden;
    }


    public int getDatabaseVersionAdded() {
        return databaseVersionAdded;
    }

    public void setDatabaseVersionAdded(int databaseVersionAdded) {
        this.databaseVersionAdded = databaseVersionAdded;
    }

}
