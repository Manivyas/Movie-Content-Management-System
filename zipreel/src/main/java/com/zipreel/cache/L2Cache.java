package com.zipreel.cache;

import com.zipreel.model.Movie;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class L2Cache {
    private final Map<String, CacheEntry> globalCache;
    private int hits;

    public L2Cache() {
        this.globalCache = new HashMap<>();
        this.hits = 0;
    }

    public List<Movie> get(String searchKey) {
        CacheEntry entry = globalCache.get(searchKey);
        if (entry != null) {
            hits++;
            entry.incrementFrequency();
            return entry.getMovies();
        }
        return null;
    }

    public void put(String searchKey, List<Movie> movies) {
        if(globalCache.size() >= 20) {
            evictLRU();
        }
        globalCache.put(searchKey, new CacheEntry(searchKey, movies));
    }

    private void evictLRU() {
        String lruKey = globalCache.entrySet().stream()
                .min((entry1, entry2) -> Integer.compare(entry1.getValue().getFrequency(), entry2.getValue().getFrequency()))
                .map(Map.Entry::getKey)
                .orElse(null);

        if (lruKey != null) {
            globalCache.remove(lruKey);
        }
    }

    public int getHits() {
        return hits;
    }

    public void clear() {
        globalCache.clear();
        hits = 0;
    }
}
