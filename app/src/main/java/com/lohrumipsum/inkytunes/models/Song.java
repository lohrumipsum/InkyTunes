package com.lohrumipsum.inkytunes.models;

public class Song {
    private final long id;
    private final String title;
    private final String artist;
    private final String album;
    private final long duration;
    private final String path;

    public Song(long id, String title, String artist, String album, long duration, String path) {
        this.id = id;
        this.title = title;
        this.artist = artist;
        this.album = album;
        this.duration = duration;
        this.path = path;
    }

    public long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getArtist() {
        return artist;
    }

    public String getAlbum() {
        return album;
    }

    public long getDuration() {
        return duration;
    }

    public String getPath() {
        return path;
    }
}

