package com.zipreel.service;

import com.zipreel.model.Movie;

import java.util.List;

public record SearchResult(List<Movie> movies, String cacheLevel) {
    public SearchResult {
        movies = List.copyOf(movies);
    }
}
