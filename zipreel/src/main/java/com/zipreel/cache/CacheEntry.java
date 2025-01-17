package com.zipreel.cache;

import com.zipreel.model.Movie;

import java.util.List;

public class CacheEntry {

    private final String key;
    private final List<Movie> movies;
    private int frequency;
    private long timestamp;

    public CacheEntry(String key, List<Movie> movies) {
        this.key = key;
        this.movies = movies;
        this.frequency = 0;    //look after
        this.timestamp = System.currentTimeMillis();
    }

    public String getKey() {
        return key;
    }

    public List<Movie> getMovies() {
        return movies;
    }

    public int getFrequency() {
        return frequency;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void incrementFrequency() {
        this.frequency++;
    }

    public void updateTimestamp() {
        this.timestamp = System.currentTimeMillis();
    }
}
