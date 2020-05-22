package edu.uci.ics.fabflixmobile;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class ListViewActivity extends Activity {

    private ArrayList<Movie> movies;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.listview);

        Bundle bundle = getIntent().getExtras();
        String data = bundle.getString("data");

        try{
            JSONArray jsonArray = new JSONArray(data);
            movies = new ArrayList<>();

            for(int i = 0; i < jsonArray.length(); i++){
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String id = jsonObject.getString("movie_id");
                String title = jsonObject.getString("movie_title");
                String year = jsonObject.getString("movie_year");
                String director = jsonObject.getString("movie_director");
                float rating = (float)jsonObject.getDouble("rating");;
                JSONArray starsArray = jsonObject.getJSONArray("stars_name");
                Set<String> stars = new HashSet<>();
                for(int j = 0; j < starsArray.length(); j++)
                    stars.add(starsArray.getJSONObject(j).getString("star_name"));
                JSONArray genresArray = jsonObject.getJSONArray("genres_name");
                Set<String> genres = new HashSet<>();
                for(int j = 0; j < genresArray.length(); j++)
                    genres.add(genresArray.getJSONObject(j).getString("genre_name"));

                Movie movie = new Movie(id, title, year, director, rating, genres, stars);
                movies.add(movie);
            }
        }catch(Exception e){
            Log.e("ListViewActivity", "unexpected JSON exception", e);
        }

        MovieListViewAdapter adapter = new MovieListViewAdapter(movies, this);

        ListView listView = findViewById(R.id.list);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Movie movie = movies.get(position);
                String message = String.format("Clicked on position: %d, title: %s, %s", position, movie.getTitle(), movie.getYear());
                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
