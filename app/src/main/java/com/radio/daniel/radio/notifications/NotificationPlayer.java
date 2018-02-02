package com.radio.daniel.radio.notifications;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.support.v4.app.NotificationCompat;
import android.widget.ImageView;
import android.widget.RemoteViews;
import com.radio.daniel.radio.HelperActivity;
import com.radio.daniel.radio.MainActivity;
import com.radio.daniel.radio.R;
import com.radio.daniel.radio.Radio;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

public class NotificationPlayer extends Notification {

    static final int NOTIFICATION_ID = 1;

    public NotificationPlayer(Context context) {
        super();

        Intent notificationIntent = new Intent(context, KillNotificationsService.class);
        context.startService(notificationIntent);

        final RemoteViews contentView = new RemoteViews(context.getPackageName(), R.layout.notification_player);
        final RemoteViews contentViewLarge = new RemoteViews(context.getPackageName(), R.layout.notification_player_large);


        if(!Radio.getCurrentRadioStation().getImageURL().equals("")) {

            final ImageView tmpView = new ImageView(context);
            Picasso.with(context).load(Radio.getCurrentRadioStation().getImageURL()).into(tmpView, new Callback() {
                @Override
                public void onSuccess() {
                    Bitmap bitmap = ((BitmapDrawable) tmpView.getDrawable()).getBitmap();
                    contentView.setImageViewBitmap(R.id.notification_player_cover_image, bitmap);
                    contentViewLarge.setImageViewBitmap(R.id.notification_player_cover_image, bitmap);
                }

                @Override
                public void onError() {
                    contentView.setImageViewResource(R.id.notification_player_cover_image, R.drawable.mascot_small);
                    contentViewLarge.setImageViewResource(R.id.notification_player_cover_image, R.drawable.mascot_small);
                }
            });

        } else {
            contentView.setImageViewResource(R.id.notification_player_cover_image, R.drawable.mascot_small);
            contentViewLarge.setImageViewResource(R.id.notification_player_cover_image, R.drawable.mascot_small);
        }



        switch (Radio.getRadioState()) {
            case PLAY:
                contentView.setImageViewResource(R.id.notification_player_play_pause_button, R.drawable.pause);
                contentViewLarge.setImageViewResource(R.id.notification_player_play_pause_button, R.drawable.pause);
                break;

            case STOP:
                contentView.setImageViewResource(R.id.notification_player_play_pause_button, R.drawable.play);
                contentViewLarge.setImageViewResource(R.id.notification_player_play_pause_button, R.drawable.play);
                break;

            case LOADING:
                contentView.setImageViewResource(R.id.notification_player_play_pause_button, R.drawable.loading);
                contentViewLarge.setImageViewResource(R.id.notification_player_play_pause_button, R.drawable.loading);
                break;

            case BUFFERING:
                contentView.setImageViewResource(R.id.notification_player_play_pause_button, R.drawable.loading);
                contentViewLarge.setImageViewResource(R.id.notification_player_play_pause_button, R.drawable.loading);
                break;

            case NULL:
                contentViewLarge.setImageViewResource(R.id.notification_player_close_button, R.drawable.close_clicked);
                break;
        }



        contentView.setImageViewResource(R.id.notification_player_next_button, R.drawable.next);
        contentView.setImageViewResource(R.id.notification_player_previous_button, R.drawable.previous);

        contentView.setTextViewText(R.id.notification_player_station_name, Radio.getCurrentRadioStation().getName());
        contentView.setTextViewText(R.id.notification_player_title, Radio.getSongMetaDate().getTitle(context));

        contentViewLarge.setImageViewResource(R.id.notification_player_next_button, R.drawable.next);
        contentViewLarge.setImageViewResource(R.id.notification_player_previous_button, R.drawable.previous);
        contentViewLarge.setImageViewResource(R.id.notification_player_close_button, R.drawable.close);

        contentViewLarge.setTextViewText(R.id.notification_player_station_name, Radio.getCurrentRadioStation().getName());
        contentViewLarge.setTextViewText(R.id.notification_player_title, Radio.getSongMetaDate().getTitle(context));


        Intent playPauseIntent = new Intent(context, HelperActivity.class);
        playPauseIntent.setAction(HelperActivity.PLAY_PAUSE);
        PendingIntent pendingPlayPauseIntent = PendingIntent.getBroadcast(context, 0, playPauseIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        contentView.setOnClickPendingIntent(R.id.notification_player_play_pause_button, pendingPlayPauseIntent);
        contentViewLarge.setOnClickPendingIntent(R.id.notification_player_play_pause_button, pendingPlayPauseIntent);

        Intent nextStationIntent = new Intent(context, HelperActivity.class);
        nextStationIntent.setAction(HelperActivity.NEXT_STATION);
        PendingIntent pendingNextStationIntent = PendingIntent.getBroadcast(context, 0, nextStationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        contentView.setOnClickPendingIntent(R.id.notification_player_next_button, pendingNextStationIntent);
        contentViewLarge.setOnClickPendingIntent(R.id.notification_player_next_button, pendingNextStationIntent);


        Intent previousStationIntent = new Intent(context, HelperActivity.class);
        previousStationIntent.setAction(HelperActivity.PREVIOUS_STATION);
        PendingIntent pendingPreviousStationIntent = PendingIntent.getBroadcast(context, 0, previousStationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        contentView.setOnClickPendingIntent(R.id.notification_player_previous_button, pendingPreviousStationIntent);
        contentViewLarge.setOnClickPendingIntent(R.id.notification_player_previous_button, pendingPreviousStationIntent);

        Intent closeAppIntent = new Intent(context, HelperActivity.class);
        closeAppIntent.setAction(HelperActivity.CLOSE_APP);
        PendingIntent pendingCloseAppIntent = PendingIntent.getBroadcast(context, 0, closeAppIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        contentViewLarge.setOnClickPendingIntent(R.id.notification_player_close_button, pendingCloseAppIntent);

        android.support.v4.app.NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context)
                .setOngoing(true)
                .setSmallIcon(R.drawable.mascot_notification)
                .setCustomBigContentView(contentViewLarge)
                .setContent(contentView);


        Intent resultIntent = new Intent(context.getApplicationContext(), MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context.getApplicationContext(), 0, resultIntent, 0);

        contentViewLarge.setOnClickPendingIntent(R.id.notification_player_container, pendingIntent);

        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        assert mNotificationManager != null;
        mNotificationManager.notify(NOTIFICATION_ID, notificationBuilder.build());
    }

}



