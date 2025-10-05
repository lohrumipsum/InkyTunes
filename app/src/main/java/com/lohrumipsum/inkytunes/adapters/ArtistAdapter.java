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
import com.lohrumipsum.inkytunes.models.Artist;

import java.util.List;

public class ArtistAdapter extends RecyclerView.Adapter<ArtistAdapter.ViewHolder> {

    private final Context context;
    private final List<Artist> artistList;

    public ArtistAdapter(Context context, List<Artist> artistList) {
        this.context = context;
        this.artistList = artistList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Artist artist = artistList.get(position);
        holder.titleTextView.setText(artist.getName());
        holder.subtitleTextView.setText(""); // No subtitle for artist view
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, TrackListActivity.class);
            intent.putExtra(TrackListActivity.EXTRA_ARTIST_ID, artist.getId());
            intent.putExtra(TrackListActivity.EXTRA_TITLE, artist.getName());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return artistList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView;
        TextView subtitleTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.list_item_title);
            subtitleTextView = itemView.findViewById(R.id.list_item_subtitle);
        }
    }
}

