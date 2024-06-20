package com.example.grandmapa;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
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
        loadMedicineStates(); // Load medicine states when adapter is initialized
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

        holder.textViewHour.setText(medicine.getTime());
        holder.textViewMedicine.setText(TextUtils.isEmpty(medicine.getName()) ? "Προσθέστε φάρμακο" : medicine.getName());
        holder.checkBoxTaken.setChecked(medicine.isTaken());
        holder.checkBoxTaken.setVisibility(TextUtils.isEmpty(medicine.getName()) ? View.GONE : View.VISIBLE);

        holder.textViewMedicine.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("Προσθέστε φάρμακο");

            // Inflate custom layout
            View dialogView = LayoutInflater.from(context).inflate(R.layout.item_hour, null);
            builder.setView(dialogView);

            final EditText editTextTime = dialogView.findViewById(R.id.editTextTime);
            final EditText editTextMedicine = dialogView.findViewById(R.id.editTextMedicine);

            builder.setPositiveButton("OK", (dialog, which) -> {
                String time = editTextTime.getText().toString();
                String medicineName = editTextMedicine.getText().toString();

                medicine.setTime(time);
                medicine.setName(medicineName);
                holder.checkBoxTaken.setVisibility(View.VISIBLE);
                notifyDataSetChanged(); // Ensure to notify adapter of data change

                // Schedule notifications
                scheduleMedicineNotification(context, time, medicineName);

                // Save medicine state
                saveMedicineState(medicine);
            });
            builder.setNegativeButton("Άκυρο", (dialog, which) -> dialog.cancel());

            builder.show();
        });

        holder.checkBoxTaken.setOnCheckedChangeListener((buttonView, isChecked) -> {
            medicine.setTaken(isChecked);
            saveMedicineState(medicine); // Save medicine state when checkbox state changes
        });

        holder.imageViewDelete.setOnClickListener(v -> {
            showDeleteConfirmationDialog(medicine);
        });
    }

    @Override
    public int getItemCount() {
        return medicineList.size();
    }

    public void addMedicine(Medicine medicine) {
        if (!medicineList.contains(medicine)) {
            medicineList.add(medicine);
            notifyItemInserted(medicineList.size() - 1);
        }
    }

    private void showDeleteConfirmationDialog(Medicine medicine) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Διαγραφή Φαρμάκου");
        builder.setMessage("Είστε σίγουροι ότι θέλετε να διαγράψετε αυτό το φάρμακο;");

        builder.setPositiveButton("Ναι", (dialog, which) -> {
            deleteMedicine(medicine);
        });
        builder.setNegativeButton("Όχι", (dialog, which) -> {
            dialog.dismiss();
        });

        builder.show();
    }

    private void deleteMedicine(Medicine medicine) {
        int position = medicineList.indexOf(medicine);
        if (position != -1) {
            medicineList.remove(position);
            notifyItemRemoved(position);

            // Notify DetailActivity to save updated medicineMap
            if (context instanceof DetailActivity) {
                ((DetailActivity) context).saveMedicines();
            }
        }
    }



    @SuppressLint("ScheduleExactAlarm")
    private void scheduleMedicineNotification(Context context, String hour, String medicineName) {
        Log.d("MedicineAdapter", "Scheduling medicine notification for " + hour + ": " + medicineName);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, AlarmReceiver.class);
        intent.putExtra("medicine_name", medicineName);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, medicineName.hashCode(), intent, PendingIntent.FLAG_UPDATE_CURRENT);

        // Parse hour to set alarm
        Calendar calendar = Calendar.getInstance();
        String[] timeParts = hour.split(":");
        calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(timeParts[0]));
        calendar.set(Calendar.MINUTE, Integer.parseInt(timeParts[1]));
        calendar.set(Calendar.SECOND, 0);

        long alarmTime = calendar.getTimeInMillis();

        // Check if the time is in the past, if so, add one day
        if (alarmTime < System.currentTimeMillis()) {
            alarmTime += AlarmManager.INTERVAL_DAY;
        }

        // Set the alarm
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, alarmTime, pendingIntent);
        } else {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, alarmTime, pendingIntent);
        }

        Log.d("MedicineAdapter", "Medicine notification scheduled for " + calendar.getTime().toString());
    }

    @SuppressLint("ScheduleExactAlarm")
    private void scheduleNotification(Context context, String notificationTime, String medicineName) {
        Log.d("MedicineAdapter", "Scheduling notification for " + notificationTime + ": " + medicineName);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, AlarmReceiver.class);
        intent.putExtra("medicine_name", medicineName);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, medicineName.hashCode() + 1, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        // Parse notification time to set alarm
        Calendar notificationCalendar = Calendar.getInstance();
        String[] timeParts = notificationTime.split(":");
        notificationCalendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(timeParts[0]));
        notificationCalendar.set(Calendar.MINUTE, Integer.parseInt(timeParts[1]));
        notificationCalendar.set(Calendar.SECOND, 0);

        long alarmTime = notificationCalendar.getTimeInMillis();

        // Check if the time is in the past, if so, add one day
        if (alarmTime < System.currentTimeMillis()) {
            alarmTime += AlarmManager.INTERVAL_DAY;
        }

        // Set the alarm
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, alarmTime, pendingIntent);
        } else {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, alarmTime, pendingIntent);
        }

        Log.d("MedicineAdapter", "Notification scheduled for " + notificationCalendar.getTime().toString());
    }

    private void saveMedicineState(Medicine medicine) {
        SharedPreferences prefs = context.getSharedPreferences("MedicinePrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(medicine.getTime() + medicine.getName(), medicine.isTaken());
        editor.apply();
    }

    private void loadMedicineStates() {
        SharedPreferences prefs = context.getSharedPreferences("MedicinePrefs", Context.MODE_PRIVATE);
        for (Medicine medicine : medicineList) {
            boolean isTaken = prefs.getBoolean(medicine.getTime() + medicine.getName(), false);
            medicine.setTaken(isTaken);
        }
    }

    public static class MedicineViewHolder extends RecyclerView.ViewHolder {
        ImageView imageViewDelete;
        TextView textViewHour, textViewMedicine;
        CheckBox checkBoxTaken;

        public MedicineViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewHour = itemView.findViewById(R.id.editTextTime);
            textViewMedicine = itemView.findViewById(R.id.editTextMedicine);
            checkBoxTaken = itemView.findViewById(R.id.checkBoxTaken);
            imageViewDelete = itemView.findViewById(R.id.delete);
        }
    }
}
