package com.lohrumipsum.inkytunes.activities;

import android.content.ComponentName;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.media3.session.MediaController;
import androidx.media3.session.SessionToken;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.common.util.concurrent.ListenableFuture;
import com.lohrumipsum.inkytunes.R;
import com.lohrumipsum.inkytunes.adapters.MusicAdapter;
import com.lohrumipsum.inkytunes.models.Song;
import com.lohrumipsum.inkytunes.services.MusicService;
import com.lohrumipsum.inkytunes.utils.PlayerControlsDelegate;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class TrackListActivity extends AppCompatActivity {

    public static final String EXTRA_ALBUM_ID = "EXTRA_ALBUM_ID";
    public static final String EXTRA_ARTIST_ID = "EXTRA_ARTIST_ID";
    public static final String EXTRA_TITLE = "EXTRA_TITLE";

    private RecyclerView recyclerView;
    private MusicAdapter musicAdapter;
    private List<Song> songList = new ArrayList<>();
    private ListenableFuture<MediaController> mediaControllerFuture;
    private PlayerControlsDelegate playerControlsDelegate;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track_list);
        // Standardize how the delegate is initialized.
        playerControlsDelegate = new PlayerControlsDelegate(findViewById(R.id.playback_controls_container));


        Toolbar toolbar = findViewById(R.id.toolbar);
        String title = getIntent().getStringExtra(EXTRA_TITLE);
        toolbar.setTitle(title != null ? title : "Tracks");

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        musicAdapter = new MusicAdapter(this, songList);
        recyclerView.setAdapter(musicAdapter);

        long albumId = getIntent().getLongExtra(EXTRA_ALBUM_ID, -1);
        long artistId = getIntent().getLongExtra(EXTRA_ARTIST_ID, -1);

        if (albumId != -1) {
            loadTracksByAlbum(albumId);
        } else if (artistId != -1) {
            loadTracksByArtist(artistId);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        SessionToken sessionToken = new SessionToken(this, new ComponentName(this, MusicService.class));
        mediaControllerFuture = new MediaController.Builder(this, sessionToken).buildAsync();
        mediaControllerFuture.addListener(() -> {
            try {
                MediaController controller = mediaControllerFuture.get();
                musicAdapter.setMediaController(controller);
                playerControlsDelegate.setMediaController(controller);
            } catch (ExecutionException | InterruptedException e) {
                // Handle error
            }
        }, ContextCompat.getMainExecutor(this));
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mediaControllerFuture != null) {
            MediaController.releaseFuture(mediaControllerFuture);
        }
        playerControlsDelegate.release();
    }

    private void loadTracksByAlbum(long albumId) {
        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String[] projection = {
                MediaStore.Audio.Media._ID, MediaStore.Audio.Media.TITLE, MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.ALBUM, MediaStore.Audio.Media.DURATION, MediaStore.Audio.Media.DATA
        };
        String selection = MediaStore.Audio.Media.IS_MUSIC + "!= 0 AND " + MediaStore.Audio.Media.ALBUM_ID + "=?";
        String[] selectionArgs = {String.valueOf(albumId)};
        String sortOrder = MediaStore.Audio.Media.TRACK + " ASC";
        queryAndPopulate(uri, projection, selection, selectionArgs, sortOrder);
    }

    private void loadTracksByArtist(long artistId) {
        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String[] projection = {
                MediaStore.Audio.Media._ID, MediaStore.Audio.Media.TITLE, MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.ALBUM, MediaStore.Audio.Media.DURATION, MediaStore.Audio.Media.DATA
        };
        String selection = MediaStore.Audio.Media.IS_MUSIC + "!= 0 AND " + MediaStore.Audio.Media.ARTIST_ID + "=?";
        String[] selectionArgs = {String.valueOf(artistId)};
        String sortOrder = MediaStore.Audio.Media.ALBUM + " ASC, " + MediaStore.Audio.Media.TRACK + " ASC";
        queryAndPopulate(uri, projection, selection, selectionArgs, sortOrder);
    }

    private void queryAndPopulate(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor cursor = getContentResolver().query(uri, projection, selection, selectionArgs, sortOrder);
        if (cursor != null) {
            int idColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID);
            int titleColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE);
            int artistColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST);
            int albumColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM);
            int durationColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION);
            int pathColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA);

            while (cursor.moveToNext()) {
                long id = cursor.getLong(idColumn);
                String title = cursor.getString(titleColumn);
                String artist = cursor.getString(artistColumn);
                String album = cursor.getString(albumColumn);
                long duration = cursor.getLong(durationColumn);
                String path = cursor.getString(pathColumn);
                songList.add(new Song(id, title, artist, album, duration, path));
            }
            cursor.close();
            musicAdapter.notifyDataSetChanged();
        }
    }
}

