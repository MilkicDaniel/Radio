package com.radio.daniel.radio.database;

import android.content.ContentValues;
import android.provider.BaseColumns;
import com.radio.daniel.radio.RadioStation;


public class RadioStationTable  {

    private RadioStationTable(){}


    public static class RadioStationEntry implements BaseColumns {

        public static final String TABLE_NAME = "radio_station";
        public static final String COLUMN_RADIO_STATION_NAME = "name";
        public static final String COLUMN_RADIO_STATION_IMAGE_URL = "image_url";
        public static final String COLUMN_RADIO_STATION_WEB_URL = "website_url";
        public static final String COLUMN_RADIO_STATION_STREAM_URL = "stream_url";
        public static final String COLUMN_RADIO_STATION_LOW_QUALITY_STREAM_URL = "low_quality_stream_url";
        public static final String COLUMN_RADIO_STATION_FAVOURITE = "favourite";
        public static final String COLUMN_RADIO_STATION_HIDDEN = "hidden";
        public static final String COLUMN_RADIO_STATION_DATABASE_VERSION_ADDED = "databaseVersionAdded";

    }

    public static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE IF NOT EXISTS " + RadioStationEntry.TABLE_NAME + " (" +
                    RadioStationEntry._ID + " INTEGER PRIMARY KEY," +
                    RadioStationEntry.COLUMN_RADIO_STATION_NAME + " VARCHAR," +
                    RadioStationEntry.COLUMN_RADIO_STATION_IMAGE_URL + " VARCHAR," +
                    RadioStationEntry.COLUMN_RADIO_STATION_WEB_URL + " VARCHAR," +
                    RadioStationEntry.COLUMN_RADIO_STATION_STREAM_URL + " VARCHAR," +
                    RadioStationEntry.COLUMN_RADIO_STATION_LOW_QUALITY_STREAM_URL + " VARCHAR," +
                    RadioStationEntry.COLUMN_RADIO_STATION_FAVOURITE + " INT," +
                    RadioStationEntry.COLUMN_RADIO_STATION_HIDDEN + " INT," +
                    RadioStationEntry.COLUMN_RADIO_STATION_DATABASE_VERSION_ADDED + " INT)";



    public static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + RadioStationEntry.TABLE_NAME;


    public static ContentValues insertToTable(RadioStation station) {
        ContentValues values = new ContentValues();
        values.put(RadioStationEntry.COLUMN_RADIO_STATION_NAME, station.getName());
        values.put(RadioStationEntry.COLUMN_RADIO_STATION_IMAGE_URL, station.getImageURL());
        values.put(RadioStationEntry.COLUMN_RADIO_STATION_WEB_URL, station.getURL());
        values.put(RadioStationEntry.COLUMN_RADIO_STATION_STREAM_URL, station.getStreamURL());
        values.put(RadioStationEntry.COLUMN_RADIO_STATION_LOW_QUALITY_STREAM_URL, station.getLowQualityStreamURL());
        values.put(RadioStationEntry.COLUMN_RADIO_STATION_FAVOURITE, station.isFavourite());
        values.put(RadioStationEntry.COLUMN_RADIO_STATION_HIDDEN, station.isHidden());
        values.put(RadioStationEntry.COLUMN_RADIO_STATION_DATABASE_VERSION_ADDED, station.getDatabaseVersionAdded());
        return values;
    }


    public static String[] getProjection() {

        return new String[]{
                RadioStationEntry._ID,
                RadioStationEntry.COLUMN_RADIO_STATION_NAME,
                RadioStationEntry.COLUMN_RADIO_STATION_IMAGE_URL,
                RadioStationEntry.COLUMN_RADIO_STATION_WEB_URL,
                RadioStationEntry.COLUMN_RADIO_STATION_STREAM_URL,
                RadioStationEntry.COLUMN_RADIO_STATION_LOW_QUALITY_STREAM_URL,
                RadioStationEntry.COLUMN_RADIO_STATION_FAVOURITE,
                RadioStationEntry.COLUMN_RADIO_STATION_HIDDEN,
                RadioStationEntry.COLUMN_RADIO_STATION_DATABASE_VERSION_ADDED
        };
    }

    public static ContentValues getContentValues(RadioStation station){

        ContentValues values = new ContentValues();

        values.put(RadioStationEntry.COLUMN_RADIO_STATION_NAME, station.getName());
        values.put(RadioStationEntry.COLUMN_RADIO_STATION_IMAGE_URL, station.getImageURL());
        values.put(RadioStationEntry.COLUMN_RADIO_STATION_WEB_URL, station.getURL());
        values.put(RadioStationEntry.COLUMN_RADIO_STATION_STREAM_URL, station.getStreamURL());
        values.put(RadioStationEntry.COLUMN_RADIO_STATION_LOW_QUALITY_STREAM_URL, station.getLowQualityStreamURL());
        values.put(RadioStationEntry.COLUMN_RADIO_STATION_FAVOURITE, station.isFavourite());
        values.put(RadioStationEntry.COLUMN_RADIO_STATION_HIDDEN, station.isHidden());
        values.put(RadioStationEntry.COLUMN_RADIO_STATION_DATABASE_VERSION_ADDED, station.getDatabaseVersionAdded());
        return values;
    }





}
