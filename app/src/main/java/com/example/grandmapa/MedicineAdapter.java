package com.example.grandmapa;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.app.AlarmManagerCompat;
import androidx.recyclerview.widget.RecyclerView;
import java.util.Calendar;
import java.util.List;

public class MedicineAdapter extends RecyclerView.Adapter<MedicineAdapter.MedicineViewHolder> {

    private List<Medicine> medicineList;
    private Context context;

    public MedicineAdapter(Context context, List<Medicine> medicineList) {
        this.context = context;
        this.medicineList = medicineList;
    }

    @NonNull
    @Override
    public MedicineViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_hour, parent, false);
        return new MedicineViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MedicineViewHolder holder, int position) {
        Medicine medicine = medicineList.get(position);

        holder.textViewMedicine.setText(medicine.getName().isEmpty() ? "Προσθέστε φάρμακο" : medicine.getName());
        holder.checkBoxTaken.setChecked(medicine.isTaken());
        holder.checkBoxTaken.setVisibility(medicine.getName().isEmpty() ? View.GONE : View.VISIBLE);

        holder.textViewMedicine.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("Προσθέστε φάρμακο");

            final EditText input = new EditText(context);
            input.setInputType(InputType.TYPE_CLASS_TEXT);
            builder.setView(input);

            builder.setPositiveButton("OK", (dialog, which) -> {
                medicine.setName(input.getText().toString());
                holder.checkBoxTaken.setVisibility(View.VISIBLE);
                notifyDataSetChanged(); // Ensure to notify adapter of data change

                // Optionally, you can schedule the notification here
                scheduleNotification(context, "desired_hour", medicine.getName());
            });
            builder.setNegativeButton("Άκυρο", (dialog, which) -> dialog.cancel());

            builder.show();
        });

        holder.checkBoxTaken.setOnCheckedChangeListener((buttonView, isChecked) -> {
            medicine.setTaken(isChecked);
            // Update the state of the medicine object
        });
    }


    @Override
    public int getItemCount() {
        return medicineList.size();
    }

    @SuppressLint("ScheduleExactAlarm")
    private void scheduleNotification(Context context, String hour, String medicineName) {
        // Create an intent for the BroadcastReceiver that handles the notification
        Intent notificationIntent = new Intent(context, AlarmReceiver.class);
        notificationIntent.putExtra("hour", hour);
        notificationIntent.putExtra("medicineName", medicineName);

        // Use PendingIntent.FLAG_UPDATE_CURRENT for mutability
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_MUTABLE);

        // Schedule the notification using AlarmManager
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (alarmManager != null) {
            long triggerAtMillis = System.currentTimeMillis() + 5000; // Example: trigger after 5 seconds
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAtMillis, pendingIntent);
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, triggerAtMillis, pendingIntent);
            } else {
                alarmManager.set(AlarmManager.RTC_WAKEUP, triggerAtMillis, pendingIntent);
            }
        }
    }

    public static class MedicineViewHolder extends RecyclerView.ViewHolder {
        TextView textViewHour, textViewMedicine;
        CheckBox checkBoxTaken;

        public MedicineViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewHour = itemView.findViewById(R.id.textViewHour);
            textViewMedicine = itemView.findViewById(R.id.textViewMedicine);
            checkBoxTaken = itemView.findViewById(R.id.checkBoxTaken);
        }
    }
}
