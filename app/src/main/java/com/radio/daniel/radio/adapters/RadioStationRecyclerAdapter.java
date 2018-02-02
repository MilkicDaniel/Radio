package com.radio.daniel.radio.adapters;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import com.radio.daniel.radio.R;
import com.radio.daniel.radio.Radio;
import com.radio.daniel.radio.RadioStation;
import com.radio.daniel.radio.RadioStationCallback;
import com.radio.daniel.radio.database.Database;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;


public class RadioStationRecyclerAdapter extends RecyclerView.Adapter<RadioStationRecyclerAdapter.ViewHolder> {

    private Context context;
    private ArrayList<RadioStation> stations;
//    private Radio radio = Radio.getInstance();


    public RadioStationRecyclerAdapter(Context context, ArrayList<RadioStation> stations){
        this.context = context;
        this.stations = stations;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.radio_station_item, parent, false);

        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, final int position) {

        final RadioStation station = stations.get(position);

        viewHolder.nameText.setText(station.getName());
        viewHolder.linkText.setText(station.getURL());

        if(!station.getImageURL().equals("")){
            Picasso.with(context).load(station.getImageURL()).into(viewHolder.coverImage);
        } else {
            viewHolder.coverImage.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.mascot_small));
        }



        if(station.isFavourite())
            viewHolder.favouriteButton.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.favorite_clicked_white));
        else
            viewHolder.favouriteButton.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.favorite_border_white));

        viewHolder.favouriteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                station.setFavourite(!station.isFavourite());

                Database.updateRadioStation(station);

                if (station.isFavourite()) {
                    viewHolder.favouriteButton.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.favorite_clicked_white));
                } else {
                    viewHolder.favouriteButton.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.favorite_border_white));
                }

                Animation favouriteAnim = AnimationUtils.loadAnimation(context, R.anim.favourite);
                favouriteAnim.setRepeatMode(Animation.REVERSE);
                favouriteAnim.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        updateStationList(Database.getRadioStations());
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {
                    }
                });

                viewHolder.favouriteButton.startAnimation(favouriteAnim);

            }
        });


        viewHolder.container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (Radio.getCurrentRadioStation() != null
                        && Radio.getCurrentRadioStation().getStreamURL().equals(station.getStreamURL())
                        && Radio.getRadioState().equals(Radio.RadioStates.PLAY)) {
                    Radio.setRadioState(Radio.RadioStates.STOP);
                } else {
                    Radio.setCurrentRadioStation(station, position);
                }
            }
        });


    }

    @Override
    public int getItemCount() {
        return stations.size();
    }


    private void updateStationList(ArrayList<RadioStation> stations){


        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new RadioStationCallback(this.stations, stations));
        diffResult.dispatchUpdatesTo(this);
        this.stations.clear();
        this.stations.addAll(stations);

        int streamPosition = 0;

        if (Radio.getCurrentRadioStation() != null) {
            for (RadioStation radioStation : Radio.getRadioStationList()) {
                if (radioStation.getName().equals(Radio.getCurrentRadioStation().getName())) {
                    Radio.setCurrentStationPosition(streamPosition);
                    break;
                }
                streamPosition++;
            }
        }
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView nameText;
        TextView linkText;
        ImageView coverImage;
        ImageButton favouriteButton;
        View container;

        ViewHolder(View rootView) {
            super(rootView);

            nameText = (TextView) rootView.findViewById(R.id.station_item_name);
            linkText = (TextView) rootView.findViewById(R.id.station_item_link);
            coverImage = (ImageView) rootView.findViewById(R.id.station_item_cover);
            favouriteButton = (ImageButton) rootView.findViewById(R.id.station_item_favorite);
            container = rootView.findViewById(R.id.station_item_container);

        }
    }

}
