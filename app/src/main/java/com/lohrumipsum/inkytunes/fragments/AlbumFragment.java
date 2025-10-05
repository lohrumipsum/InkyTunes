package com.lohrumipsum.inkytunes.fragments;

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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.lohrumipsum.inkytunes.R;
import com.lohrumipsum.inkytunes.adapters.AlbumAdapter;
import com.lohrumipsum.inkytunes.models.Album;

import java.util.ArrayList;
import java.util.List;

public class AlbumFragment extends Fragment {

    private RecyclerView recyclerView;
    private AlbumAdapter albumAdapter;
    private List<Album> albumList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list, container, false);

        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        albumList = new ArrayList<>();
        albumAdapter = new AlbumAdapter(getContext(), albumList);
        recyclerView.setAdapter(albumAdapter);

        loadAlbums();

        return view;
    }

    private void loadAlbums() {
        Uri uri = MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI;
        String[] projection = {
                MediaStore.Audio.Albums._ID,
                MediaStore.Audio.Albums.ALBUM,
                MediaStore.Audio.Albums.ARTIST
        };

        // Sort albums alphabetically
        String sortOrder = MediaStore.Audio.Albums.ALBUM + " ASC";

        Cursor cursor = getContext().getContentResolver().query(uri, projection, null, null, sortOrder);

        if (cursor != null) {
            int idColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Albums._ID);
            int titleColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Albums.ALBUM);
            int artistColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Albums.ARTIST);

            while (cursor.moveToNext()) {
                long id = cursor.getLong(idColumn);
                String title = cursor.getString(titleColumn);
                String artist = cursor.getString(artistColumn);

                albumList.add(new Album(id, title, artist));
            }
            cursor.close();
            albumAdapter.notifyDataSetChanged();
        }
    }
}

