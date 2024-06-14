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
    private static final String BIRIBA_PACKAGE_NAME = "air.com.lazyland.biriba"; //mporei na prepei na fygei to air.
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
        backImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MenuActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        pasietzaImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchGame(SOLITAIRE_PACKAGE_NAME);
            }
        });

        mpirimpaImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchGame(BIRIBA_PACKAGE_NAME);
            }
        });

        cameraImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MenuActivity.this, CameraActivity.class);
                startActivity(intent);
            }
        });

        photosImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MenuActivity.this, PhotosActivity.class);
                startActivity(intent);
            }
        });
    }

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
        for (String packageName : radioPackages) {
            Intent intent = getPackageManager().getLaunchIntentForPackage(packageName);
            if (intent != null) {
                startActivity(intent);
                appFound = true;
                break;
            }
        }

        if (!appFound) {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.nextradioapp.nextradio"));
            startActivity(intent);
        }
    }

    private void launchGame(String packageName) {
        Intent launchIntent = getPackageManager().getLaunchIntentForPackage(packageName);
        if (launchIntent != null) {
            startActivity(launchIntent);
        } else {
            Toast.makeText(this, "App is not installed.", Toast.LENGTH_LONG).show();
        }
    }
}
