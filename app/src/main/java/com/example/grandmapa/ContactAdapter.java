package com.example.grandmapa;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;
import java.util.Random;

public class ContactAdapter extends BaseAdapter {
    private Context context;
    private List<Contact> contacts;
    private int[] photos = {R.drawable.profile1, R.drawable.profile2, R.drawable.profile3, R.drawable.profile4};
    private Random random = new Random();

    public ContactAdapter(Context context, List<Contact> contacts) {
        this.context = context;
        this.contacts = contacts;
    }

    @Override
    public int getCount() {
        return contacts.size();
    }

    @Override
    public Object getItem(int position) {
        return contacts.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
        }

        ImageView photo = convertView.findViewById(R.id.photo);
        TextView contactName = convertView.findViewById(R.id.contact_name);

        final Contact contact = contacts.get(position);
        contactName.setText(contact.getName() + ": " + contact.getPhone());
        photo.setImageResource(photos[random.nextInt(photos.length)]);

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle click event, pass contact details to ContactDetailActivity
                Intent intent = new Intent(context, ContactDetailActivity.class);
                intent.putExtra("ContactName", contact.getName());
                intent.putExtra("ContactPhone", contact.getPhone());
                context.startActivity(intent);
            }
        });

        return convertView;
    }
}
