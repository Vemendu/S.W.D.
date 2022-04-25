package com.example.finalexamweatherapp;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class PushNotificationService extends FirebaseMessagingService {
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onMessageReceived(@NonNull RemoteMessage message) {
        String title = message.getNotification().getTitle() + " NOTIFICATION TITLE1";
        String text = message.getNotification().getBody() + " NOTIFICATION BODY1";
        final String CHANNEL_ID = "HEADS_UP_NOTIFICATIONS";
        NotificationChannel channel = new NotificationChannel(
                CHANNEL_ID,
                "Heads up Notification",
                NotificationManager.IMPORTANCE_HIGH);

        getSystemService(NotificationManager.class).createNotificationChannel(channel);
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle(title + " NOTIFICATION TITLE2")
                .setContentText(text + " NOTIFICATION BODY2")
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setAutoCancel(true)
                .build();
        NotificationManagerCompat.from(this).notify(1, notification);
        super.onMessageReceived(message);
    }
}
