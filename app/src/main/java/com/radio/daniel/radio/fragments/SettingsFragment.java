package com.radio.daniel.radio.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import com.radio.daniel.radio.R;
import com.radio.daniel.radio.Radio;
import com.radio.daniel.radio.adapters.SettingsRadioStationRecyclerAdapter;
import com.radio.daniel.radio.database.Database;


public class SettingsFragment extends Fragment {

    public final static String TAG = "SettingsFragment";
    public SettingsFragment() {}
    SettingsListener settingsListener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root =  inflater.inflate(R.layout.fragment_settings, container, false);
        Switch qualitySwitch = (Switch) root.findViewById(R.id.settings_quality_switch);
        Switch headphoneSwitch = (Switch) root.findViewById(R.id.settings_headphone_switch);
        RecyclerView recyclerView = (RecyclerView) root.findViewById(R.id.settings_recycler_view);


        SettingsRadioStationRecyclerAdapter settingsRadioStationRecyclerAdapter = new SettingsRadioStationRecyclerAdapter(getContext(), Database.getRadioStations(true));
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(settingsRadioStationRecyclerAdapter);

        qualitySwitch.setChecked(Radio.isLowQualityStream());
        headphoneSwitch.setChecked(Radio.isHeadphonesCycleFavourites());

        qualitySwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isLowQuality) {
                Radio.setIsLowQualityStream(isLowQuality);
            }
        });

        headphoneSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean HeadphonesCycleFavourites) {
                Radio.setHeadphonesCycleFavourites(HeadphonesCycleFavourites);
            }
        });


        return root;
    }

    public void setSettingsListener(SettingsListener settingsListener) {
        this.settingsListener = settingsListener;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Radio.setRadioStationListOriginal(Database.getRadioStations());
        Radio.setRadioStationList(Database.getRadioStations());
        settingsListener.SettingsUpdate();
    }

    public abstract static class SettingsListener {

        public SettingsListener(){}

        public abstract void SettingsUpdate();

    }

}
