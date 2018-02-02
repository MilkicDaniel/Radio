package com.radio.daniel.radio.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import com.radio.daniel.radio.MainActivity;
import com.radio.daniel.radio.R;
import com.radio.daniel.radio.Radio;
import com.radio.daniel.radio.RadioStation;
import com.radio.daniel.radio.database.Database;


public class AddRadioStationFragment extends Fragment {

    public final static String TAG = "AddRadioStationFragment";
    View root;
    EditText stationName;
    EditText stationStreamUrl;
    EditText stationLqStreamUrl;
    EditText stationCover;
    EditText stationSiteURL;
    DrawerLayout drawerLayout;
    AddRadioStationListener radioListener;

    public AddRadioStationFragment() {}

    public abstract static class AddRadioStationListener {

        public AddRadioStationListener(){}

        public abstract void stationUpdate();

    }
    public void setAddRadioStationListener(AddRadioStationListener radioListener) {
        this.radioListener = radioListener;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        root =  inflater.inflate(R.layout.fragment_add_radio_station, container, false);
        View background = root.findViewById(R.id.add_station_background);
        SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);

        stationName = (EditText) root.findViewById(R.id.add_station_name);
        stationStreamUrl = (EditText) root.findViewById(R.id.add_station_stream_url);
        stationLqStreamUrl = (EditText) root.findViewById(R.id.add_station_lq_stream_url);
        stationCover = (EditText) root.findViewById(R.id.add_station_cover_url);
        stationSiteURL = (EditText) root.findViewById(R.id.add_station_site_url);
        stationName.setText(sharedPref.getString(getString(R.string.saved_station_name),""));
        stationStreamUrl.setText(sharedPref.getString(getString(R.string.saved_station_stream_url),""));
        stationLqStreamUrl.setText(sharedPref.getString(getString(R.string.saved_station_low_quality_stream_url),""));
        stationCover.setText(sharedPref.getString(getString(R.string.saved_station_cover_url),""));
        stationSiteURL.setText(sharedPref.getString(getString(R.string.saved_station_site_url),""));
        drawerLayout = (DrawerLayout) ((MainActivity)getContext()).findViewById(R.id.main_drawer_layout);
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        final Button createButton = (Button) root.findViewById(R.id.add_station_create_button);
        final Button cancelButton = (Button) root.findViewById(R.id.add_station_cancel_button);

        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(!stationName.getText().toString().trim().equals("") && !stationStreamUrl.getText().toString().trim().equals("")){
                    if(isValidStream(stationStreamUrl.getText().toString())){
                        if(!Database.doesNameExists(stationName.getText().toString())) {

                            RadioStation radioStation = new RadioStation();
                            radioStation.setName(stationName.getText().toString());
                            radioStation.setStreamURL(stationStreamUrl.getText().toString());
                            radioStation.setLowQualityStreamURL(stationStreamUrl.getText().toString());
                            radioStation.setURL("");
                            radioStation.setImageURL("");
                            radioStation.setFavourite(false);
                            radioStation.setHidden(false);
                            radioStation.setDatabaseVersionAdded(0);

                            if (!stationLqStreamUrl.getText().toString().trim().equals("")) {

                                if(isValidStream(stationLqStreamUrl.getText().toString())) {
                                    radioStation.setLowQualityStreamURL(stationLqStreamUrl.getText().toString());
                                } else {
                                    Snackbar.make(((MainActivity) getContext()).findViewById(R.id.main_overlay_container),
                                            getResources().getString(R.string.warning_wrong_extension_stream), Snackbar.LENGTH_LONG).show();
                                    return;
                                }
                            }

                            if (!stationCover.getText().toString().trim().equals("")) {

                                if (isValidImage(stationCover.getText().toString())) {
                                    radioStation.setImageURL(stationCover.getText().toString());
                                } else {
                                    Snackbar.make(((MainActivity) getContext()).findViewById(R.id.main_overlay_container),
                                            getResources().getString(R.string.warring_wrong_extension_image), Snackbar.LENGTH_LONG).show();
                                    return;
                                }
                            }

                            if(!stationSiteURL.getText().toString().trim().equals("")){
                                radioStation.setURL(stationSiteURL.getText().toString());
                            }


                            Database.addRadioStation(radioStation);

                            Snackbar.make(((MainActivity) getContext()).findViewById(R.id.main_overlay_container),
                                    getResources().getString(R.string.notification_added_station, radioStation.getName()), Snackbar.LENGTH_LONG).show();

                            cancelButton.callOnClick();


                        } else {
                            Snackbar.make(((MainActivity) getContext()).findViewById(R.id.main_overlay_container),
                                    getResources().getString(R.string.warring_name_taken), Snackbar.LENGTH_LONG).show();
                        }

                    } else {
                        Snackbar.make(((MainActivity) getContext()).findViewById(R.id.main_overlay_container),
                                getResources().getString(R.string.warning_wrong_extension_stream), Snackbar.LENGTH_LONG).show();
                    }

                } else {
                    Snackbar.make(((MainActivity) getContext()).findViewById(R.id.main_overlay_container),
                            getResources().getString(R.string.warning_missing_mandatory), Snackbar.LENGTH_LONG).show();
                }

                updateSharedPreferencesEditor();
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateSharedPreferencesEditor();
                ((MainActivity) getContext()).getSupportFragmentManager().popBackStack();
            }
        });


        background.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cancelButton.performClick();
            }
        });

        return root;
    }

    private boolean isValidStream(String streamURL){

        if(streamURL.length() < 5)
            return false;

        String extension = streamURL.substring(streamURL.length()-4, streamURL.length());

        return extension.equals(".mp3") || extension.equals(".m3u");

    }

    private boolean isValidImage(String imageURL){

        if(imageURL.length() < 5)
            return false;

        String extension = imageURL.substring(imageURL.length()-4, imageURL.length());

        return extension.equals(".png") || extension.equals(".jpg");

    }


    private void updateSharedPreferencesEditor(){
        SharedPreferences.Editor editor = getActivity().getPreferences(Context.MODE_PRIVATE).edit();
        editor.putString(getString(R.string.saved_station_name), stationName.getText().toString());
        editor.putString(getString(R.string.saved_station_stream_url), stationStreamUrl.getText().toString());
        editor.putString(getString(R.string.saved_station_low_quality_stream_url), stationLqStreamUrl.getText().toString());
        editor.putString(getString(R.string.saved_station_cover_url), stationCover.getText().toString());
        editor.putString(getString(R.string.saved_station_site_url), stationSiteURL.getText().toString());
        editor.apply();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        updateSharedPreferencesEditor();

        View view = ((MainActivity) getContext()).getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            assert imm != null;
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }

        if(drawerLayout != null)
            drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);

        root.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.slide_down));
        ((MainActivity) getContext()).findViewById(R.id.main_transparent_layer).startAnimation(AnimationUtils.loadAnimation(getContext(),R.anim.fade_out));

        Radio.setRadioStationListOriginal(Database.getRadioStations());
        Radio.setRadioStationList(Database.getRadioStations());
        radioListener.stationUpdate();
    }
}
