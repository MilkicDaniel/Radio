package com.radio.daniel.radio.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.radio.daniel.radio.R;
import com.radio.daniel.radio.Radio;
import com.radio.daniel.radio.adapters.RadioStationRecyclerAdapter;


public class RadioStationSelectionFragment extends Fragment {

    public final static String TAG = "RadioStationSelectionFragment";
    private RadioStationRecyclerAdapter radioStationRecyclerAdapter;
    private RecyclerView stationRecyclerView;

    public RadioStationSelectionFragment() {}


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_radio_station_selection, container, false);

        stationRecyclerView = (RecyclerView) root.findViewById(R.id.station_selection_recycler_view);

        radioStationRecyclerAdapter = new RadioStationRecyclerAdapter(getContext(), Radio.getRadioStationList());
        stationRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        stationRecyclerView.setAdapter(radioStationRecyclerAdapter);

        return root;
    }


    public void updateRecyclerView(){
        if(radioStationRecyclerAdapter != null) {
            radioStationRecyclerAdapter = new RadioStationRecyclerAdapter(getContext(), Radio.getRadioStationList());
            stationRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            stationRecyclerView.setAdapter(radioStationRecyclerAdapter);
        }
    }

}
