package com.example.grandmapa;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DetailActivity extends AppCompatActivity {

    private static final String PREFS_NAME = "MedicinePrefs";
    private static final String KEY_MEDICINE_MAP = "medicineMap";

    private RecyclerView recyclerViewHours;
    private TextView textViewDate;
    private MedicineAdapter medicineAdapter;
    private Map<String, List<Medicine>> medicineMap; // Map to store medicines by date
    private String date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        textViewDate = findViewById(R.id.textView);
        recyclerViewHours = findViewById(R.id.recyclerViewHours);
        ImageView backImageView = findViewById(R.id.back);
        ImageView AddMedicine = findViewById(R.id.add);

        backImageView.setOnClickListener(v -> {
            Intent intent = new Intent(DetailActivity.this, CalendarActivity.class);
            startActivity(intent);
        });

        date = getIntent().getStringExtra("date");
        textViewDate.setText("Επιλεγμένη ημερομηνία: " + date);

        // Initialize medicineMap if null
        if (medicineMap == null) {
            loadMedicines();
        }

        // Initialize or create medicineList for the current date
        if (!medicineMap.containsKey(date)) {
            medicineMap.put(date, new ArrayList<>());
        }

        // Initialize RecyclerView and Adapter with medicines for the current date
        medicineAdapter = new MedicineAdapter(this, medicineMap.get(date));
        recyclerViewHours.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewHours.setAdapter(medicineAdapter);

        AddMedicine.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Προσθέστε φάρμακο");

            View dialogView = LayoutInflater.from(this).inflate(R.layout.item_hour, null);
            builder.setView(dialogView);

            final EditText editTextTime = dialogView.findViewById(R.id.editTextTime);
            final EditText editTextMedicine = dialogView.findViewById(R.id.editTextMedicine);

            builder.setPositiveButton("OK", (dialog, which) -> {
                String time = editTextTime.getText().toString();
                String medicineName = editTextMedicine.getText().toString();

                // Check if medicine already exists for the current date
                boolean alreadyExists = false;
                List<Medicine> medicines = medicineMap.get(date);
                for (Medicine med : medicines) {
                    if (med.getTime().equals(time) && med.getName().equals(medicineName)) {
                        alreadyExists = true;
                        break;
                    }
                }

                if (!alreadyExists) {
                    // Add new medicine to the list for the current date
                    Medicine newMedicine = new Medicine(time, medicineName, false, "default_notification_time");
                    medicines.add(newMedicine);
                    medicineAdapter.addMedicine(newMedicine);

                    // Save updated list of medicines for the current date
                    saveMedicines();

                } else {
                    Toast.makeText(this, "Το φάρμακο υπάρχει ήδη στη λίστα για αυτήν την ημερομηνία", Toast.LENGTH_SHORT).show();
                }
            });
            builder.setNegativeButton("Άκυρο", (dialog, which) -> dialog.cancel());

            builder.show();
        });
    }

    private void loadMedicines() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        String json = prefs.getString(KEY_MEDICINE_MAP, null);
        if (json != null) {
            Gson gson = new Gson();
            Type type = new TypeToken<Map<String, List<Medicine>>>() {}.getType();
            medicineMap = gson.fromJson(json, type);
        } else {
            medicineMap = new HashMap<>();
        }
    }

    private void saveMedicines() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(medicineMap);
        editor.putString(KEY_MEDICINE_MAP, json);
        editor.apply();
    }
}
