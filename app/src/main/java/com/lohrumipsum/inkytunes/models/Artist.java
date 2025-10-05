package com.lohrumipsum.inkytunes.models;

public class Artist {
    private final long id;
    private final String name;

    public Artist(long id, String name) {
        this.id = id;
        this.name = name;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
