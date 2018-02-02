package com.radio.daniel.radio;


import android.content.Context;
import android.util.Log;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;

public class Utils {

    private static final Utils ourInstance = new Utils();

    public static Utils getInstance() {
        return ourInstance;
    }

    private Utils() {
    }


    public static ArrayList<RadioStation> createRadioStationsFromJson(Context context){

        ArrayList<RadioStation> stations = new ArrayList<>();
        Gson gson = new Gson();
        Type listType = new TypeToken<ArrayList<RadioStation>>() {}.getType();

        try {
            InputStream is = context.getAssets().open("radio_steams.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            String json = new String(buffer, "UTF-8");

            stations = gson.fromJson(json, listType);

        } catch (Exception e) {
            Log.e("Error", e.toString());
        }

        return stations;
    }



}
