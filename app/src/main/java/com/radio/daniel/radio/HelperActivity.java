package com.radio.daniel.radio;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;


public class HelperActivity extends BroadcastReceiver {


    public static final String PLAY_PAUSE = "PLAY_PAUSE";
    public static final String NEXT_STATION = "NEXT_STATION";
    public static final String PREVIOUS_STATION = "PREVIOUS_STATION";
    public static final String CLOSE_APP = "CLOSE_APP";

    @Override
    public void onReceive(Context context, Intent intent) {

        String action = intent.getAction();

        if(context == null)
            return;

        assert action != null;
        switch (action) {

            case PLAY_PAUSE:
                switch (Radio.getRadioState()) {

                    case PLAY:
                        Radio.setRadioState(Radio.RadioStates.STOP);
                        break;
                    case STOP:
                        Radio.setRadioState(Radio.RadioStates.LOADING);
                        break;

                }
                break;

            case NEXT_STATION:
                Radio.nextStation();
                break;
            case PREVIOUS_STATION:
                Radio.previousStation();
                break;

            case CLOSE_APP:
                Radio.setRadioState(Radio.RadioStates.NULL);
                System.exit(0);
                break;
        }

    }

}




