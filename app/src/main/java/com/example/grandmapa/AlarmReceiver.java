package com.example.grandmapa;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String medicineName = intent.getStringExtra("medicine_name");
        NotificationUtils.showNotification(context, "Medicine Reminder", "It's time to take your medicine: " + medicineName);
    }
}

