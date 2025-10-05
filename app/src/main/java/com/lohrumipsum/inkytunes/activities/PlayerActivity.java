package com.lohrumipsum.inkytunes.activities;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.lohrumipsum.inkytunes.R;

// A placeholder for the player screen.
// A full implementation would bind to the MusicService and control playback.
public class PlayerActivity extends AppCompatActivity {
     @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        // TODO: Implement UI updates and controls by connecting to the MusicService
    }
}
