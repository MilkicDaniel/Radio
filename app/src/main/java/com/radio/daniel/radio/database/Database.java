package com.radio.daniel.radio.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.radio.daniel.radio.Radio;
import com.radio.daniel.radio.RadioStation;
import com.radio.daniel.radio.Utils;
import java.util.ArrayList;


public class Database {

    private static SQLiteDatabase db = null;
    private static DatabaseHelper dbHelper;

    private static final Database ourInstance = new Database();

    public static Database getInstance() {
        return ourInstance;
    }

    private Database() {}

    public static void createDatabase(Context context) {

        dbHelper = new DatabaseHelper(context);
        db = dbHelper.getWritableDatabase();


        String queryString = "SELECT * FROM " + RadioStationTable.RadioStationEntry.TABLE_NAME;

        Cursor cursor = db.rawQuery(queryString, null);
        boolean databaseExists = cursor.getCount() > 0;
        cursor.close();

        if (databaseExists)
            return;

        ArrayList<RadioStation> stations = Utils.createRadioStationsFromJson(context);
        ArrayList<RadioStation> originalStationList = new ArrayList<>();
        originalStationList.addAll(stations);

        Radio.setRadioStationList(stations);
        Radio.setRadioStationListOriginal(originalStationList);


        for (RadioStation station : stations) {
            addRadioStation(station);
        }
    }


    public static ArrayList<RadioStation> getRadioStations() {
        return getRadioStations(false);
    }


    public static ArrayList<RadioStation> getRadioStations(boolean includeHidden) {

        ArrayList<RadioStation> stationList = new ArrayList<>();

        String SELECT_QUERY = "SELECT * FROM " + RadioStationTable.RadioStationEntry.TABLE_NAME;

        if (!includeHidden)
            SELECT_QUERY += " WHERE hidden=0 ";

        SELECT_QUERY += " ORDER BY favourite DESC;";


        Cursor cursor = db.rawQuery(SELECT_QUERY, null);

        while (cursor.moveToNext()) {

            boolean favourite = (cursor.getInt(cursor.getColumnIndex(RadioStationTable.RadioStationEntry.COLUMN_RADIO_STATION_FAVOURITE)) != 0);
            boolean hidden = (cursor.getInt(cursor.getColumnIndex(RadioStationTable.RadioStationEntry.COLUMN_RADIO_STATION_HIDDEN)) != 0);

            stationList.add(new RadioStation(
                    cursor.getString(cursor.getColumnIndex(RadioStationTable.RadioStationEntry.COLUMN_RADIO_STATION_NAME)),
                    cursor.getString(cursor.getColumnIndex(RadioStationTable.RadioStationEntry.COLUMN_RADIO_STATION_IMAGE_URL)),
                    cursor.getString(cursor.getColumnIndex(RadioStationTable.RadioStationEntry.COLUMN_RADIO_STATION_WEB_URL)),
                    cursor.getString(cursor.getColumnIndex(RadioStationTable.RadioStationEntry.COLUMN_RADIO_STATION_STREAM_URL)),
                    cursor.getString(cursor.getColumnIndex(RadioStationTable.RadioStationEntry.COLUMN_RADIO_STATION_LOW_QUALITY_STREAM_URL)),
                    favourite,
                    hidden,
                    cursor.getInt(cursor.getColumnIndex(RadioStationTable.RadioStationEntry.COLUMN_RADIO_STATION_DATABASE_VERSION_ADDED))
            ));
        }

        cursor.close();
        return stationList;
    }

    public static void addRadioStation(RadioStation station) {
        db.insert(RadioStationTable.RadioStationEntry.TABLE_NAME, null,
                RadioStationTable.insertToTable(station));
    }

    public static void updateRadioStation(RadioStation station) {

        String selection = RadioStationTable.RadioStationEntry.COLUMN_RADIO_STATION_NAME + " = ?";
        String[] selectionArgs = {station.getName()};

        db.update(
                RadioStationTable.RadioStationEntry.TABLE_NAME,
                RadioStationTable.getContentValues(station),
                selection,
                selectionArgs);
    }


    public static boolean doesNameExists(String name) {
        String selection = RadioStationTable.RadioStationEntry.COLUMN_RADIO_STATION_NAME + " = ?";
        String[] selectionArgs = {name};

        Cursor cursor = db.rawQuery("SELECT 1 FROM " + RadioStationTable.RadioStationEntry.TABLE_NAME
                + " WHERE " + selection, selectionArgs);
        boolean doesExits = cursor.moveToFirst();
        cursor.close();

        return doesExits;
    }

}
