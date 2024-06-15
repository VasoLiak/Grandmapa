package com.example.grandmapa;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
                isSOSContact = !isSOSContact;
                starImageView.setImageResource(isSOSContact ? R.drawable.fullstar : R.drawable.emptystar);

                if (isSOSContact) {
                    showToast("Προσθήκη επαφής ως έκτακτης ανάγκης!");
                } else {
                    showToast("Κατάργηση επαφής ως έκτακτης ανάγκης!");
                }
            }
        });
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
                Toast.makeText(this, "Contact deleted", Toast.LENGTH_SHORT).show();

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
