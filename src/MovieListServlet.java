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
import java.sql.SQLException;

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
//        String query = "";

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
//            JsonElement genreElement = movieParameter.get("genre");
//            String genre = genreElement == null ? null : genreElement.getAsString();
            JsonElement genreIdElement = movieParameter.get("genreId");
            String genreId = genreIdElement == null ? null : genreIdElement.getAsString();
            JsonElement firstLaterElement = movieParameter.get("firstLater");
            String firstLater = firstLaterElement == null ? null : firstLaterElement.getAsString();
            JsonElement orderByElement = movieParameter.get("orderBy");
            String orderBy = orderByElement == null ? "rating desc, title asc" : orderByElement.getAsString();
            JsonElement numberOfListElement = movieParameter.get("numberOfList");
            String numberOfList = numberOfListElement == null ? "10" : numberOfListElement.getAsString();
            JsonElement pageElement = movieParameter.get("page");
            String page = pageElement == null ? "0" : pageElement.getAsString();
            JsonElement numOfDataElement = movieParameter.get("numOfData");
            String numOfData = numOfDataElement == null ? "0" : numOfDataElement.getAsString();

            int offsetInt = (Integer.parseInt(page) * Integer.parseInt(numberOfList));
            String offset = String.valueOf(offsetInt);

            ResultSet rs;
            JsonArray jsonArray = new JsonArray();
            System.out.println(status);
            String query = "";
            PreparedStatement statement = null;

//            switch (status){
//                case "adv-search" : rs = executeAdvSearch(movieParameter); break;
//                case "browse-by-genre" : rs = executeBrowseByGenre(movieParameter); break;
//                case "browse-by-title" : rs = executeBrowseByTitle(movieParameter); break;
//            }

            if("adv-search".equals(status)){
                String sumQuery = "select count(*) as count from (select distinct movies.*, ratings.rating " +
                        "from movies, stars_in_movies as sim, stars, ratings " +
                        "where movies.title like ? and movies.year like ? and movies.director like ? " +
                        "and movies.id = sim.movieId and sim.starId = stars.id and stars.name like ? " +
                        "and movies.id = ratings.movieId) as tmp ;";

                PreparedStatement tmpStatement = dbcon.prepareStatement(sumQuery);
                tmpStatement.setString(1, title);
                tmpStatement.setString(2, year);
                tmpStatement.setString(3, director);
                tmpStatement.setString(4, starName);

                ResultSet tmpRS = tmpStatement.executeQuery();
                if(tmpRS.next()){
                    numOfData = tmpRS.getString("count");
                    movieParameter.addProperty("numOfData", numOfData);
                }
                tmpRS.close();
                tmpStatement.close();


                query = "select distinct movies.*, ratings.rating " +
                        "from movies, stars_in_movies as sim, stars, ratings " +
                        "where movies.title like ? and movies.year like ? and movies.director like ? " +
                        "and movies.id = sim.movieId and sim.starId = stars.id and stars.name like ? " +
                        "and movies.id = ratings.movieId " +
                        "order by " + orderBy + " " +
                        "limit " + numberOfList + " " +
                        "offset " + offset + ";";

                // Declare our statement
                statement = dbcon.prepareStatement(query);

                // Set the parameter represented by "?" in the query to the id we get from url,
                // num 1 indicates the first "?" in the query
                statement.setString(1, title);
                statement.setString(2, year);
                statement.setString(3, director);
                statement.setString(4, starName);

            }else if("browse-by-genre".equals(status)){
                String sumQuery = "select count(*) as count from (select movies.*, ratings.rating " +
                        "from movies, ratings, genres_in_movies as gim " +
                        "where ratings.movieId = movies.id and movies.id = gim.movieId and gim.genreId = ?) as tmp ;";

                PreparedStatement tmpStatement = dbcon.prepareStatement(sumQuery);
                tmpStatement.setString(1, genreId);

                ResultSet tmpRS = tmpStatement.executeQuery();
                if(tmpRS.next()){
                    numOfData = tmpRS.getString("count");
                    movieParameter.addProperty("numOfData", numOfData);
                }
                tmpRS.close();
                tmpStatement.close();


                query = "select movies.*, ratings.rating " +
                        "from movies, ratings, genres_in_movies as gim " +
                        "where ratings.movieId = movies.id and movies.id = gim.movieId and gim.genreId = ? " +
                        "order by " + orderBy + " " +
                        "limit " + numberOfList + " " +
                        "offset " + offset + ";";

                // Declare our statement
                statement = dbcon.prepareStatement(query);

                // Set the parameter represented by "?" in the query to the id we get from url,
                // num 1 indicates the first "?" in the query
                statement.setString(1, genreId);


            }else if("browse-by-title".equals(status)){
                String sumQuery = "select count(*) as count from (select  movies.*, ratings.rating " +
                        "from movies, ratings " +
                        "where ratings.movieId = movies.id and movies.title like ? ) as tmp ;";

                PreparedStatement tmpStatement = dbcon.prepareStatement(sumQuery);
                tmpStatement.setString(1, firstLater + "%");

                ResultSet tmpRS = tmpStatement.executeQuery();
                if(tmpRS.next()){
                    numOfData = tmpRS.getString("count");
                    movieParameter.addProperty("numOfData", numOfData);
                }
                tmpRS.close();
                tmpStatement.close();


                query = "select  movies.*, ratings.rating " +
                        "from movies, ratings " +
                        "where ratings.movieId = movies.id and movies.title like ? " +
                        "order by " + orderBy + " " +
                        "limit " + numberOfList + " " +
                        "offset " + offset + ";";

                // Declare our statement
                statement = dbcon.prepareStatement(query);

                // Set the parameter represented by "?" in the query to the id we get from url,
                // num 1 indicates the first "?" in the query
                statement.setString(1, firstLater + "%");
            }else if("browse-other".equals(status)){
                String sumQuery = "select count(*) as count from (select  movies.*, ratings.rating " +
                        "from movies, ratings " +
                        "where ratings.movieId = movies.id and movies.title regexp '^[^a-zA-Z0-9]' ) as tmp ;";

                PreparedStatement tmpStatement = dbcon.prepareStatement(sumQuery);

                ResultSet tmpRS = tmpStatement.executeQuery();
                if(tmpRS.next()){
                    numOfData = tmpRS.getString("count");
                    movieParameter.addProperty("numOfData", numOfData);
                }
                tmpRS.close();
                tmpStatement.close();


                query = "select  movies.*, ratings.rating " +
                        "from movies, ratings " +
                        "where ratings.movieId = movies.id and movies.title regexp '^[^a-zA-Z0-9]' " +
                        "order by " + orderBy + " " +
                        "limit " + numberOfList + " " +
                        "offset " + offset + ";";

                // Declare our statement
                statement = dbcon.prepareStatement(query);

            }

            // Perform the query
            rs = statement.executeQuery();

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
/*
* select count(*) as count, tmp.* from (select stars.name, stars.id from stars, stars_in_movies as sim
where stars.id = sim.starId and sim.movieId = "tt0362227") as tmp, movies, stars_in_movies as sim
where movies.id = sim.movieId and sim.starId = tmp.id group by sim.starId order by count desc, name asc limit 3;
* */
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
                jsonObject.addProperty("orderBy", orderBy);
                jsonObject.addProperty("numberOfList", numberOfList);
                jsonObject.addProperty("page", page);
                jsonObject.addProperty("numOfData", numOfData);

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
/*
    private ResultSet executeAdvSearch(JsonObject movieParameter) throws SQLException {
        JsonElement titleElement = movieParameter.get("title");
        String title = titleElement == null ? null : titleElement.getAsString();
        JsonElement yearElement = movieParameter.get("year");
        String year = yearElement == null ? null : yearElement.getAsString();
        JsonElement directorElement = movieParameter.get("director");
        String director = directorElement == null ? null : directorElement.getAsString();
        JsonElement starNameElement = movieParameter.get("starName");
        String starName = starNameElement == null ? null : starNameElement.getAsString();

        // Get a connection from dataSource
        Connection dbcon = dataSource.getConnection();
        String query = "select distinct movies.*, ratings.rating " +
                       "from movies, stars_in_movies as sim, stars, ratings " +
                       "where movies.title like ? and movies.year like ? and movies.director like ? " +
                       "and movies.id = sim.movieId and sim.starId = stars.id and stars.name like ? " +
                       "and movies.id = ratings.movieId;";
        // Declare our statement
        PreparedStatement statement = dbcon.prepareStatement(query);
        // Set the parameter represented by "?" in the query to the id we get from url,
        // num 1 indicates the first "?" in the query
        statement.setString(1, title);
        statement.setString(2, year);
        statement.setString(3, director);
        statement.setString(4, starName);

        return statement.executeQuery();
    }

    private ResultSet executeBrowseByGenre(JsonObject movieParameter) throws SQLException {
        JsonElement genreIdElement = movieParameter.get("genreId");
        String genreId = genreIdElement == null ? null : genreIdElement.getAsString();

        // Get a connection from dataSource
        Connection dbcon = dataSource.getConnection();
        String query = "select  movies.*, ratings.rating " +
                       "from movies, ratings, genres_in_movies as gim " +
                       "where ratings.movieId = movies.id and movies.id = gim.movieId and gim.genreId = ?;";

        // Declare our statement
        PreparedStatement statement = dbcon.prepareStatement(query);

        // Set the parameter represented by "?" in the query to the id we get from url,
        // num 1 indicates the first "?" in the query
        statement.setString(1, genreId);

        return statement.executeQuery();
    }

    private ResultSet executeBrowseByTitle(JsonObject movieParameter) throws SQLException {
        JsonElement firstLaterElement = movieParameter.get("firstLater");
        String firstLater = firstLaterElement == null ? null : firstLaterElement.getAsString();
        firstLater += "%";

        // Get a connection from dataSource
        Connection dbcon = dataSource.getConnection();
        String query = "select  movies.*, ratings.rating " +
                "from movies, ratings " +
                "where ratings.movieId = movies.id and movies.title like ?;";

        // Declare our statement
        PreparedStatement statement = dbcon.prepareStatement(query);

        // Set the parameter represented by "?" in the query to the id we get from url,
        // num 1 indicates the first "?" in the query
        statement.setString(1, firstLater);

        return statement.executeQuery();
    }
*/
}