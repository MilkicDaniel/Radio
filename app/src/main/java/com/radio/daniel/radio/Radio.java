package com.radio.daniel.radio;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.session.PlaybackState;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.os.SystemClock;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.KeyEvent;
import java.lang.ref.WeakReference;
import java.net.URL;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import static android.content.Context.TELEPHONY_SERVICE;

public class Radio {

    public enum RadioStates {
        PLAY,
        STOP,
        LOADING,
        BUFFERING,
        HIDDEN,
        ERROR,
        NULL
    }

    private static String TAG = "Radio";
    private static Radio instance = null;
    private static MediaPlayer mediaPlayer;
    private static UpdateHandler updateHandler;
    private static ArrayList<RadioListener> radioListeners = new ArrayList<>();
    private static RadioStates radioState = RadioStates.HIDDEN;
    private static SongMetaDate songMetaDate;
    private static RadioStation currentRadioStation;
    private static ArrayList<RadioStation> radioStationList = new ArrayList<>();
    private static ArrayList<RadioStation> radioStationListOriginal = new ArrayList<>();
    private static WeakReference<MainActivity> activityReference;
    private static SharedPreferences sharedPref;
    private static SharedPreferences.Editor editor;
    private static IcyStreamMeta meta = new IcyStreamMeta();
    private static ArrayList<SongMetaDate> songHistory = new ArrayList<>();
    private static boolean headphonesCycleFavourites;
    private static boolean hasPlayBeenPressedOnce = false;
    private static boolean isLowQualityStream = false;
    private static int currentStationPosition = -1;


    protected Radio() {}

    public static Radio getInstance() {

        if (instance == null) {
            instance = new Radio();

            mediaPlayer = new MediaPlayer();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

            mediaPlayer.setOnInfoListener(new MediaPlayer.OnInfoListener() {
                @Override
                public boolean onInfo(MediaPlayer mediaPlayer, int what, int extra) {

                    if(what == 701)
                        setRadioState(RadioStates.BUFFERING);
                    else if(what == 702)
                        setRadioState(RadioStates.PLAY);
                    return false;
                }
            });

        }
        return instance;
    }


    public void setupRadio(MainActivity context) {

        activityReference = new WeakReference<>(context);
        updateHandler = new UpdateHandler(context);

        songMetaDate = new SongMetaDate();
        sharedPref = (context).getPreferences(Context.MODE_PRIVATE);

        setHeadphonesCycleFavourites( sharedPref.getBoolean(context.getString(R.string.saved_headphones_cycle_favourites), false));



        mediaPlayer.setWakeMode(context.getApplicationContext(), PowerManager.FULL_WAKE_LOCK);

        ComponentName mediaButtonReceiver = new ComponentName(context, RemoteControlReceiver.class);
        MediaSessionCompat mediaSession = new MediaSessionCompat(context, TAG, mediaButtonReceiver, null);


            mediaSession.setFlags(
                    MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS | MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);

        PlaybackStateCompat state = null;

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            state = new PlaybackStateCompat.Builder()
                    .setActions(
                            PlaybackState.ACTION_PLAY | PlaybackState.ACTION_PLAY_PAUSE |
                                    PlaybackState.ACTION_PLAY_FROM_MEDIA_ID | PlaybackState.ACTION_PAUSE |
                                    PlaybackState.ACTION_SKIP_TO_NEXT | PlaybackState.ACTION_SKIP_TO_PREVIOUS)
                    .setState(PlaybackState.STATE_PLAYING, 0, 1, SystemClock.elapsedRealtime())
                    .build();
        }

        mediaSession.setPlaybackState(state);

            mediaSession.setCallback(new MediaSessionCompat.Callback() {
                @Override
                public boolean onMediaButtonEvent(Intent mediaButtonEvent) {

                    KeyEvent keyEvent = mediaButtonEvent.getParcelableExtra(Intent.EXTRA_KEY_EVENT);

                    if(keyEvent.getAction() == KeyEvent.ACTION_UP) {
                        if (getCurrentRadioStation() != null) {

                            if (getRadioState() == RadioStates.STOP && !hasPlayBeenPressedOnce) {
                                setRadioState(RadioStates.LOADING);
                            } else if ((getRadioState() == RadioStates.PLAY || getRadioState() == RadioStates.LOADING) && !hasPlayBeenPressedOnce) {

                                hasPlayBeenPressedOnce = true;
                                setRadioState(RadioStates.STOP);
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        hasPlayBeenPressedOnce = false;
                                    }
                                }, 500);
                            } else {
                                nextStationHeadphones();
                            }
                        }

                    }

                    return super.onMediaButtonEvent(mediaButtonEvent);
                }
            });

            mediaSession.setActive(true);



        WifiManager.WifiLock wifiLock = ((WifiManager) context.getApplicationContext()
                .getSystemService(Context.WIFI_SERVICE))
                .createWifiLock(WifiManager.WIFI_MODE_FULL, TAG);

        wifiLock.acquire();

        getMetaDate(3000);

        PhoneStateListener mPhoneStateListener = new PhoneStateListener() {
            boolean streamInterrupted = false;

            @Override
            public void onCallStateChanged(int state, String incomingNumber) {
                // Incoming call
                if (state == TelephonyManager.CALL_STATE_RINGING) {
                    if (!getRadioState().equals(RadioStates.STOP)) {
                        setRadioState(RadioStates.STOP);
                        streamInterrupted = true;
                    }
                }
                // Not in call
                else if (state == TelephonyManager.CALL_STATE_IDLE) {
                    if (streamInterrupted) {
                        setRadioState(RadioStates.LOADING);
                        streamInterrupted = false;
                    }
                }

                super.onCallStateChanged(state, incomingNumber);
            }
        };

        TelephonyManager mgr = (TelephonyManager) context.getSystemService(TELEPHONY_SERVICE);
        assert mgr != null;
        mgr.listen(mPhoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);

    }


    public static void setRadioState(RadioStates radioState) {
        Radio.radioState = radioState;

        switch (radioState) {

            case LOADING:
                load();
                break;

            case BUFFERING:
                break;

            case PLAY:
                break;

            case STOP:
                mediaPlayer.stop();
                break;

            case ERROR:
                mediaPlayer.stop();
                break;

            case NULL:
                mediaPlayer.stop();
                break;

        }

        update();

    }

private static void load() {

    Thread loadingThread = new Thread(new Runnable() {
        @Override
        public void run() {
            try {
                mediaPlayer.reset();

                if(sharedPref.getBoolean(activityReference.get().getApplicationContext().getString(R.string.saved_stream_quality), false))
                    mediaPlayer.setDataSource(currentRadioStation.getStreamURL());
                else
                    mediaPlayer.setDataSource(currentRadioStation.getLowQualityStreamURL());

                mediaPlayer.prepareAsync();

                mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mediaPlayer) {
                        mediaPlayer.start();
                        setRadioState(RadioStates.PLAY);
                    }
                });

            } catch (Exception e){
                Log.e("ERROR", TAG + " " + e.toString());
                Radio.radioState = RadioStates.STOP;
            }
        }
    });

    loadingThread.run();
}

    private static void getMetaDate(int waitTime) {

        Timer timer = new Timer();

        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {

                if (currentRadioStation != null && getRadioState() != RadioStates.STOP) {

                    try {
                        meta.setStreamUrl(new URL(currentRadioStation.getStreamURL()));

                        if(!songMetaDate.getTitle(activityReference.get().getApplicationContext()).equals(meta.getTitle())) {

                            songMetaDate.setTitle(meta.getTitle());
                            songMetaDate.setArtist(meta.getArtist());
                            songMetaDate.setRadioStation(currentRadioStation.getName());
                            updateHandler.obtainMessage(1).sendToTarget();

                        }

                    } catch (Exception e) {
                        Log.e(TAG, TAG + e.toString());
                    }

                }

            }
        }, 0, waitTime);

    }

    private static void update() {
        for(RadioListener radioListener : radioListeners){
            radioListener.onRadioUpdate();
        }
    }

    public static void nextStation(){

        if(currentStationPosition < radioStationList.size()-1)
            setCurrentRadioStation(radioStationList.get(currentStationPosition+1), currentStationPosition+1);
        else
            setCurrentRadioStation(radioStationList.get(0), 0);
    }

    public static void nextStationHeadphones(){

        if(!headphonesCycleFavourites || !radioStationList.get(0).isFavourite()) {
            nextStation();
            return;
        }

        if(currentStationPosition < radioStationList.size()-1 && radioStationList.get(currentStationPosition+1).isFavourite())
                setCurrentRadioStation(radioStationList.get(currentStationPosition+1), currentStationPosition+1);
        else
            setCurrentRadioStation(radioStationList.get(0), 0);
    }

    public static void previousStation(){

        if(currentStationPosition == 0)
            setCurrentRadioStation(radioStationList.get(radioStationList.size()-1), radioStationList.size()-1);
        else
            setCurrentRadioStation(radioStationList.get(currentStationPosition-1), currentStationPosition-1);
    }


    public static RadioStation getCurrentRadioStation() {
        return currentRadioStation;
    }

    public static void setCurrentRadioStation(RadioStation currentRadioStation, int stationPosition) {
        Radio.currentRadioStation = currentRadioStation;
        Radio.currentStationPosition = stationPosition;
        setRadioState(RadioStates.LOADING);
        update();
    }

    public static ArrayList<RadioStation> getRadioStationListOriginal() {
        return radioStationListOriginal;
    }

    public static void setRadioStationListOriginal(ArrayList<RadioStation> radioStationListOriginal) {
        Radio.radioStationListOriginal.clear();
        Radio.radioStationListOriginal = radioStationListOriginal;
    }

    public static ArrayList<RadioStation> getRadioStationList() {
        return radioStationList;
    }

    public static void setRadioStationList(ArrayList<RadioStation> radioStationList) {
        Radio.radioStationList.clear();
        Radio.radioStationList = radioStationList;
    }

    public static void addRadioListener(RadioListener radioListener){
        radioListeners.add(radioListener);
    }

    public static void removeRadioListener(RadioListener radioListener){
        radioListeners.remove(radioListener);
    }

    public static SongMetaDate getSongMetaDate() {
        return songMetaDate;
    }


    public static RadioStates getRadioState() {
        return radioState;
    }

    public static int getCurrentStationPosition() {
        return currentStationPosition;
    }

    public static void setCurrentStationPosition(int currentStationPosition) {
        Radio.currentStationPosition = currentStationPosition;
    }

    public static boolean isLowQualityStream() {
        return isLowQualityStream;
    }

    public static void setIsLowQualityStream(boolean isLowQualityStream) {
        Radio.isLowQualityStream = isLowQualityStream;

        editor = sharedPref.edit();
        editor.putBoolean(activityReference.get().getApplicationContext().getString(R.string.saved_stream_quality), isLowQualityStream);
        editor.apply();
    }


    public static boolean isHeadphonesCycleFavourites() {
        return headphonesCycleFavourites;
    }

    public static void setHeadphonesCycleFavourites(boolean headphonesCycleFavourites) {
        Radio.headphonesCycleFavourites = headphonesCycleFavourites;

        editor = sharedPref.edit();
        editor.putBoolean(activityReference.get().getApplicationContext().getString(R.string.saved_headphones_cycle_favourites), headphonesCycleFavourites);
        editor.apply();
    }

    public ArrayList<SongMetaDate> getSongHistory() {
        return songHistory;
    }


    //this is necessary so that the UI can be updated in the main thread preventing a crash
    private static class UpdateHandler extends Handler {
        private final WeakReference<MainActivity> reference;

        public UpdateHandler(MainActivity activity){
            reference = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(reference.get() != null){
                update();
            }
        }
    }

}
