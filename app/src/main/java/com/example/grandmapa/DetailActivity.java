package com.example.grandmapa;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.grandmapa.MedicineUtils;

import java.util.List;

public class DetailActivity extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_SCHEDULE_EXACT_ALARM = 1001;

    private RecyclerView recyclerViewHours;
    private TextView textViewDate;
    private MedicineAdapter medicineAdapter;
    private List<Medicine> medicineList;
    private String date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        textViewDate = findViewById(R.id.textView);
        recyclerViewHours = findViewById(R.id.recyclerViewHours);
        ImageView backImageView = findViewById(R.id.back);

        backImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DetailActivity.this, CalendarActivity.class);
                startActivity(intent);
            }
        });

        date = getIntent().getStringExtra("date");
        textViewDate.setText("Επιλεγμένη ημερομηνία: " + date);

        initializeMedicineList();
        medicineAdapter = new MedicineAdapter(this, medicineList);
        recyclerViewHours.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewHours.setAdapter(medicineAdapter);

        // Request the SCHEDULE_EXACT_ALARM permission if needed
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.SCHEDULE_EXACT_ALARM)
                    != PackageManager.PERMISSION_GRANTED) {
                // Permission is not granted, request it
                requestScheduleExactAlarmPermission();
            } else {
                // Permission is already granted, proceed with your logic
                // Initialize alarms or any other relevant operations
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.S)
    private void requestScheduleExactAlarmPermission() {
        // Request the SCHEDULE_EXACT_ALARM permission
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.SCHEDULE_EXACT_ALARM},
                PERMISSION_REQUEST_SCHEDULE_EXACT_ALARM);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_SCHEDULE_EXACT_ALARM) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, proceed with your logic
                // Initialize alarms or any other relevant operations
            } else {
                // Permission denied, inform the user
                // Handle the case where the user denies the permission
            }
        }
    }

    private void initializeMedicineList() {
        medicineList = MedicineUtils.loadMedicines(this, date);
        if (medicineList.isEmpty()) {
            for (int i = 0; i < 24; i++) {
                String hour = (i < 10 ? "0" + i : i) + ":00";
                medicineList.add(new Medicine(hour, "", false));
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        MedicineUtils.saveMedicines(this, date, medicineList);
    }
}
