package model;

public class Movie {
    // Encapsulation
    private String id;
    private String title;
    private String category;
    private String director;
    private String actors;
    private int releaseYear;
    private double rating;
    private long views;
    private long favouritesCount;
    private boolean isActive;

    // Constructor không tham số
    public Movie () {
        this.isActive = true;
        this.views = 0;
        this.favouritesCount = 0;
        this.rating = 0.0;
    }

    // Constructor đầy đủ tham số
    public Movie (String id, String title, String category, String director, String actors, int releaseYear, double rating, long views, long favouritesCount, boolean isActive) {
        this.id = id;
        this.title = title;
        this.category = category;
        this.director = director;
        this.actors = actors;
        this.releaseYear = releaseYear;
        this.rating = rating;
        this.views = views;
        this.favouritesCount = favouritesCount;
        this.isActive = isActive;
    }

    



}




