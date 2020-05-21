package edu.uci.ics.fabflixmobile;

import java.util.Set;

public class Movie {
    private String id;
    private String title;
    private String year;
    private String director;
    private float rating;
    private Set<String> genres;
    private Set<String> stars;

    public Movie(String id, String title, String year, String director, float rating, Set<String> genres, Set<String> stars) {
        this.id = id;
        this.title = title;
        this.year = year;
        this.director = director;
        this.rating = rating;
        this.genres = genres;
        this.stars = stars;
    }

    public Movie(String title, String year) {
        this.title = title;
        this.year = year;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getYear() {
        return year;
    }

    public String getDirector() {
        return director;
    }

    public float getRating() {
        return rating;
    }

    public Set<String> getGenres() {
        return genres;
    }

    public Set<String> getStars() {
        return stars;
    }
}