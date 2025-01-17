package com.zipreel.model;

public class User {

    private final String id;
    private final String name;
    private final String preferredGenre;

    public User(String id, String name, String preferredGenre) {
        this.id = id;
        this.name = name;
        this.preferredGenre = preferredGenre;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getPreferredGenre() {
        return preferredGenre;
    }

    @Override
    public String toString() {
        return "User{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", preferredGenre='" + preferredGenre + '\'' +
                '}';
    }
}
