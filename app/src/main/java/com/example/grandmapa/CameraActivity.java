package com.example.grandmapa;

import android.content.Intent;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class CameraActivity extends AppCompatActivity {

    private SoundPool soundPool;
    private int soundID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_camera);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize SoundPool
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            AudioAttributes attributes = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build();
            soundPool = new SoundPool.Builder()
                    .setMaxStreams(1)
                    .setAudioAttributes(attributes)
                    .build();
        } else {
            soundPool = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
        }

        // Load the sound file
        soundID = soundPool.load(this, R.raw.cameraclick, 1);

        ImageView backImageView = findViewById(R.id.back);
        ImageView photosImageView = findViewById(R.id.photos);
        ImageView clickImageView = findViewById(R.id.click);

        backImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CameraActivity.this, MenuActivity.class);
                startActivity(intent);
            }
        });

        photosImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CameraActivity.this, PhotosActivity.class);
                startActivity(intent);
            }
        });

        clickImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Play the camera click sound
                soundPool.play(soundID, 1, 1, 1, 0, 1);
                // Add your logic for taking a picture here
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Release the SoundPool resources
        soundPool.release();
        soundPool = null;
    }
}
