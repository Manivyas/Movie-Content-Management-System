package com.zipreel.service;

import com.zipreel.cache.L1Cache;
import com.zipreel.cache.L2Cache;
import com.zipreel.model.Movie;
import com.zipreel.model.User;

import javax.lang.model.util.SimpleElementVisitor14;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class ZipReelService {
    private final L2Cache l2Cache;
    private final L1Cache l1Cache;
    private final Map<String, Movie> movies;
    private final Map<String, User> users;
    private int primaryStoreHits;
    private int totalSearches;

    public ZipReelService() {
        this.l2Cache = new L2Cache();
        this.l1Cache = new L1Cache();
        this.movies = new HashMap<>();
        this.users = new HashMap<>();
        this.primaryStoreHits = 0;
        this.totalSearches = 0;
    }

    public void addMovie(String id, String title, String genre, int year, double rating) {
        validateMovieInput(id, title, genre, year, rating);

        if (movies.containsKey(id)) {
            throw new IllegalArgumentException("Movie with id " + id + " already exists");
        }

        movies.put(id, new Movie(id, title, genre, year, rating));
        System.out.println("Movie added successfully: " + title);
    }

    public void addUser(String id, String name, String preferredGenre) {
        validateUserInput(id, name, preferredGenre);

        if (users.containsKey(id)) {
            throw new IllegalArgumentException("User with id " + id + " already exists");
        }

        users.put(id, new User(id, name, preferredGenre));
        System.out.println("User added successfully: " + name);
    }

    public void search(String userId, String searchType, String searchValue) {
        validateSearchInput(userId, searchType, searchValue);

        totalSearches++;
        String cacheKey = searchType + ":" + searchValue;

        SearchResult result = performSearch(userId, cacheKey, () ->
                searchInPrimaryStore(searchType, searchValue));

        printResults(result);
    }

    public void searchMulti(String userId, String genre, int year, double rating) {
        validateSearchMultiInput(userId, genre, year, rating);

        totalSearches++;
        String cacheKey = "multi:" + genre + ":" + year + ":" + rating;

        SearchResult result = performSearch(userId, cacheKey, () ->
                searchMultiInPrimaryStore(genre, year, rating));

        printResults(result);
    }

    private SearchResult performSearch(String userId, String cacheKey, Supplier<List<Movie>> primaryStoreSearcher) {
        List<Movie> results = l1Cache.get(userId, cacheKey);
        if (results != null) {
            return new SearchResult(results, "L1 Cache");
        }

        results = l2Cache.get(cacheKey);
        if (results != null) {
            l1Cache.put(userId, cacheKey, results);
            return new SearchResult(results, "L2 Cache");

        }

        primaryStoreHits++;
        results = primaryStoreSearcher.get();

        l2Cache.put(cacheKey, results);
        l1Cache.put(userId, cacheKey, results);

        return new SearchResult(results, "Primary Store");
    }

    private List<Movie> searchInPrimaryStore(String searchType, String searchValue) {
        return movies.values().stream()
                .filter(movie -> {
                    if (searchType.equals("GENRE")) {
                        return movie.getGenre().equalsIgnoreCase(searchValue);
                    } else if (searchType.equals("TITLE")) {
                        return movie.getTitle().equalsIgnoreCase(searchValue);
                    }
                    return false;
                }).collect(Collectors.toList());
    }

    private List<Movie> searchMultiInPrimaryStore(String genre, int year, double rating) {
        return movies.values().stream()
                .filter(movie -> movie.getGenre().equalsIgnoreCase(genre) &&
                        movie.getYear() == year &&
                        movie.getRating() >= rating)
                .collect(Collectors.toList());
    }

    public void viewCacheStats() {
        System.out.println("L1 Cache Hits: " + l1Cache.getHits());
        System.out.println("L2 Cache Hits: " + l2Cache.getHits());
        System.out.println("Primary Store Hits: " + primaryStoreHits);
        System.out.println("Total Searches: " + totalSearches);
    }

    public void clearCache(String cacheLevel) {
        switch (cacheLevel.toUpperCase()) {
            case "L1":
                l1Cache.clear();
                System.out.println("L1 Cache cleared");
                break;
            case "L2":
                l2Cache.clear();
                System.out.println("L2 Cache cleared");
                break;
            default:
                throw new IllegalArgumentException("Invalid cache level: " + cacheLevel);
        }
    }

    private void validateMovieInput(String id, String title, String genre, int year, double rating) {
        if (id == null || id.isEmpty()) {
            throw new IllegalArgumentException("Movie id cannot be empty");
        }
        if (title == null || title.isEmpty()) {
            throw new IllegalArgumentException("Movie title cannot be empty");
        }
        if (genre == null || genre.isEmpty()) {
            throw new IllegalArgumentException("Movie genre cannot be empty");
        }
        if (year < 1900 || year > 2026) {
            throw new IllegalArgumentException("Invalid movie year: " + year);
        }
        if (rating < 0 || rating > 10) {
            throw new IllegalArgumentException("Invalid movie rating: " + rating);
        }
    }

    private void validateUserInput(String id, String name, String preferredGenre) {
        if (id == null || id.isEmpty()) {
            throw new IllegalArgumentException("User id cannot be empty");
        }
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("User name cannot be empty");
        }
        if (preferredGenre == null || preferredGenre.isEmpty()) {
            throw new IllegalArgumentException("User preferred genre cannot be empty");
        }
    }

    private void validateSearchInput(String userId, String searchType, String searchValue) {
        if (!users.containsKey(userId)) {
            throw new IllegalArgumentException("User with id " + userId + " does not exist");
        }
        if (!searchType.equals("GENRE") && !searchType.equals("TITLE")) {
            throw new IllegalArgumentException("Invalid search type: " + searchType);
        }
        if (searchValue == null || searchValue.isEmpty()) {
            throw new IllegalArgumentException("Search value cannot be empty");
        }
    }

    private void validateSearchMultiInput(String userId, String genre, int year, double rating) {
        if (!users.containsKey(userId)) {
            throw new IllegalArgumentException("User with id " + userId + " does not exist");
        }
        if (genre == null || genre.isEmpty()) {
            throw new IllegalArgumentException("Genre cannot be empty");
        }
        if (year < 1900 || year > 2026) {
            throw new IllegalArgumentException("Invalid year: " + year);
        }
        if (rating < 0 || rating > 10) {
            throw new IllegalArgumentException("Invalid rating: " + rating);
        }
    }

    private void printResults(SearchResult result) {
        if(result.movies().isEmpty()) {
            System.out.println("No movies found");
            return;
        }

        for(Movie movie : result.movies()) {
            System.out.println(movie.getTitle() + " (Found in " + result.cacheLevel() + ")");
        }
    }
}
