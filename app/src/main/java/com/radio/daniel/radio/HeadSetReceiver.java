package com.radio.daniel.radio;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import java.util.Objects;


public class HeadSetReceiver extends BroadcastReceiver {

    private boolean streamInterrupted = false;

    @Override public void onReceive(Context context, Intent intent) {
        if (Objects.equals(intent.getAction(), Intent.ACTION_HEADSET_PLUG)) {
            int state = intent.getIntExtra("state", -1);
            switch (state) {
                case 0:
                    //Headset unplugged
                    if (Radio.getRadioState().equals(Radio.RadioStates.PLAY) || Radio.getRadioState().equals(Radio.RadioStates.LOADING)) {
                        Radio.setRadioState(Radio.RadioStates.STOP);
                        streamInterrupted = true;
                    }
                    break;
                case 1:
                    // Headset plugged
                    if (streamInterrupted) {
                        Radio.setRadioState(Radio.RadioStates.LOADING);
                        streamInterrupted = false;
                    }
                    break;
            }
        }
    }
}