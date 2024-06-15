package com.example.grandmapa;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.HashMap;

public class SosActivity extends AppCompatActivity {

    private static final int REQUEST_READ_CONTACTS = 1;
    private ListView contactsListView;
    private TextView callingTextView;
    private ArrayList<String> contactNames = new ArrayList<>();
    private HashMap<String, String> contactsMap = new HashMap<>();
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sos);

        callingTextView = findViewById(R.id.calling_text_view); // Initialize callingTextView

        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences("SOSPreferences", Context.MODE_PRIVATE);
        String savedContactName = sharedPreferences.getString("SOSContactName", null);
        String savedContactNumber = sharedPreferences.getString("SOSContactNumber", null);

        if (savedContactName != null && savedContactNumber != null) {
            // Display "Calling [SOS Contact Name]" message
            callingTextView.setText("Κλήση " + savedContactName);
            callingTextView.setVisibility(View.VISIBLE);

            // Call the saved SOS contact directly
            callSOSContact(savedContactNumber);
        } else {
            // Show message and allow the user to select a contact
            Toast.makeText(this, "Select Sos Contact", Toast.LENGTH_SHORT).show();

            // Check for READ_CONTACTS permission
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_CONTACTS}, REQUEST_READ_CONTACTS);
            } else {
                loadContacts();
            }

            contactsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    String contactName = contactNames.get(position);
                    String contactNumber = contactsMap.get(contactName);

                    // Save selected contact as SOS contact
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("SOSContactName", contactName);
                    editor.putString("SOSContactNumber", contactNumber);
                    editor.apply();

                    // Display "Calling [SOS Contact Name]" message
                    callingTextView.setText("Calling " + contactName);
                    callingTextView.setVisibility(View.VISIBLE);

                    // Make a call to this number
                    callSOSContact(contactNumber);
                }
            });
        }
    }


    private void loadContacts() {
        ContentResolver contentResolver = getContentResolver();
        Cursor cursor = contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                null, null, null, null);

        if (cursor != null) {
            while (cursor.moveToNext()) {
                String name = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                String number = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                contactNames.add(name);
                contactsMap.put(name, number);
            }
            cursor.close();
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, contactNames);
        contactsListView.setAdapter(adapter);
    }

    private void callSOSContact(String contactNumber) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE)
                == PackageManager.PERMISSION_GRANTED) {
            Intent callIntent = new Intent(Intent.ACTION_CALL);
            callIntent.setData(Uri.parse("tel:" + contactNumber));
            startActivity(callIntent);
        } else {
            Toast.makeText(this, "Permission to make calls not granted.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_READ_CONTACTS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                loadContacts();
            } else {
                Toast.makeText(this, "Permission to read contacts not granted", Toast.LENGTH_SHORT).show();
            }
        }
    }
}