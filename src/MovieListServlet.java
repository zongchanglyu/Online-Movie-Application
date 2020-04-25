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

// Declaring a WebServlet called MovieListServlet, which maps to url "/api/movie-list"
@WebServlet(name = "MovieListServlet", urlPatterns = "/api/movie-list")
public class MovieListServlet extends HttpServlet {
    private static final long serialVersionUID = 2L;

    // Create a dataSource which registered in web.xml
    @Resource(name = "jdbc/moviedb")
    private DataSource dataSource;

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
     * response)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json"); // Response mime type

        // Output stream to STDOUT
        PrintWriter out = response.getWriter();
        String query = "";

        try{
            // Get a connection from dataSource
            Connection dbcon = dataSource.getConnection();

            // Get a instance of current session on the request
            HttpSession session = request.getSession();
            JsonObject movieParameter = (JsonObject) session.getAttribute("movieParameter");

            String status = movieParameter.get("status").getAsString();
            JsonElement titleElement = movieParameter.get("title");
            String title = titleElement == null ? null : titleElement.getAsString();
            JsonElement yearElement = movieParameter.get("year");
            String year = yearElement == null ? null : yearElement.getAsString();
            JsonElement directorElement = movieParameter.get("director");
            String director = directorElement == null ? null : directorElement.getAsString();
            JsonElement starNameElement = movieParameter.get("starName");
            String starName = starNameElement == null ? null : starNameElement.getAsString();
            JsonElement genreElement = movieParameter.get("genre");
            String genre = genreElement == null ? null : genreElement.getAsString();
            JsonElement genreIdElement = movieParameter.get("genreId");
            String genreId = genreIdElement == null ? null : genreIdElement.getAsString();
            JsonElement orderByElement = movieParameter.get("orderBy");
            String orderBy = orderByElement == null ? null : orderByElement.getAsString();
            JsonElement NumberOfListElement = movieParameter.get("NumberOfList");
            String NumberOfList = NumberOfListElement == null ? null : NumberOfListElement.getAsString();

            PreparedStatement statement = null;
            ResultSet rs = null;
            JsonArray jsonArray = new JsonArray();
            if("adv-search".equals(status)){
                System.out.println("status is adv-search");
                query = "select distinct movies.*, ratings.rating " +
                        "from movies, stars_in_movies as sim, stars, ratings " +
                        "where movies.title like ? and movies.year like ? and movies.director like ? " +
                        "and movies.id = sim.movieId and sim.starId = stars.id and stars.name like ? " +
                        "and movies.id = ratings.movieId;";

                // Declare our statement
                statement = dbcon.prepareStatement(query);

                // Set the parameter represented by "?" in the query to the id we get from url,
                // num 1 indicates the first "?" in the query
                statement.setString(1, title);
                statement.setString(2, year);
                statement.setString(3, director);
                statement.setString(4, starName);

                System.out.println(statement);
                // Perform the query
                rs = statement.executeQuery();
            }else if("browse-by-genre".equals(status)){
                System.out.println("status is browse-by-genre");
            }

            // Iterate through each row of rs
            while (rs.next()) {
                String movie_id = rs.getString("id");
                String movie_title = rs.getString("title");
                String movie_year = rs.getString("year");
                String movie_director = rs.getString("director");
                float rating = rs.getFloat("rating");

                // use movie_id to get 3 stars with new sql sentences
                String starsQuery = "select stars.name, stars.id from stars join stars_in_movies as sim " +
                        "on stars.id = sim.starId and sim.movieId = ? limit 3;";
                // Declare our statement
                PreparedStatement starsStatement = dbcon.prepareStatement(starsQuery);
                // Set the parameter represented by "?" in the query to the id we get from url,
                // num 1 indicates the first "?" in the query
                starsStatement.setString(1, movie_id);
                ResultSet starsRS = starsStatement.executeQuery();
                JsonArray starsJsonArray = new JsonArray();
                while(starsRS.next()){
                    String star_name = starsRS.getString("name");
                    String star_id = starsRS.getString("id");

                    JsonObject starJsonObject = new JsonObject();
                    starJsonObject.addProperty("star_name", star_name);
                    starJsonObject.addProperty("star_id", star_id);
                    starsJsonArray.add(starJsonObject);
                }

                // use movie_id to get 3 genres with new sql sentences
                String genresQuery = "select genres.name from genres join genres_in_movies as gim " +
                        "on genres.id = gim.genreId and gim.movieId = ? limit 3;";
                // Declare our statement
                PreparedStatement genresStatement = dbcon.prepareStatement(genresQuery);
                // Set the parameter represented by "?" in the query to the id we get from url,
                // num 1 indicates the first "?" in the query
                genresStatement.setString(1, movie_id);
                ResultSet genresRS = genresStatement.executeQuery();
                JsonArray genresJsonArray = new JsonArray();
                while(genresRS.next()){
                    String star_name = genresRS.getString("name");
                    genresJsonArray.add(star_name);
                }

                // Create a JsonObject based on the data we retrieve from rs
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("movie_id", movie_id);
                jsonObject.addProperty("movie_title", movie_title);
                jsonObject.addProperty("movie_year", movie_year);
                jsonObject.addProperty("movie_director", movie_director);
                jsonObject.add("stars_name", starsJsonArray);
                jsonObject.add("genres_name", genresJsonArray);
                jsonObject.addProperty("rating", rating);

                jsonArray.add(jsonObject);
            }

            // write JSON string to output
            out.write(jsonArray.toString());
            System.out.println("write jsonArray to out");
            // set response status to 200 (OK)
            response.setStatus(200);
        }catch (Exception e) {
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