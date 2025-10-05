package com.lohrumipsum.inkytunes.utils;

import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.media3.common.MediaMetadata;
import androidx.media3.common.Player;
import androidx.media3.session.MediaController;

import com.lohrumipsum.inkytunes.R;

public class PlayerControlsDelegate {

    private MediaController mediaController;
    private TextView nowPlayingTitle;
    private SeekBar progressBar;
    private ImageButton playPauseButton;
    private ImageButton nextButton;
    private ImageButton prevButton;
    private final LinearLayout controlsContainer;

    private final Handler handler = new Handler(Looper.getMainLooper());
    private final Runnable progressUpdater = new Runnable() {
        @Override
        public void run() {
            if (mediaController != null && mediaController.isPlaying()) {
                progressBar.setProgress((int) mediaController.getCurrentPosition());
                handler.postDelayed(this, 500);
            }
        }
    };

    private final Player.Listener playerListener = new Player.Listener() {
        @Override
        public void onIsPlayingChanged(boolean isPlaying) {
            updatePlayPauseButton(isPlaying);
            if (isPlaying) {
                handler.post(progressUpdater);
            } else {
                handler.removeCallbacks(progressUpdater);
            }
        }

        @Override
        public void onMediaMetadataChanged(@NonNull MediaMetadata mediaMetadata) {
            updateMetadata(mediaMetadata);
        }

        @Override
        public void onPlaybackStateChanged(int playbackState) {
            if (playbackState == Player.STATE_READY || playbackState == Player.STATE_BUFFERING) {
                progressBar.setMax((int) mediaController.getDuration());
            }
        }
    };

    public PlayerControlsDelegate(@NonNull View controlsContainerView) {
        // The view passed to the constructor IS the container.
        this.controlsContainer = (LinearLayout) controlsContainerView;
        bindViews();
    }

    private void bindViews() {
        // Find child views within the container.
        nowPlayingTitle = controlsContainer.findViewById(R.id.now_playing_title);
        progressBar = controlsContainer.findViewById(R.id.progress_bar);
        playPauseButton = controlsContainer.findViewById(R.id.button_play_pause);
        nextButton = controlsContainer.findViewById(R.id.button_next);
        prevButton = controlsContainer.findViewById(R.id.button_previous);

        playPauseButton.setOnClickListener(v -> {
            if (mediaController == null) return;
            if (mediaController.isPlaying()) {
                mediaController.pause();
            } else {
                mediaController.play();
            }
        });

        nextButton.setOnClickListener(v -> {
            if (mediaController != null) mediaController.seekToNextMediaItem();
        });

        prevButton.setOnClickListener(v -> {
            if (mediaController != null) mediaController.seekToPreviousMediaItem();
        });

        progressBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser && mediaController != null) {
                    mediaController.seekTo(progress);
                }
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
    }

    public void setMediaController(MediaController controller) {
        if (this.mediaController != null) {
            this.mediaController.removeListener(playerListener);
        }
        this.mediaController = controller;
        if (controller != null) {
            controlsContainer.setVisibility(View.VISIBLE);
            controller.addListener(playerListener);
            // Initial state update
            updatePlayPauseButton(controller.isPlaying());
            updateMetadata(controller.getMediaMetadata());
            if (controller.getPlaybackState() != Player.STATE_IDLE) {
                progressBar.setMax((int) controller.getDuration());
                progressBar.setProgress((int) controller.getCurrentPosition());
            }
            if (controller.isPlaying()) {
                handler.post(progressUpdater);
            }
        } else {
            controlsContainer.setVisibility(View.GONE);
        }
    }

    private void updatePlayPauseButton(boolean isPlaying) {
        playPauseButton.setImageResource(isPlaying ? R.drawable.ic_pause : R.drawable.ic_play);
    }

    private void updateMetadata(MediaMetadata mediaMetadata) {
        if (mediaMetadata != null && mediaMetadata.title != null) {
            nowPlayingTitle.setText(mediaMetadata.title);
        } else {
            nowPlayingTitle.setText("Not Playing");
        }
    }



    public void release() {
        handler.removeCallbacks(progressUpdater);
        if (mediaController != null) {
            mediaController.removeListener(playerListener);
        }
    }
}

