package com.lohrumipsum.inkytunes.adapters;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.media3.common.MediaItem;
import androidx.media3.session.MediaController;
import androidx.recyclerview.widget.RecyclerView;

import com.lohrumipsum.inkytunes.R;
import com.lohrumipsum.inkytunes.models.Song;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MusicAdapter extends RecyclerView.Adapter<MusicAdapter.ViewHolder> {

    private final Context context;
    private final List<Song> songList;
    private MediaController mediaController;

    public MusicAdapter(Context context, List<Song> songList) {
        this.context = context;
        this.songList = songList;
    }

    public void setMediaController(MediaController mediaController) {
        this.mediaController = mediaController;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Song song = songList.get(position);
        holder.titleTextView.setText(song.getTitle());
        holder.artistTextView.setText(song.getArtist());

        holder.itemView.setOnClickListener(v -> {
            if (mediaController != null) {
                List<MediaItem> mediaItems = new ArrayList<>();
                for (Song s : songList) {
                    // Using fromFile is more robust for local paths
                    mediaItems.add(MediaItem.fromUri(Uri.fromFile(new File(s.getPath()))));
                }
                mediaController.setMediaItems(mediaItems, position, 0);
                mediaController.prepare();
                mediaController.play();
            }
        });
    }

    @Override
    public int getItemCount() {
        return songList.size();
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

