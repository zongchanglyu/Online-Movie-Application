import com.google.gson.JsonArray;
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
@WebServlet(name = "browseResultServlet", urlPatterns = "/api/browse-result")
public class browseResultServlet extends HttpServlet {
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

        // Retrieve parameter id from url request.
        String genres = request.getParameter("genres");

        // Output stream to STDOUT
        PrintWriter out = response.getWriter();

        try {
            // Get a connection from dataSource
            Connection dbcon = dataSource.getConnection();

            String query = "select gim.movieId " +
                    "from genres, genres_in_movies as gim " +
                    "where genres.id = gim.genreId and genres.name = ? limit 10";

            PreparedStatement Statement = dbcon.prepareStatement(query);

            Statement.setString(1, genres);

            ResultSet rs = Statement.executeQuery();

            JsonArray jsonArray = new JsonArray();


            // Iterate through each row of rs
            while (rs.next()) {
                String id = rs.getString("movieId");

                // Construct a query with parameter represented by "?"
                String movieQuery = "select movies.*, ratings.rating from movies, ratings " +
                        "where movies.id = ratings.movieId and movies.id = ? ;";


                String starsQuery = "select stars.* from stars, stars_in_movies as sim " +
                        "where stars.id = sim.starId and sim.movieId = ? ;";

                // Declare our statement
                PreparedStatement movieStatement = dbcon.prepareStatement(movieQuery);
                PreparedStatement starsStatement = dbcon.prepareStatement(starsQuery);

                // Set the parameter represented by "?" in the query to the id we get from url,
                // num 1 indicates the first "?" in the query
                movieStatement.setString(1, id);
                starsStatement.setString(1, id);

                // Perform the query
                ResultSet movieRS = movieStatement.executeQuery();
                ResultSet starsRS = starsStatement.executeQuery();

                // First, get movie info
                movieRS.next();
                String movieId = movieRS.getString("id");
                String movieTitle = movieRS.getString("title");
                String movieYear = movieRS.getString("year");
                String movieDirector = movieRS.getString("director");
                float rating = movieRS.getFloat("rating");

                // Second, iterate and put genres info into genresJsonArray


                // Third, iterate and put stars info into starsJsonArray
                JsonArray starsJsonArray = new JsonArray();
                while (starsRS.next()) {
                    String starId = starsRS.getString("id");
                    String starName = starsRS.getString("name");
                    String starDob = starsRS.getString("birthYear");

                    JsonObject starsJsonObject = new JsonObject();
                    starsJsonObject.addProperty("star_id", starId);
                    starsJsonObject.addProperty("star_name", starName);
                    starsJsonObject.addProperty("star_dob", starDob);

                    starsJsonArray.add(starsJsonObject);
                }

                // Put all properties into jsonObject
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("movie_id", movieId);
                jsonObject.addProperty("movie_title", movieTitle);
                jsonObject.addProperty("movie_year", movieYear);
                jsonObject.addProperty("movie_director", movieDirector);
                jsonObject.addProperty("movie_rating", rating);
                jsonObject.addProperty("genres", genres);
                jsonObject.add("stars", starsJsonArray);

                jsonArray.add(jsonObject);
            }
                // write JSON string to output
                out.write(jsonArray.toString());
                // set response status to 200 (OK)
                response.setStatus(200);

//                starsRS.close();
//                movieRS.close();
//
//                starsStatement.close();
//                movieStatement.close();


                rs.close();

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