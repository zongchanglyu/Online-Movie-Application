package edu.uci.ics.fabflixmobile;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.*;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.*;

public class ListViewActivity extends Activity {

    private ArrayList<Movie> movies;

    private int numOfData;
    private int numOfPages;
    private int tmpPage;

    private Button prevButton;
    private Button nextButton;
    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.listview);

        Bundle bundle = getIntent().getExtras();
        String data = bundle.getString("data");

        try{
            JSONArray jsonArray = new JSONArray(data);
            movies = new ArrayList<>();

            numOfData = jsonArray.length();
            numOfPages = numOfData / 20;
            tmpPage = 0;

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

        prevButton = findViewById(R.id.prev);
        nextButton = findViewById(R.id.next);

//        prevButton.setEnabled(false);
        checkButton();

        prevButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "page " + --tmpPage, Toast.LENGTH_SHORT).show();
                showList(tmpPage);
            }
        });

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "page " + ++tmpPage, Toast.LENGTH_SHORT).show();
                showList(tmpPage);
            }
        });

        listView = findViewById(R.id.list);

        showList(tmpPage);

//        MovieListViewAdapter adapter = new MovieListViewAdapter(movies, this);
//
//        ListView listView = findViewById(R.id.list);
//        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                position += tmpPage * 20;
                Movie movie = movies.get(position);
                String message = String.format("Clicked on position: %d, title: %s, id: %s", position, movie.getTitle(), movie.getId());
                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();

                Log.d("onItemClick.success", message);

                goToSingleMoviePage(movie.getId());
            }
        });
    }

    private void checkButton() {
        if(tmpPage <= 0){
            prevButton.setEnabled(false);
        }else{
            prevButton.setEnabled(true);
        }

        if(tmpPage >= numOfPages){
            nextButton.setEnabled(false);
        }else{
            nextButton.setEnabled(true);
        }
    }

    private void showList(int tmpPage) {
        ArrayList<Movie> tmp = new ArrayList<>();
        for(int i = tmpPage * 20; i < Math.min(movies.size(), tmpPage * 20 + 20); i++){
            tmp.add(movies.get(i));
        }
        checkButton();

        MovieListViewAdapter adapter = new MovieListViewAdapter(tmp, this);
        listView.setAdapter(adapter);
    }

    private void goToSingleMoviePage(String id) {
        String url;
//        url = "https://192.168.0.106:8443/fabflix/api/";
//        url = "https://new.soommate.com:47373/fabflix/api/";
//        url = "https://71.69.162.72:47373/fabflix/api/";
        url = "https://52.53.150.120:8443/fabflix/api/";

        // Use the same network queue across our application
        final RequestQueue queue = NetworkManager.sharedManager(this).queue;
        //request type is POST
        final StringRequest searchRequest = new StringRequest(Request.Method.GET, url + "single-movie?id=" + id, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //TODO should parse the json response to redirect to appropriate functions.
                        try{
                            if(response != null){
                                Log.d("single-movie.success", response);
                                Intent singleMoviePage = new Intent(ListViewActivity.this, SingleMoviePage.class);
                                singleMoviePage.putExtra("singleMovieData", response);
                                startActivity(singleMoviePage);
                            }else{
                                Log.d("mobile-search.failed", response);
                            }
                        }catch(Exception e){
                            Log.e("Search", "unexpected JSON exception", e);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        Log.d("login.error", error.toString());
                    }
                }) {
            //            @Override
            protected Map<String, String> getParams() {
                // Post request form data
                final Map<String, String> params = new HashMap<>();
                params.put("id", id);

                return params;
            }
        };

        // !important: queue.add is where the login request is actually sent
        queue.add(searchRequest);
    }
}
