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
@WebServlet(name = "SearchServlet", urlPatterns = "/api/search_destroy")
public class SearchServlet extends HttpServlet {
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
        title = "%" + title + "%";
        String year = request.getParameter("year");
        if("".equals(year)) year = "%";
        else if(year.length() < 4) year = "11111";
        String director = request.getParameter("director");
        director = "%" + director + "%";
        String starName = request.getParameter("starName");
        starName = "%" + starName + "%";

        // Output stream to STDOUT
        PrintWriter out = response.getWriter();

        try {
            // Get a connection from dataSource
            Connection dbcon = dataSource.getConnection();

            // Construct a query with parameter represented by "?"
            String query = "select distinct movies.*, ratings.rating " +
                           "from movies, stars_in_movies as sim, stars, ratings " +
                           "where movies.title like ? and movies.year like ? and movies.director like ? " +
                           "and movies.id = sim.movieId and sim.starId = stars.id and stars.name like ? " +
                           "and movies.id = ratings.movieId;";
            // Declare our statement
            PreparedStatement Statement = dbcon.prepareStatement(query);


            // Set the parameter represented by "?" in the query to the id we get from url,
            // num 1 indicates the first "?" in the query

            Statement.setString(1, title);
            Statement.setString(2, year);
            Statement.setString(3, director);
            Statement.setString(4, starName);

            System.out.println(Statement);
            // Perform the query
            ResultSet rs = Statement.executeQuery();

            JsonArray jsonArray = new JsonArray();


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

            // get session, if exit, rewrite
            HttpSession session = request.getSession();
            JsonArray previousList = (JsonArray)session.getAttribute("previousList");
            if (previousList != null) {
                previousList = null;
            }
            session.setAttribute("previousList", jsonArray);

            JsonObject responseJsonObject = new JsonObject();
            responseJsonObject.addProperty("status", "success");

            System.out.println("set attribute finished");

            out.write(responseJsonObject.toString());
            System.out.println(responseJsonObject.toString());
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