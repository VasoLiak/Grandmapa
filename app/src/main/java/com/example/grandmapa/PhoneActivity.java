package com.example.grandmapa;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class PhoneActivity extends AppCompatActivity {

    private static final int REQUEST_CALL = 1;
    private TextView phoneNumberDisplay;
    private boolean isFirstInput = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone);

        phoneNumberDisplay = findViewById(R.id.phone_number_display);

        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) ImageView backImageView = findViewById(R.id.back);
        backImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PhoneActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        ImageView eraseButton = findViewById(R.id.erase);
        eraseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                eraseLastDigit();
            }
        });

        int[] buttonIds = {
                R.id.one, R.id.two, R.id.three, R.id.four, R.id.five,
                R.id.six, R.id.seven, R.id.eight, R.id.nine, R.id.zero
        };

        View.OnClickListener numberButtonListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImageView imageView = (ImageView) v;
                String tag = (String) imageView.getTag();
                if (tag != null) {
                    if (isFirstInput) {
                        phoneNumberDisplay.setText("");
                        isFirstInput = false;
                    }
                    phoneNumberDisplay.append(tag);
                } else {
                    Toast.makeText(PhoneActivity.this, "Tag not set for this button", Toast.LENGTH_SHORT).show();
                }
            }
        };

        for (int id : buttonIds) {
            findViewById(id).setOnClickListener(numberButtonListener);
        }

        ImageView callButton = findViewById(R.id.call);
        callButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makePhoneCall();
            }
        });
    }

    private void makePhoneCall() {
        String phoneNumber = phoneNumberDisplay.getText().toString();

        if (phoneNumber.isEmpty()) {
            Toast.makeText(this, "Εισάγετε έναν αριθμό τηλεφώνου", Toast.LENGTH_SHORT).show();
        } else {
            if (ContextCompat.checkSelfPermission(PhoneActivity.this,
                    Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(PhoneActivity.this,
                        new String[]{Manifest.permission.CALL_PHONE}, REQUEST_CALL);
            } else {
                String dial = "tel:" + phoneNumber;
                startActivity(new Intent(Intent.ACTION_CALL, Uri.parse(dial)));
            }
        }
    }

    private void eraseLastDigit() {
        String currentText = phoneNumberDisplay.getText().toString();
        if (!currentText.isEmpty()) {
            phoneNumberDisplay.setText(currentText.substring(0, currentText.length() - 1));
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CALL) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                makePhoneCall();
            } else {
                Toast.makeText(this, "Permission DENIED", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
