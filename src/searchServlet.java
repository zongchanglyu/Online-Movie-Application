import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import javax.annotation.Resource;
import javax.servlet.ServletException;
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

// Declaring a WebServlet called SingleStarServlet, which maps to url "/api/single-star"
@WebServlet(name = "searchServlet", urlPatterns = "/api/search")
public class searchServlet extends HttpServlet {
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

        // Retrieve parameters from url request.
        String title = request.getParameter("title");
        String year = request.getParameter("year");
        String director = request.getParameter("director");
        String starName = request.getParameter("starName");

        // Output stream to STDOUT
        PrintWriter out = response.getWriter();

        try {
            // Get a connection from dataSource
            Connection dbcon = dataSource.getConnection();

            // Construct a query with parameter represented by "?"


            String query = "select stars.*, movies.* from stars, stars_in_movies as sim, movies " +
                    "where stars.id = sim.starId and sim.movieId = movies.id " +
                    "and movies.title = %?% and movies.director = %?% and movies.year = ? and stars.name = %?% ;";

            // Declare our statement
            PreparedStatement Statement = dbcon.prepareStatement(query);


            // Set the parameter represented by "?" in the query to the id we get from url,
            // num 1 indicates the first "?" in the query

            Statement.setString(1, title);
            Statement.setString(2, director);
            Statement.setString(3, starName);

            // Perform the query
            ResultSet rs = Statement.executeQuery();


            // First, get movie info
            rs.next();
            String movieId = rs.getString("id");
            String movieTitle = rs.getString("title");
            String movieYear = rs.getString("year");
            String movieDirector = rs.getString("director");


            // Second, iterate and put genres info into genresJsonArray

//            JsonArray genresJsonArray = new JsonArray();
//            while (rs.next()) {
//                String genreName = rs.getString("name");
//                genresJsonArray.add(genreName);
//            }

            // Third, iterate and put stars info into starsJsonArray

//            JsonArray starsJsonArray = new JsonArray();
//            while (starsRS.next()) {
//                String starId = starsRS.getString("id");
//                String starName = starsRS.getString("name");
//                String starDob = starsRS.getString("birthYear");
//
//                JsonObject starsJsonObject = new JsonObject();
//                starsJsonObject.addProperty("star_id", starId);
//                starsJsonObject.addProperty("star_name", starName);
//                starsJsonObject.addProperty("star_dob", starDob);
//
//                starsJsonArray.add(starsJsonObject);
//            }

            // Put all properties into jsonObject
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("movie_id", movieId);
            jsonObject.addProperty("movie_title", movieTitle);
            jsonObject.addProperty("movie_year", movieYear);
            jsonObject.addProperty("movie_director", movieDirector);


            // write JSON string to output
            out.write(jsonObject.toString());
            // set response status to 200 (OK)
            response.setStatus(200);

            rs.close();

            Statement.close();

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