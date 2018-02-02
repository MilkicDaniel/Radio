package com.radio.daniel.radio.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import com.radio.daniel.radio.R;
import com.radio.daniel.radio.Radio;
import com.radio.daniel.radio.RadioListener;
import com.radio.daniel.radio.SongMetaDate;
import com.squareup.picasso.Picasso;

public class PlayerFragment extends Fragment {

    public final static String TAG = "PlayerFragment";
    private Radio radio = Radio.getInstance();
    private RadioListener radioListener;
    private String currentSongTitle = "";
    private Radio.RadioStates currentState = Radio.RadioStates.NULL;


    public PlayerFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_player, container, false);

        final TextView titleView = (TextView) root.findViewById(R.id.player_title);
        final TextView stationNameView = (TextView) root.findViewById(R.id.player_station_name);
        final ImageButton playPauseButton = (ImageButton) root.findViewById(R.id.player_play_pause_button);
        final ImageButton nextButton = (ImageButton) root.findViewById(R.id.player_next_button);
        final ImageButton previousButton = (ImageButton) root.findViewById(R.id.player_previous_button);
        final ImageView coverImage = (ImageView) root.findViewById(R.id.player_cover_image);

        titleView.setSingleLine();
        titleView.setSelected(true);

        
        radioListener = new RadioListener() {

            @Override
            public void onRadioUpdate() {

                if (currentState != Radio.getRadioState()) {

                    currentState = Radio.getRadioState();

                    switch (Radio.getRadioState()) {

                        case LOADING:
                            playPauseButton.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.loading_white));
                            playPauseButton.setAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.spin_animation));
                            titleView.setText(getString(R.string.buffering));
                            currentSongTitle = getString(R.string.buffering);
                            stationNameView.setText(Radio.getCurrentRadioStation().getName());
                            setCover(coverImage, Radio.getCurrentRadioStation().getImageURL());
                            break;

                        case PLAY:

                            playPauseButton.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.pause_white));
                            playPauseButton.setAnimation(null);
                            stationNameView.setText(Radio.getCurrentRadioStation().getName());
                            setCover(coverImage, Radio.getCurrentRadioStation().getImageURL());
                            break;

                        case STOP:
                            playPauseButton.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.play_white));
                            playPauseButton.setAnimation(null);
                            break;

                        case BUFFERING:
                            playPauseButton.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.loading_white));
                            playPauseButton.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.spin_animation));
                            break;
                    }

                } else if(currentState == Radio.RadioStates.LOADING) {
                    setCover(coverImage, Radio.getCurrentRadioStation().getImageURL());
                    stationNameView.setText(Radio.getCurrentRadioStation().getName());
                }

                if (Radio.getRadioState() == Radio.RadioStates.PLAY && !Radio.getSongMetaDate().getTitle(getContext()).equals(currentSongTitle)) {
                    titleView.setText(Radio.getSongMetaDate().getTitle(getContext()));
                    currentSongTitle = Radio.getSongMetaDate().getTitle(getContext());
                    if (!radio.getSongHistory().contains(Radio.getSongMetaDate())) {
                        radio.getSongHistory().add(new SongMetaDate(Radio.getSongMetaDate()));
                    }
                }

            }
        };


        playPauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                switch (Radio.getRadioState()) {

                    case PLAY:
                        Radio.setRadioState(Radio.RadioStates.STOP);
                        break;
                    case STOP:
                        Radio.setRadioState(Radio.RadioStates.LOADING);
                        break;

                }

            }
        });

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Radio.nextStation();
            }
        });

        previousButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Radio.previousStation();
            }
        });


        Radio.addRadioListener(radioListener);

        return root;
    }

    private void setCover(ImageView coverView, String coverURL) {
        if(!coverURL.equals("")){
            Picasso.with(getContext()).load(coverURL).into(coverView);
        } else {
            coverView.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.mascot_small));
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        Radio.removeRadioListener(radioListener);
    }
}
