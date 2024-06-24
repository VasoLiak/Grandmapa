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

        //back button
        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) ImageView backImageView = findViewById(R.id.back);
        backImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PhoneActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        //erase button
        ImageView eraseButton = findViewById(R.id.erase);
        eraseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                eraseLastDigit();
            }
        });

        // Array of button IDs for the number buttons
        int[] buttonIds = {
                R.id.one, R.id.two, R.id.three, R.id.four, R.id.five,
                R.id.six, R.id.seven, R.id.eight, R.id.nine, R.id.zero
        };

        //number buttons
        View.OnClickListener numberButtonListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get the tag of the clicked button, which represents the digit
                ImageView imageView = (ImageView) v;
                String tag = (String) imageView.getTag();
                if (tag != null) {
                    if (isFirstInput) {
                        // Clear the display if it's the first input
                        phoneNumberDisplay.setText("");
                        isFirstInput = false;
                    }
                    // Append the digit to the phone number display
                    phoneNumberDisplay.append(tag);
                } else {
                    Toast.makeText(PhoneActivity.this, "Tag not set for this button", Toast.LENGTH_SHORT).show();
                }
            }
        };

        for (int id : buttonIds) {
            findViewById(id).setOnClickListener(numberButtonListener);
        }

        //call button
        ImageView callButton = findViewById(R.id.call);
        callButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makePhoneCall();
            }
        });
    }

    // Method to initiate a phone call
    private void makePhoneCall() {
        // Get the phone number from the display
        String phoneNumber = phoneNumberDisplay.getText().toString();

        // Check if the phone number is empty
        if (phoneNumber.isEmpty()) {
            Toast.makeText(this, "Εισάγετε έναν αριθμό τηλεφώνου", Toast.LENGTH_SHORT).show();
        } else {
            // Check if CALL_PHONE permission is granted
            if (ContextCompat.checkSelfPermission(PhoneActivity.this,
                    Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                // Request the CALL_PHONE permission
                ActivityCompat.requestPermissions(PhoneActivity.this,
                        new String[]{Manifest.permission.CALL_PHONE}, REQUEST_CALL);
            } else {
                // Start the call intent
                String dial = "tel:" + phoneNumber;
                startActivity(new Intent(Intent.ACTION_CALL, Uri.parse(dial)));
            }
        }
    }

    // Method to erase the last digit
    private void eraseLastDigit() {
        String currentText = phoneNumberDisplay.getText().toString();
        if (!currentText.isEmpty()) {
            phoneNumberDisplay.setText(currentText.substring(0, currentText.length() - 1));
        }
    }

    // Handle the result of the permission request
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // Check if the request code matches the call request
        if (requestCode == REQUEST_CALL) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                makePhoneCall();
            } else {
                Toast.makeText(this, "Permission DENIED", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
