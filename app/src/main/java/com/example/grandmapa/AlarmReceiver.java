package com.example.grandmapa;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String medicineName = intent.getStringExtra("medicine_name");
        Log.d("AlarmReceiver", "Received alarm for medicine: " + medicineName);
        NotificationUtils.showNotification(context, "Medicine Reminder", "It's time to take your medicine: " + medicineName);
    }
}



