package com.example.grandmapa;

import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
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

        radioImageView.setOnClickListener(v -> launchRadioApp());
        backImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create Intent to start the second activity
                Intent intent = new Intent(MenuActivity.this, MainActivity.class);
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
                // Prompt to install a radio app
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.nextradioapp.nextradio"));
                startActivity(intent);
            }
        }

}
