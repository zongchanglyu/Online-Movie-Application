package edu.uci.ics.fabflixmobile;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
import org.json.JSONArray;
import org.json.JSONException;
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

        String contents = bundle.getString("message");

        try {
            JSONArray jsonArray = new JSONArray(contents);
            movies = new ArrayList<>();
            if(jsonArray != null){
                for(int i = 0; i < jsonArray.length(); i++){
                    JSONObject tmp = jsonArray.getJSONObject(i);
                    String id = tmp.getString("movie_id");
                    String title = tmp.getString("movie_title");
                    String year = tmp.getString("movie_year");
                    String director = tmp.getString("director");
                    JSONArray genres = tmp.getJSONArray("genres_name");
                    Set<String> genresName = new HashSet<>();
                    for(int j = 0; j < genres.length(); j++){
                        genresName.add(genres.getString(j));
                    }
                    JSONArray stars = tmp.getJSONArray("stars_name");
                    Set<String> starsName = new HashSet<>();
                    for(int j = 0; j < stars.length(); j++){
                        starsName.add(stars.getJSONObject(j).getString("star_name"));
                    }
                    float rating = (float) tmp.getDouble("rating");

                    movies.add(new Movie(id, title, year, director, rating, genresName, starsName));
                }
            }else{
                Log.d("login.failed", contents);
            }
        }catch(JSONException e){
            Log.e("MYAPP", "unexpected JSON exception", e);
        }

        MovieListViewAdapter adapter = new MovieListViewAdapter(movies, this);

        ListView listView = findViewById(R.id.list);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Movie movie = movies.get(position);
                String message = String.format("Clicked on position: %d, name: %s, %s", position, movie.getTitle(), movie.getYear());
                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
            }
        });
    }
}