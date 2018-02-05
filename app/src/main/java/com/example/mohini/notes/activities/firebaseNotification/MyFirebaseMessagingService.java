package com.example.mohini.notes.activities.firebaseNotification;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.example.mohini.notes.activities.activities.Notes;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

/**
 * Created by mohini on 5/2/18.
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    //generates Android Notification In case any firebase notification being recieved.
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        //gets Title and body of the firebase notification.
        try {
            String title = remoteMessage.getNotification().getTitle();
            String body = remoteMessage.getNotification().getBody();
            Intent intent = new Intent(this, Notes.class);

            PendingIntent pendingIntent;
            //pending Intent in case notification is being clicked.
            pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            //NotificationCompat is helper for accessing feature in Notification.
            Notification.Builder notificationBuilder = new Notification.Builder(this);
            notificationBuilder.setContentTitle(title);
            notificationBuilder.setContentText(body);
            notificationBuilder.setAutoCancel(true);
            notificationBuilder.setOngoing(true);
//            notificationBuilder.setOnlyAlertOnce(true);

            notificationBuilder.setContentIntent(pendingIntent);

            //Initializing Notification Manager(NotificationManager is a class that notify user that something has happened in background).
            NotificationManager notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
            //NotificationChannel is required for Android version from Oreo.
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel channel = new NotificationChannel("notify_001",
                        "Channel human readable title",
                        NotificationManager.IMPORTANCE_DEFAULT);
                notificationManager.createNotificationChannel(channel);
            } else
                //shows the notification in the notification bar.
                notificationManager.notify(1, notificationBuilder.getNotification());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
