package edu.uci.ics.fabflixmobile;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.*;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class SingleMoviePage extends Activity {

    private Movie tmpMovie;
    private TextView movieTitle;
    private TextView subtitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.singlemovie);

        Bundle bundle = getIntent().getExtras();
        String data = bundle.getString("singleMovieData");

        try{
            JSONObject jsonObject = new JSONObject(data);
            String id = jsonObject.getString("movie_id");
            String title = jsonObject.getString("movie_title");
            String year = jsonObject.getString("movie_year");
            String director = jsonObject.getString("movie_director");
            float rating = (float)jsonObject.getDouble("movie_rating");
            JSONArray starsArray = jsonObject.getJSONArray("stars");
            Set<String> stars = new HashSet<>();
            for(int j = 0; j < starsArray.length(); j++)
                stars.add(starsArray.getJSONObject(j).getString("star_name"));
            JSONArray genresArray = jsonObject.getJSONArray("genres");
            Set<String> genres = new HashSet<>();
            for(int j = 0; j < genresArray.length(); j++)
                genres.add(genresArray.getJSONObject(j).getString("genre_name"));

            tmpMovie = new Movie(id, title, year, director, rating, genres, stars);


            movieTitle = findViewById(R.id.movieTitle);
            subtitle = findViewById(R.id.subtitle);

            movieTitle.setText(tmpMovie.getTitle());
            subtitle.setText("year: " + tmpMovie.getYear() + "\nrating: " + tmpMovie.getRating() + "\ndirector: " + tmpMovie.getDirector() +
                    "\ngenre: " + tmpMovie.getGenresString() + "\nstars: " + tmpMovie.getStarsString());

            Log.d("SingleMoviePage", data);

        }catch(Exception e){
            Log.e("SingleMoviePage", "unexpected JSON exception", e);
        }


    }
}
