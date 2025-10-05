package com.lohrumipsum.inkytunes.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.lohrumipsum.inkytunes.R;
import com.lohrumipsum.inkytunes.activities.TrackListActivity;
import com.lohrumipsum.inkytunes.models.Album;

import java.util.List;

public class AlbumAdapter extends RecyclerView.Adapter<AlbumAdapter.ViewHolder> {

    private final Context context;
    private final List<Album> albumList;

    public AlbumAdapter(Context context, List<Album> albumList) {
        this.context = context;
        this.albumList = albumList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Album album = albumList.get(position);
        holder.titleTextView.setText(album.getTitle());
        holder.artistTextView.setText(album.getArtist());
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, TrackListActivity.class);
            intent.putExtra(TrackListActivity.EXTRA_ALBUM_ID, album.getId());
            intent.putExtra(TrackListActivity.EXTRA_TITLE, album.getTitle());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return albumList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView;
        TextView artistTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.list_item_title);
            artistTextView = itemView.findViewById(R.id.list_item_subtitle);
        }
    }
}

