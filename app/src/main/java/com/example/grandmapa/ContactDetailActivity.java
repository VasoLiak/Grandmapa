package com.example.grandmapa;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.app.AlertDialog;

import androidx.appcompat.app.AppCompatActivity;

public class ContactDetailActivity extends AppCompatActivity {

    private String contactName;
    private String contactPhone;
    private long contactId;
    private boolean isSOSContact = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_detail);

        TextView nameTextView = findViewById(R.id.contact_name);
        TextView phoneTextView = findViewById(R.id.contact_phone);
        ImageView deleteImageView = findViewById(R.id.delete);
        ImageView callButton = findViewById(R.id.call);
        ImageView backImageView = findViewById(R.id.back);
        ImageView starImageView = findViewById(R.id.star_image);

        contactId = getIntent().getLongExtra("ContactId", -1);
        contactName = getIntent().getStringExtra("ContactName");
        contactPhone = getIntent().getStringExtra("ContactPhone");

        nameTextView.setText(contactName);
        phoneTextView.setText(contactPhone);

        SharedPreferences sharedPreferences = getSharedPreferences("SOSPreferences", Context.MODE_PRIVATE);
        String savedContactName = sharedPreferences.getString("SOSContactName", null);
        String savedContactNumber = sharedPreferences.getString("SOSContactNumber", null);

        isSOSContact = contactName.equals(savedContactName);

        starImageView.setImageResource(isSOSContact ? R.drawable.fullstar : R.drawable.emptystar);

        backImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ContactDetailActivity.this, CatalogueActivity.class);
                startActivity(intent);
            }
        });

        deleteImageView.setOnClickListener(v -> deleteContact(contactPhone));

        callButton.setOnClickListener(v -> {
            Intent dialIntent = new Intent(Intent.ACTION_DIAL);
            dialIntent.setData(Uri.parse("tel:" + contactPhone));
            startActivity(dialIntent);
        });

        starImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isSOSContact) {
                    removeSOSContact(sharedPreferences, starImageView);
                } else {
                    if (savedContactName != null && !contactName.equals(savedContactName)) {
                        showConfirmationDialog(sharedPreferences, starImageView, contactName, contactPhone);
                    } else {
                        setSOSContact(sharedPreferences, starImageView, contactName, contactPhone);
                    }
                }
            }
        });
    }

    private void showConfirmationDialog(SharedPreferences sharedPreferences, ImageView starImageView, String newContactName, String newContactPhone) {
        new AlertDialog.Builder(this)
                .setTitle("Αλλαγή επαφής SOS")
                .setMessage("Έχετε επιλέξει μια άλλη επαφή ως έκτακτης ανάγκης. Είστε σίγουροι ότι θέλετε να την αλλάξετε με τον χρήστη " + newContactName + ";")
                .setPositiveButton("Ναι", (dialog, which) -> setSOSContact(sharedPreferences, starImageView, newContactName, newContactPhone))
                .setNegativeButton("Όχι", null)
                .show();
    }

    private void setSOSContact(SharedPreferences sharedPreferences, ImageView starImageView, String contactName, String contactPhone) {
        isSOSContact = true;
        starImageView.setImageResource(R.drawable.fullstar);

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("SOSContactName", contactName);
        editor.putString("SOSContactNumber", contactPhone);
        editor.apply();

        showToast("Η επαφή καταχωρήθηκε ως έκτακτης ανάγκης!");
    }

    private void removeSOSContact(SharedPreferences sharedPreferences, ImageView starImageView) {
        isSOSContact = false;
        starImageView.setImageResource(R.drawable.emptystar);

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove("SOSContactName");
        editor.remove("SOSContactNumber");
        editor.apply();

        showToast("Η επαφή αφαιρέθηκε ως έκτακτης ανάγκης!");
    }

    @SuppressLint("Range")
    private void deleteContact(String phoneNumber) {
        ContentResolver contentResolver = getContentResolver();
        Uri contactUri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phoneNumber));
        Cursor cursor = contentResolver.query(contactUri, null, null, null, null);

        try {
            if (cursor != null && cursor.moveToFirst()) {
                contactId = cursor.getLong(cursor.getColumnIndex(ContactsContract.PhoneLookup._ID));
                Uri deleteUri = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, contactId);
                contentResolver.delete(deleteUri, null, null);
                Toast.makeText(this, "Επιτυχής διαγραφή επαφής", Toast.LENGTH_SHORT).show();

                Intent resultIntent = new Intent();
                resultIntent.putExtra("action", "delete");
                resultIntent.putExtra("contactId", contactId);
                setResult(RESULT_OK, resultIntent);
                finish();
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }
    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
