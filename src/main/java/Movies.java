package main.java;

import java.util.List;

public class Movies {

    private String title;

    private String id;

    private int year;

    private String director;

    private List<String> genres;

    public Movies(){

    }

    public Movies(String title, String id, int year, String director, List<String> genres) {
        this.title = title;
        this.id  = id;
        this.year = year;
        this.director = director;
        this.genres = genres;
    }



    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public String getDirector() {
        return director;
    }

    public void setDirector(String director) {
        this.director = director;
    }

    public List<String> getGenres() {
        return genres;
    }

    public void setGenres(List<String> genres) {
        this.genres = genres;
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("Movies Details - ");
        sb.append("title:" + getTitle());
        sb.append(", ");
        sb.append("id:" + getId());
        sb.append(", ");
        sb.append("year:" + getYear());
        sb.append(", ");
        sb.append("director:" + getDirector());
        sb.append(", ");
        sb.append("genres:" + getGenres());
        sb.append(".");

        return sb.toString();
    }
}
