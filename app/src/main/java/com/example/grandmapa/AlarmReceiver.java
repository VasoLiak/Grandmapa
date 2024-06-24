package com.example.grandmapa;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String medicineName = intent.getStringExtra("medicine_name");
        String message = intent.getStringExtra("message");
        NotificationUtils.showNotification(context, "Υπενθύμιση Φαρμάκου", message);
    }
}