package main.java;

import java.sql.*;
import java.util.*;

public class XmlParse {

    public static void main(String[] args) throws ClassNotFoundException, SQLException, InstantiationException, IllegalAccessException {

        /**
         * parse movies into database
         * */

        ParseMovie pm = new ParseMovie();

        pm.run();

        List<Movies> myMovies = pm.getMyMovies();
        Set<String> allKindsOfGenres = pm.getAllKindsOfGenres();

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

        // oldMovies<director, tmpMovie<title, year>>
        Map<String, Map<String, Integer>> oldMovies = new HashMap<>();

        // avoid duplicate movieId
        Set<String> movieId = new HashSet<>();

        String query = "select * from movies";

        PreparedStatement statement = conn.prepareStatement(query);

        ResultSet result = statement.executeQuery();

        while (result.next()) {
            Map<String, Integer> tempMovie = oldMovies.getOrDefault(result.getString("director"), new HashMap<>());
            tempMovie.put(result.getString("title"), result.getInt("year"));
            oldMovies.put(result.getString("director"), tempMovie);

            movieId.add(result.getString("id"));
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
                continue;
            }

            while (movieId.contains(movie.getId()))
                movie.setId(movie.getId() + "1");
            movieId.add(movie.getId());

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

        /**
         * parse stars into database
         * */

        ParseStar ps = new ParseStar();

        ps.run();

        Set<Stars> myStars = ps.getMyStars();


        Map<String, Integer> oldStars = new HashMap<>();

        query = "select * from stars;";

        statement = conn.prepareStatement(query);

        result = statement.executeQuery();

        while(result.next()){
            try{
                oldStars.put(result.getString("name"), result.getInt("year"));
            }
            catch (Exception e){
                oldStars.put(result.getString("name"), 0);
            }
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
                System.out.println("duplicate star: " + star.getName());
                continue;
            }

            String id = prefix + (++number);

            statement.setString(1, id);
            statement.setString(2, star.getName());
            statement.setInt(3, star.getBirthYear());

            oldStars.put(star.getName(), star.getBirthYear());

            statement.addBatch();
        }

        statement.executeBatch();
        conn.commit();

        System.out.println("insert stars data into db finished!!!");

        result.close();
        statement.close();
        psInsertRecord.close();

    }
}
