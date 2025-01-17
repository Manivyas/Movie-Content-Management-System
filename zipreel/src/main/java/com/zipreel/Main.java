package com.zipreel;

import com.zipreel.service.ZipReelService;

import java.util.Scanner;

public class Main {
    private static final String PROMPT = "Enter command (or 'exit' to quit): ";
    private static final ZipReelService service = new ZipReelService();

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Welcome to ZipReel Movie Management System");
        System.out.println("Available commands:");
        System.out.println("1. ADD_MOVIE <id> <title> <genre> <year> <rating>");
        System.out.println("2. ADD_USER <id> <name> <preferred_genre>");
        System.out.println("3. SEARCH <user_id> <search_type> <search_value>");
        System.out.println("4. SEARCH_MULTI <user_id> <genre> <year> <min_rating>");
        System.out.println("5. VIEW_CACHE_STATS");
        System.out.println("6. CLEAR_CACHE <cache_level>");
        System.out.println("Type 'exit' to quit\n");

        while (true) {
            try {
                System.out.print(PROMPT);
                String input = scanner.nextLine().trim();

                if (input.equalsIgnoreCase("exit")) {
                    break;
                }

                processCommand(input);

            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
            System.out.println(); // Empty line for readability
        }

        scanner.close();
        System.out.println("Thank you for using ZipReel!");
    }

    private static void processCommand(String input) {
        String[] parts = input.split(" (?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)"); // Split by space but respect quotes

        if (parts.length == 0) {
            throw new IllegalArgumentException("Empty command");
        }

        String command = parts[0].toUpperCase();

        switch (command) {
            case "ADD_MOVIE" -> handleAddMovie(parts);
            case "ADD_USER" -> handleAddUser(parts);
            case "SEARCH" -> handleSearch(parts);
            case "SEARCH_MULTI" -> handleSearchMulti(parts);
            case "VIEW_CACHE_STATS" -> handleViewCacheStats(parts);
            case "CLEAR_CACHE" -> handleClearCache(parts);
            default -> throw new IllegalArgumentException("Unknown command: " + command);
        }
    }

    private static void handleAddMovie(String[] parts) {
        if (parts.length != 6) {
            throw new IllegalArgumentException("Invalid ADD_MOVIE command. Format: ADD_MOVIE <id> <title> <genre> <year> <rating>");
        }

        String id = parts[1];
        String title = parts[2].replaceAll("\"", "");
        String genre = parts[3].replaceAll("\"", "");
        int year = Integer.parseInt(parts[4]);
        double rating = Double.parseDouble(parts[5]);

        service.addMovie(id, title, genre, year, rating);
    }

    private static void handleAddUser(String[] parts) {
        if (parts.length != 4) {
            throw new IllegalArgumentException("Invalid ADD_USER command. Format: ADD_USER <id> <name> <preferred_genre>");
        }

        String id = parts[1];
        String name = parts[2].replaceAll("\"", "");
        String preferredGenre = parts[3].replaceAll("\"", "");

        service.addUser(id, name, preferredGenre);
    }

    private static void handleSearch(String[] parts) {
        if (parts.length != 4) {
            throw new IllegalArgumentException("Invalid SEARCH command. Format: SEARCH <user_id> <search_type> <search_value>");
        }

        String userId = parts[1];
        String searchType = parts[2];
        String searchValue = parts[3].replaceAll("\"", "");

        service.search(userId, searchType, searchValue);
    }

    private static void handleSearchMulti(String[] parts) {
        if (parts.length != 5) {
            throw new IllegalArgumentException("Invalid SEARCH_MULTI command. Format: SEARCH_MULTI <user_id> <genre> <year> <min_rating>");
        }

        String userId = parts[1];
        String genre = parts[2].replaceAll("\"", "");
        int year = Integer.parseInt(parts[3]);
        double minRating = Double.parseDouble(parts[4]);

        service.searchMulti(userId, genre, year, minRating);
    }

    private static void handleViewCacheStats(String[] parts) {
        if (parts.length != 1) {
            throw new IllegalArgumentException("Invalid VIEW_CACHE_STATS command. No parameters required.");
        }

        service.viewCacheStats();
    }

    private static void handleClearCache(String[] parts) {
        if (parts.length != 2) {
            throw new IllegalArgumentException("Invalid CLEAR_CACHE command. Format: CLEAR_CACHE <cache_level>");
        }

        service.clearCache(parts[1]);
    }
}