package com.example.grandmapa;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MenuActivity extends AppCompatActivity {
    private static final String BIRIBA_PACKAGE_NAME = "air.com.lazyland.biriba";
    private static final String SOLITAIRE_PACKAGE_NAME = "com.karmangames.solitaire";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_menu);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        ImageView backImageView = findViewById(R.id.back);
        ImageView radioImageView = findViewById(R.id.radio);
        ImageView pasietzaImageView = findViewById(R.id.pasietza);
        ImageView mpirimpaImageView = findViewById(R.id.mpirimpa);
        ImageView cameraImageView = findViewById(R.id.camera);
        ImageView photosImageView = findViewById(R.id.photos);

        radioImageView.setOnClickListener(v -> launchRadioApp());

        //button
        backImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MenuActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        //solitaire game button
        pasietzaImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchGame(SOLITAIRE_PACKAGE_NAME);
            }
        });

        //biriba game button
        mpirimpaImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchGame(BIRIBA_PACKAGE_NAME);
            }
        });

        //camera button
        cameraImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MenuActivity.this, CameraActivity.class);
                startActivity(intent);
            }
        });

        //photos button
        photosImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MenuActivity.this, PhotosActivity.class);
                startActivity(intent);
            }
        });
    }

    // Method to launch a radio app if installed or redirect to the Play Store if not
    private void launchRadioApp() {
        String[] radioPackages = {
                "com.sec.android.app.fm",
                "com.htc.fmradio",
                "com.sonyericsson.fmradio",
                "com.motorola.fmradio",
                "com.caf.fmradio",
                "com.miui.fm",
                "com.huawei.android.FMRadio",
                "com.realme.fmradio"
        };

        boolean appFound = false;
        // Try to find a radio app installed on the device
        for (String packageName : radioPackages) {
            Intent intent = getPackageManager().getLaunchIntentForPackage(packageName);
            if (intent != null) {
                startActivity(intent);
                appFound = true;
                break;
            }
        }

        // If no radio app is found, redirect to the Play Store to install one
        if (!appFound) {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("com.nextradioapp.nextradio"));
            startActivity(intent);
        }
    }

    // Method to launch a game app if installed, or show a toast if not
    private void launchGame(String packageName) {
        Intent launchIntent = getPackageManager().getLaunchIntentForPackage(packageName);
        if (launchIntent != null) {
            startActivity(launchIntent);
        } else {
            Toast.makeText(this, "Απαιτείται εγκατάσταση της εφαρμογής.", Toast.LENGTH_LONG).show();
        }
    }
}
