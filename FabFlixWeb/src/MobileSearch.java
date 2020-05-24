import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import javax.annotation.Resource;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

// Declaring a WebServlet called MovieListServlet, which maps to url "/api/mobile-search"
@WebServlet(name = "MobileSearch", urlPatterns = "/api/mobile-search")
public class MobileSearch extends HttpServlet {
    private static final long serialVersionUID = 2L;

    // Create a dataSource which registered in web.xml
    @Resource(name = "jdbc/moviedb")
    private DataSource dataSource;

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
     * response)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        response.setContentType("application/json"); // Response mime type

        // Output stream to STDOUT
        PrintWriter out = response.getWriter();

//        String title = "%" + request.getParameter("title") + "%";
        String title = request.getParameter("title");
        String [] arrTitle = title.split("\\s+");
        title = "";
        for(String s: arrTitle){
            title += "+" + s + "* ";
        }

        try{
            // Get a connection from dataSource
            Connection dbcon = dataSource.getConnection();

            String query = "select distinct movies.*, ratings.rating from movies, ratings " +
                    "where match (movies.title) against (? in boolean mode) " +
                    "and movies.id = ratings.movieId " +
                    "order by rating desc, title asc " +
                    ";";

            PreparedStatement statement = dbcon.prepareStatement(query);
            statement.setString(1, title);

            // Perform the query
            ResultSet rs = statement.executeQuery();

            JsonArray jsonArray = new JsonArray();

            while (rs.next()) {
                String movie_id = rs.getString("id");
                String movie_title = rs.getString("title");
                String movie_year = rs.getString("year");
                String movie_director = rs.getString("director");
                float rating = rs.getFloat("rating");

                // use movie_id to get 3 stars with new sql sentences
                String starsQuery = "select count(*) as count, tmp.* " +
                        "from (select stars.name, stars.id from stars, stars_in_movies as sim " +
                        "where stars.id = sim.starId and sim.movieId = ? ) as tmp, " +
                        "movies, stars_in_movies as sim " +
                        "where movies.id = sim.movieId and sim.starId = tmp.id " +
                        "group by sim.starId order by count desc, name asc limit 3;";

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
                String genresQuery = "select genres.* from genres join genres_in_movies as gim " +
                        "on genres.id = gim.genreId and gim.movieId = ? order by name limit 3;";
                // Declare our statement
                PreparedStatement genresStatement = dbcon.prepareStatement(genresQuery);
                // Set the parameter represented by "?" in the query to the id we get from url,
                // num 1 indicates the first "?" in the query
                genresStatement.setString(1, movie_id);
                ResultSet genresRS = genresStatement.executeQuery();
                JsonArray genresJsonArray = new JsonArray();
                while(genresRS.next()){
                    String genre_name = genresRS.getString("name");
                    String genre_id = genresRS.getString("id");

                    JsonObject genreJsonObject = new JsonObject();
                    genreJsonObject.addProperty("genre_name", genre_name);
                    genreJsonObject.addProperty("genre_id", genre_id);
                    genresJsonArray.add(genreJsonObject);
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
            // set response status to 200 (OK)
            response.setStatus(200);

            rs.close();
            statement.close();
            dbcon.close();
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