import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

// Declaring a WebServlet called SingleStarServlet, which maps to url "/api/single-star"
@WebServlet(name = "SingleMovieServlet", urlPatterns = "/api/single-movie")
public class SingleMovieServlet extends HttpServlet {
    private static final long serialVersionUID = 2L;

    // Create a dataSource which registered in web.xml
    @Resource(name = "jdbc/moviedb")
    private DataSource dataSource;

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
     *      response)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json"); // Response mime type

        // Retrieve parameter id from url request.
        String id = request.getParameter("id");

        // Output stream to STDOUT
        PrintWriter out = response.getWriter();

        try {
            // Get a connection from dataSource
            Connection dbcon = dataSource.getConnection();

            // Construct a query with parameter represented by "?"
            String movieQuery = "select movies.*, ratings.rating from movies, ratings " +
                           "where movies.id = ratings.movieId and movies.id = ? ;";

            String genresQuery = "select genres.* from genres, genres_in_movies as gim " +
                           "where genres.id = gim.genreId and gim.movieId = ? order by name;";

            String starsQuery = "select count(*) as count, tmp.* " +
                                "from (select stars.* from stars, stars_in_movies as sim " +
                                "where stars.id = sim.starId and sim.movieId = ? ) as tmp, " +
                                "movies, stars_in_movies as sim " +
                                "where movies.id = sim.movieId and sim.starId = tmp.id " +
                                "group by sim.starId order by count desc, name asc;";

/*
*   select count(*) as count, tmp.* from (select stars.name, stars.id from stars, stars_in_movies as sim
    where stars.id = sim.starId and sim.movieId = "tt0362227") as tmp, movies, stars_in_movies as sim
    where movies.id = sim.movieId and sim.starId = tmp.id group by sim.starId order by count desc, name asc;
* */

            // Declare our statement
            PreparedStatement movieStatement = dbcon.prepareStatement(movieQuery);
            PreparedStatement genresStatement = dbcon.prepareStatement(genresQuery);
            PreparedStatement starsStatement = dbcon.prepareStatement(starsQuery);

            // Set the parameter represented by "?" in the query to the id we get from url,
            // num 1 indicates the first "?" in the query
            movieStatement.setString(1, id);
            genresStatement.setString(1, id);
            starsStatement.setString(1, id);

            // Perform the query
            ResultSet movieRS = movieStatement.executeQuery();
            ResultSet genresRS = genresStatement.executeQuery();
            ResultSet starsRS = starsStatement.executeQuery();

            System.out.println("check 1");
            // First, get movie info
            movieRS.next();
            String movieId = movieRS.getString("id");
            String movieTitle = movieRS.getString("title");
            String movieYear = movieRS.getString("year");
            String movieDirector = movieRS.getString("director");
            float rating = movieRS.getFloat("rating");

            System.out.println("check 2");
            // Second, iterate and put genres info into genresJsonArray
            JsonArray genresJsonArray = new JsonArray();
            while(genresRS.next()){
                String genreName = genresRS.getString("name");
                String genreId = genresRS.getString("id");

                JsonObject genresJsonObject = new JsonObject();
                genresJsonObject.addProperty("genre_id", genreId);;
                genresJsonObject.addProperty("genre_name", genreName);

                genresJsonArray.add(genresJsonObject);
            }

            System.out.println("check 3");
            // Third, iterate and put stars info into starsJsonArray
            JsonArray starsJsonArray = new JsonArray();
            while(starsRS.next()){
                String starId = starsRS.getString("id");
                String starName = starsRS.getString("name");
                String starDob = starsRS.getString("birthYear");

                JsonObject starsJsonObject = new JsonObject();
                starsJsonObject.addProperty("star_id", starId);
                starsJsonObject.addProperty("star_name", starName);
                starsJsonObject.addProperty("star_dob", starDob);

                starsJsonArray.add(starsJsonObject);
            }


            // page info from session
            HttpSession session = request.getSession();
            JsonObject movieParameter = (JsonObject) session.getAttribute("movieParameter");
            String display;
            if(movieParameter == null){
                display = "0";
            }else{
                display = "1";
            }

            // Put all properties into jsonObject
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("display", display);
            jsonObject.addProperty("movie_id", movieId);
            jsonObject.addProperty("movie_title", movieTitle);
            jsonObject.addProperty("movie_year", movieYear);
            jsonObject.addProperty("movie_director", movieDirector);
            jsonObject.addProperty("movie_rating", rating);
            jsonObject.add("genres", genresJsonArray);
            jsonObject.add("stars", starsJsonArray);

            // write JSON string to output
            out.write(jsonObject.toString());
            // set response status to 200 (OK)
            response.setStatus(200);

            starsRS.close();
            genresRS.close();
            movieRS.close();
            starsStatement.close();
            genresStatement.close();
            movieStatement.close();
            dbcon.close();
        } catch (Exception e) {
            // write error message JSON object to output
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("errorMessage", e.getMessage());
            out.write(jsonObject.toString());

            // set reponse status to 500 (Internal Server Error)
            response.setStatus(500);
        }
        out.close();
        //close it;

    }

}