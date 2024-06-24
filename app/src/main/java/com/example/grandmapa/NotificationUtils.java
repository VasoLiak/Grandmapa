package com.example.grandmapa;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;

import androidx.core.app.NotificationCompat;

public class NotificationUtils {

    private static final String CHANNEL_ID = "medicine_channel";
    private static final String CHANNEL_NAME = "Medicine Reminder";
    private static final String CHANNEL_DESC = "Reminders to take your medicine";
    private static int notificationId = 0; //ID for each notification

    /**
     * Creates a notification channel for devices running Android O and above.
     * This channel will be used to send medicine reminders.
     */
    public static void createNotificationChannel(Context context) {
        // Check if the Android version is Oreo or above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Create the notification channel
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel.setDescription(CHANNEL_DESC); //channel description
            channel.enableLights(true); // Enable lights for notifications
            channel.setLightColor(Color.RED); //light color for notifications
            channel.enableVibration(true); //vibration for notifications

            // Get the NotificationManager and create the notification channel
            NotificationManager manager = context.getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
    }

    /**
     * Shows a notification with the specified title and message.
     * When the notification is tapped, it opens the CalendarActivity.
     */
    public static void showNotification(Context context, String title, String message) {
        //open CalendarActivity when the notification is tapped
        Intent intent = new Intent(context, CalendarActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE);

        // Build the notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.pills) //icon for the notification
                .setContentTitle(title) //title of the notification
                .setContentText(message) //message of the notification
                .setPriority(NotificationCompat.PRIORITY_HIGH) //priority of the notification
                .setContentIntent(pendingIntent) //intent to be triggered when the notification is tapped
                .setAutoCancel(true); // Auto-cancel the notification when tapped

        // Get the NotificationManager and show the notification
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(notificationId++, builder.build());
    }
}
