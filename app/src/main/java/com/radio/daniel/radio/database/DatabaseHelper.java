package com.radio.daniel.radio.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.radio.daniel.radio.RadioStation;
import com.radio.daniel.radio.Utils;


public class DatabaseHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "RadioStation.db";
    private Context context;

    DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(RadioStationTable.SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        for(RadioStation jsonStation : Utils.createRadioStationsFromJson(context)) {
            if (jsonStation.getDatabaseVersionAdded() > oldVersion) {
                db.insert(RadioStationTable.RadioStationEntry.TABLE_NAME, null,
                        RadioStationTable.insertToTable(jsonStation));
            }
        }



    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
}
