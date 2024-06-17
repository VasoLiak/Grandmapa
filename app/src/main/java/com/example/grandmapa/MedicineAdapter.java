package com.example.grandmapa;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.text.InputType;
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
        holder.textViewMedicine.setText(medicine.getName().isEmpty() ? "Προσθέστε φάρμακο" : medicine.getName());
        holder.checkBoxTaken.setChecked(medicine.isTaken());
        holder.checkBoxTaken.setVisibility(medicine.getName().isEmpty() ? View.GONE : View.VISIBLE);

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

                // Schedule notification
                scheduleNotification(context, time, medicineName);

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

        holder.ImageViewDelete.setOnClickListener(v -> {
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
        }
    }

    @SuppressLint("ScheduleExactAlarm")
    private void scheduleNotification(Context context, String hour, String medicineName) {
        // Implementation remains the same as in your code
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
        public View ImageViewDelete;
        TextView textViewHour, textViewMedicine;
        CheckBox checkBoxTaken;

        public MedicineViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewHour = itemView.findViewById(R.id.editTextTime);
            textViewMedicine = itemView.findViewById(R.id.editTextMedicine);
            checkBoxTaken = itemView.findViewById(R.id.checkBoxTaken);
            ImageViewDelete = itemView.findViewById(R.id.delete);
        }
    }
}
