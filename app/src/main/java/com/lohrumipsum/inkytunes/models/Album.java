package com.lohrumipsum.inkytunes.models;

public class Album {
    private final long id;
    private final String title;
    private final String artist;

    public Album(long id, String title, String artist) {
        this.id = id;
        this.title = title;
        this.artist = artist;
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
}
