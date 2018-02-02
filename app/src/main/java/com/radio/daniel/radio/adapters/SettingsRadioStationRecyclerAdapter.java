package com.radio.daniel.radio.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import com.radio.daniel.radio.R;
import com.radio.daniel.radio.RadioStation;
import com.radio.daniel.radio.database.Database;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;

public class SettingsRadioStationRecyclerAdapter  extends RecyclerView.Adapter<SettingsRadioStationRecyclerAdapter.ViewHolder>
{

    private Context context;
    private ArrayList<RadioStation> stations;

    public SettingsRadioStationRecyclerAdapter(Context context, ArrayList<RadioStation> stations) {
        this.context = context;
        this.stations = stations;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.settings_radio_station_item, parent, false);

        return new SettingsRadioStationRecyclerAdapter.ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, int position) {
        final RadioStation station = stations.get(position);

        viewHolder.nameText.setText(station.getName());
        viewHolder.hideSwitch.setChecked(station.isHidden());
        Picasso.with(context).load(station.getImageURL()).into(viewHolder.coverImage);


        viewHolder.hideSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isHidden) {

                if(viewHolder.hideSwitch.isPressed()) {

                    station.setHidden(isHidden);
                    Database.updateRadioStation(station);
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return stations.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView nameText;
        ImageView coverImage;
        Switch hideSwitch;

        ViewHolder(View rootView) {
            super(rootView);

            nameText = (TextView) rootView.findViewById(R.id.settings_station_item_name);
            coverImage = (ImageView) rootView.findViewById(R.id.settings_station_item_cover);
            hideSwitch = (Switch) rootView.findViewById(R.id.settings_station_hide_switch);

        }
    }

}
