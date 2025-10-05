package com.lohrumipsum.inkytunes.fragments;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.media3.session.MediaController;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.lohrumipsum.inkytunes.R;
import com.lohrumipsum.inkytunes.activities.MainActivity;
import com.lohrumipsum.inkytunes.adapters.MusicAdapter;
import com.lohrumipsum.inkytunes.models.Song;

import java.util.ArrayList;
import java.util.List;

public class TrackFragment extends Fragment implements MainActivity.ControllerCallback {

    private RecyclerView recyclerView;
    private MusicAdapter musicAdapter;
    private List<Song> songList = new ArrayList<>();
    private MediaController mediaController;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof MainActivity) {
            ((MainActivity) context).registerCallback(this);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list, container, false);
        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        loadMusic();
        return view;
    }

    private void loadMusic() {
        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String[] projection = {
                MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.ALBUM,
                MediaStore.Audio.Media.DURATION,
                MediaStore.Audio.Media.DATA
        };
        String selection = MediaStore.Audio.Media.IS_MUSIC + "!= 0";

        Cursor cursor = requireContext().getContentResolver().query(uri, projection, selection, null, null);

        if (cursor != null) {
            // To prevent duplication when view is re-created
            if (songList.isEmpty()) {
                while (cursor.moveToNext()) {
                    long id = cursor.getLong(0);
                    String title = cursor.getString(1);
                    String artist = cursor.getString(2);
                    String album = cursor.getString(3);
                    long duration = cursor.getLong(4);
                    String path = cursor.getString(5);
                    songList.add(new Song(id, title, artist, album, duration, path));
                }
            }
            cursor.close();
        }

        musicAdapter = new MusicAdapter(getContext(), songList);
        // If the controller is already available, set it on the new adapter
        if (mediaController != null) {
            musicAdapter.setMediaController(mediaController);
        }
        recyclerView.setAdapter(musicAdapter);
    }

    @Override
    public void onControllerReady(@NonNull MediaController mediaController) {
        // Store the controller for later
        this.mediaController = mediaController;
        // If the adapter has already been created, set the controller on it
        if (musicAdapter != null) {
            musicAdapter.setMediaController(mediaController);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Context context = getContext();
        if (context instanceof MainActivity) {
            ((MainActivity) context).unregisterCallback(this);
        }
    }
}

