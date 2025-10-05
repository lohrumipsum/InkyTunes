package com.lohrumipsum.inkytunes.services;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.session.MediaSession;
import androidx.media3.session.MediaSessionService;

public class MusicService extends MediaSessionService {

    private MediaSession mediaSession;

    @Override
    public void onCreate() {
        // Initialize player and session BEFORE calling super.onCreate()
        ExoPlayer player = new ExoPlayer.Builder(this).build();
        mediaSession = new MediaSession.Builder(this, player).build();
        super.onCreate();
    }

    @Nullable
    @Override
    public MediaSession onGetSession(@NonNull MediaSession.ControllerInfo controllerInfo) {
        // Return the session to allow controllers to connect.
        // This will now return a valid session object.
        return mediaSession;
    }

    @Override
    public void onDestroy() {
        // Release the session and player resources
        if (mediaSession != null) {
            mediaSession.getPlayer().release();
            mediaSession.release();
            mediaSession = null;
        }
        super.onDestroy();
    }
}

