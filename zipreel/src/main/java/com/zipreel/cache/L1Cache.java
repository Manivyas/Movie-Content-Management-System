package com.zipreel.cache;

import com.zipreel.model.Movie;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class L1Cache {

    private final Map<String, Map<String, CacheEntry>> userCache;
    private int hits;

    public L1Cache() {
        this.userCache = new HashMap<>();
        this.hits = 0;
    }

    public List<Movie> get(String userId, String searchKey) {
        Map<String, CacheEntry> userEntries = userCache.get(userId);
        if(userEntries != null && userEntries.containsKey(searchKey)) {
            hits++;
            CacheEntry entry = userEntries.get(searchKey);
            entry.updateTimestamp();
            return entry.getMovies();
        }
        return null;
    }

    public void put(String userId, String searchKey, List<Movie> movies) {
        userCache.putIfAbsent(userId, new LinkedHashMap<>());
        Map<String, CacheEntry> userEntries = userCache.get(userId);

        if (userEntries.size() >= 5) {
            evictLRU(userEntries);
        }
    }

    private void evictLRU(Map<String, CacheEntry> userEntries) {
        String lruKey = userEntries.entrySet().stream()
                .min((entry1, entry2) -> Long.compare(entry1.getValue().getTimestamp(), entry2.getValue().getTimestamp()))
                .map(Map.Entry::getKey)
                .orElse(null);

        if (lruKey != null) {
            userEntries.remove(lruKey);
        }
    }

    public int getHits() {
        return hits;
    }

    public void clear() {
        userCache.clear();
        hits = 0;
    }
}
