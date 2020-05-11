package main.java;

import java.sql.*;
import java.util.*;

public class XmlParse {

    public static void main(String[] args) throws ClassNotFoundException, SQLException, InstantiationException, IllegalAccessException {

        /**
         * connect to database
         * */

        long startTime = System.currentTimeMillis();

        System.out.println("connect to database....");


        Connection conn = null;
        Class.forName("com.mysql.jdbc.Driver").newInstance();
        String jdbcURL = "jdbc:mysql://localhost:3306/moviedb";

        try {
            conn = DriverManager.getConnection(jdbcURL, "mytestuser", "mypassword");
        } catch (
                SQLException e) {
            e.printStackTrace();
        }

        /**
         * parse movies into database
         * */
        System.out.println("parse movies into database");

        ParseMovie pm = new ParseMovie();
        pm.run();
        List<Movies> myMovies = pm.getMyMovies();
        Set<String> allKindsOfGenres = pm.getAllKindsOfGenres();


        // oldMovies<director, tmpMovie<title, year>>
        Map<String, Map<String, Integer>> oldMovies = new HashMap<>();
        // avoid duplicate movieId
        Set<String> movieId = new HashSet<>();
        // save all movieTitle
        Map<String, String> movieTitleId = new HashMap<>();
//        Set<String> movieTitle = new HashSet<>();

        String query = "select * from movies";

        PreparedStatement statement = conn.prepareStatement(query);
        ResultSet result = statement.executeQuery();

        while (result.next()) {
            Map<String, Integer> tempMovie = oldMovies.getOrDefault(result.getString("director"), new HashMap<>());
            tempMovie.put(result.getString("title"), result.getInt("year"));
            oldMovies.put(result.getString("director"), tempMovie);

            movieId.add(result.getString("id"));
//            movieTitle.add(result.getString("title"));
            movieTitleId.put(result.getString("title"), result.getString("id"));
        }

        conn.setAutoCommit(false);

        query = "insert into genres (name) select ? from DUAL where NOT EXISTS(select name from genres where name = ?);";

        statement = conn.prepareStatement(query);

        for (String genre : allKindsOfGenres) {
            if (genre == null) continue;
            statement.setString(1, genre);
            statement.setString(2, genre);
            statement.addBatch();
        }

        statement.executeBatch();
        conn.commit();

        System.out.println("insert new genres finished!!!");

        conn.setAutoCommit(false);

        String insertSql = "insert into movies(id, title, year, director) values ( ?, ?, ?, ? );";

        String gimSql = "insert into genres_in_movies (genreId, movieId) values ((select id from genres where name = ?), ?)";

        String ratingSql = "insert into ratings (movieId) values ( ? );";

        PreparedStatement psInsertRecord = conn.prepareStatement(insertSql);
        PreparedStatement gimStatement = conn.prepareStatement(gimSql);
        PreparedStatement ratingStatement = conn.prepareStatement(ratingSql);

        for (Movies movie : myMovies) {
            if (oldMovies.containsKey(movie.getDirector()) && oldMovies.get(movie.getDirector()).containsKey(movie.getTitle()) || movie.getId() == null) {
//                System.out.println("duplicate movie: " + movie.getTitle());
                continue;
            }

            while (movieId.contains(movie.getId()))
                movie.setId(movie.getId() + "1");
            movieId.add(movie.getId());

//            movieTitle.add(movie.getTitle());
            movieTitleId.put(movie.getTitle(), movie.getId());

            psInsertRecord.setString(1, movie.getId());
            psInsertRecord.setString(2, movie.getTitle());
            psInsertRecord.setInt(3, movie.getYear());
            psInsertRecord.setString(4, movie.getDirector());

            ratingStatement.setString(1, movie.getId());

            Map<String, Integer> tempMovie = oldMovies.getOrDefault(movie.getDirector(), new HashMap<>());
            tempMovie.put(movie.getTitle(), movie.getYear());
            oldMovies.put(movie.getDirector(), tempMovie);
            if (movie.getGenres() != null && movie.getGenres().size() > 0) {
                for (String genre : movie.getGenres()) {
                    if (genre == null) continue;
                    gimStatement.setString(1, genre);
                    gimStatement.setString(2, movie.getId());
                    gimStatement.addBatch();
                }
            }

            psInsertRecord.addBatch();

            ratingStatement.addBatch();
        }

        psInsertRecord.executeBatch();
        gimStatement.executeBatch();

        ratingStatement.executeBatch();
        conn.commit();

        System.out.println("insert movies and genres_in_movies finished!");
        System.out.println("Time spent on insert movies and genres_in_movies is: "+ (double)(System.currentTimeMillis() - startTime)/1000 + " Seconds");


        /**
         * parse stars into database
         * */

        ParseStar ps = new ParseStar();
        ps.run();
        Set<Stars> myStars = ps.getMyStars();
        // map<starName, dob>
        Map<String, Integer> oldStars = new HashMap<>();
        // map<starName, starId>
        Map<String, String> starNameId = new HashMap<>();


        query = "select * from stars;";

        statement = conn.prepareStatement(query);

        result = statement.executeQuery();

        while(result.next()){
            String name = result.getString("name");
            Integer birthYear = result.getInt("birthYear");
            oldStars.put(name, birthYear);

            String id = result.getString("id");
            starNameId.put(name, id);
        }

        System.out.println("old stars size is: " + oldStars.size());

        conn.setAutoCommit(false);

        query = "select max(id) as starId from stars";

        statement = conn.prepareStatement(query);
        ResultSet rs = statement.executeQuery();
        rs.next();

        String starId = rs.getString("starId");
        String prefix = starId.substring(0, 2);
        int number = Integer.parseInt(starId.substring(2));

        query = "insert into stars (id, name, birthYear) values (?,?,?) ";
        statement = conn.prepareStatement(query);

        for(Stars star : myStars){
            if(oldStars.containsKey(star.getName()) && oldStars.get(star.getName())==star.getBirthYear()){
                continue;
            }

            String id = prefix + (++number);

            statement.setString(1, id);
            statement.setString(2, star.getName());
            statement.setInt(3, star.getBirthYear());

            oldStars.put(star.getName(), star.getBirthYear());
            starNameId.put(star.getName(), id);

            statement.addBatch();
        }

        statement.executeBatch();
        conn.commit();

        System.out.println("insert stars data into db finished!!!");
        System.out.println("Time spent on insert stars data into db is: "+ (double)(System.currentTimeMillis() - startTime)/1000 + " Seconds");


        /**
         * parse stars_in_movies into database
         * */

        ParseStarsInMovies psim = new ParseStarsInMovies();
        psim.run();
        Map<String, String> myStarsInMovies = psim.getMyStarsInMovies();
        System.out.println("myStarsInMovies size is: "+ myStarsInMovies.size());
        Set<String[]> myNewStarsInMovies = psim.getMyNewStarsInMovies();
        System.out.println("myNewStarsInMovies size is: "+ myNewStarsInMovies.size());

        query = "select max(id) as id from stars";
        statement = conn.prepareStatement(query);
        rs = statement.executeQuery();
        rs.next();

        starId = rs.getString("id");
        prefix = starId.substring(0, 2);
        number = Integer.parseInt(starId.substring(2));


        conn.setAutoCommit(false);
/*
        //just in case of some extra new stars data in casts124.xml, so we want to insert stars again.
        String starInsertQuery = "insert into stars (id, name, birthYear) values (?,?,?) ";
        PreparedStatement starInsertStatement = conn.prepareStatement(starInsertQuery);

        query = "insert into stars_in_movies (starId, movieId) " +
                "values ((select id from stars where name = ? limit 1), (select id from movies where title = ? limit 1))";
        statement = conn.prepareStatement(query);

        String tmpQuery = "insert into stars_in_movies (starId, movieId) " +
                          "values( ? , (select id from movies where title = ? limit 1));";
        PreparedStatement tmpQueryStatement = conn.prepareStatement(tmpQuery);

        int count = 0;

        for(String starName: myStarsInMovies.keySet()){
            if(movieTitle.contains(myStarsInMovies.get(starName))){
                if("Forrest Gump".equals(myStarsInMovies.get(starName))) System.out.println("Forrest Gump found");
                if("Tom Hanks".equals(starName)) System.out.println("Tom Hanks found");
                if(!oldStars.containsKey(starName)){
                    String tmpStarId = prefix + (++number);
                    starInsertStatement.setString(1, tmpStarId);
                    starInsertStatement.setString(2, starName);
                    starInsertStatement.setInt(3,0);
                    starInsertStatement.addBatch();

                    oldStars.put(starName, 0);

                    tmpQueryStatement.setString(1, tmpStarId);
                    tmpQueryStatement.setString(2, myStarsInMovies.get(starName));
                    tmpQueryStatement.addBatch();
                }else{
                    statement.setString(1, starName);
                    statement.setString(2, myStarsInMovies.get(starName));
                    statement.addBatch();
                }
            }
            else{
                // maybe a new movie, not sure add to db or not
                count++;

//                System.out.println("movie title not found: " + myStarsInMovies.get(starName));
            }
        }
        System.out.println("sum of movie title not fount: " + count + "\nmaybe a new movie, not sure add to db or not");
*/

        String starInsertQuery = "insert into stars (id, name, birthYear) values (?,?,?) ";
        PreparedStatement starInsertStatement = conn.prepareStatement(starInsertQuery);

        query = "insert into stars_in_movies (starId, movieId) " +
                "values (? , ? )";
        statement = conn.prepareStatement(query);

        String tmpQuery = "insert into stars_in_movies (starId, movieId) " +
                "values( ? , ? );";
        PreparedStatement tmpQueryStatement = conn.prepareStatement(tmpQuery);

        int count = 0;

        for(String[] StarName_MovieName : myNewStarsInMovies){
            if(movieTitleId.containsKey(StarName_MovieName[1])){
//                if("Forrest Gump".equals(StarName_MovieName[1])) System.out.println("Forrest Gump found");
//                if("Tom Hanks".equals(StarName_MovieName[0])) System.out.println("Tom Hanks found");
                if(!oldStars.containsKey(StarName_MovieName[0])){
                    String tmpStarId = prefix + (++number);
                    starInsertStatement.setString(1, tmpStarId);
                    starInsertStatement.setString(2, StarName_MovieName[0]);
                    starInsertStatement.setInt(3,0);
                    starInsertStatement.addBatch();

                    oldStars.put(StarName_MovieName[0], 0);
                    starNameId.put(StarName_MovieName[0], tmpStarId);

                    tmpQueryStatement.setString(1, tmpStarId);
                    tmpQueryStatement.setString(2, movieTitleId.get(StarName_MovieName[1]));
                    tmpQueryStatement.addBatch();
                }else{
                    statement.setString(1, starNameId.get(StarName_MovieName[0]));
                    statement.setString(2, movieTitleId.get(StarName_MovieName[1]));
                    statement.addBatch();
                }
            }
            else{
                // maybe a new movie, not sure add to db or not
                count++;

//                System.out.println("movie title not found: " + myStarsInMovies.get(starName));
            }
        }
        System.out.println("sum of movie title not fount: " + count + "\nmaybe a new movie, not sure add to db or not");


        starInsertStatement.executeBatch();
        tmpQueryStatement.executeBatch();
        statement.executeBatch();
        conn.commit();

        System.out.println("insert into stars_in_movies complete!!!");

        System.out.println("Finally, the total running is: "+ (double)(System.currentTimeMillis() - startTime)/1000 + " Seconds");


        result.close();
        statement.close();
        psInsertRecord.close();

    }
}
