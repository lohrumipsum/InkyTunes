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
import com.lohrumipsum.inkytunes.adapters.ArtistAdapter;
import com.lohrumipsum.inkytunes.models.Artist;

import java.util.ArrayList;
import java.util.List;

public class ArtistFragment extends Fragment {

    private RecyclerView recyclerView;
    private ArtistAdapter artistAdapter;
    private List<Artist> artistList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list, container, false);

        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        artistList = new ArrayList<>();
        artistAdapter = new ArtistAdapter(getContext(), artistList);
        recyclerView.setAdapter(artistAdapter);

        loadArtists();

        return view;
    }

    private void loadArtists() {
        Uri uri = MediaStore.Audio.Artists.EXTERNAL_CONTENT_URI;
        String[] projection = {
                MediaStore.Audio.Artists._ID,
                MediaStore.Audio.Artists.ARTIST
        };

        // Sort artists alphabetically
        String sortOrder = MediaStore.Audio.Artists.ARTIST + " ASC";

        Cursor cursor = getContext().getContentResolver().query(uri, projection, null, null, sortOrder);

        if (cursor != null) {
            int idColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Artists._ID);
            int nameColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Artists.ARTIST);

            while (cursor.moveToNext()) {
                long id = cursor.getLong(idColumn);
                String name = cursor.getString(nameColumn);

                artistList.add(new Artist(id, name));
            }
            cursor.close();
            artistAdapter.notifyDataSetChanged();
        }
    }
}

