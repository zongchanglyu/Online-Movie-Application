package main.java;

import java.sql.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class insertData {


    public static void main(String[] args) throws ClassNotFoundException, IllegalAccessException, InstantiationException, SQLException {

        moviesXmlParse moviesParse = new moviesXmlParse();

        moviesParse.run();

        List<Movies> myMovies = moviesParse.getMyMovies();
        HashSet<String> allKindsOfGenres = moviesParse.getAllKindsOfGenres();

        System.out.println("mymovies size: "+myMovies.size());

        starsXmlParse starParse = new starsXmlParse();

        starParse.run();

        HashSet<Stars> myStars = starParse.getMyStars();

        System.out.println("mystars size: "+myStars.size());



//==============================================================
//        Start to insert data:
//==============================================================

        System.out.println("debugging connection....");

        Connection conn = null;
        Class.forName("com.mysql.jdbc.Driver").newInstance();
        String jdbcURL = "jdbc:mysql://localhost:3306/moviedb";


        try {
            conn = DriverManager.getConnection(jdbcURL, "mytestuser", "mypassword");
            System.out.println("connect to db");
        } catch (
                SQLException e) {
            e.printStackTrace();
        }

        // oldMovies<director, tmpMovie<title, year>>
        HashMap<String, HashMap<String, Integer>> oldMovies = new HashMap<>();

        // avoid duplicate movieId
        Set<String> movieId = new HashSet<>();

        String query = "select * from movies";

        PreparedStatement statement = conn.prepareStatement(query);

        ResultSet result = statement.executeQuery();

        while (result.next()) {
            HashMap<String, Integer> tempMovie = oldMovies.getOrDefault(result.getString("director"), new HashMap<>());
            tempMovie.put(result.getString("title"), result.getInt("year"));
            oldMovies.put(result.getString("director"), tempMovie);

            movieId.add(result.getString("id"));
        }

        // this should be the size of old directors
        System.out.println("old movies size is: " + oldMovies.size());

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

        System.out.println("execute genres sql finished!!!");

//================================================================
//insert movies data:
//================================================================


        conn.setAutoCommit(false);

        PreparedStatement psInsertRecord = null;

        String insertSql = "insert into movies(id, title, year, director) values ( ?, ?, ?, ? );";

        String gimSql = "insert into genres_in_movies (genreId, movieId) values ((select id from genres where name = ?), ?)";

        String ratingSql = "insert into ratings (movieId) values ( ? );";

        psInsertRecord = conn.prepareStatement(insertSql);
        PreparedStatement gimStatement = conn.prepareStatement(gimSql);
        PreparedStatement ratingStatement = conn.prepareStatement(ratingSql);

        for (Movies movie : myMovies) {
//                if (oldMovies.containsKey(movie.getDirector()) && oldMovies.get(movie.getDirector()).containsKey(movie.getTitle()) &&
//                        oldMovies.get(movie.getDirector()).get(movie.getTitle()).equals(movie.getYear()) || movie.getId() == null) {
//                    continue;
//                }
            if (oldMovies.containsKey(movie.getDirector()) && oldMovies.get(movie.getDirector()).containsKey(movie.getTitle()) || movie.getId() == null) {
                continue;
            }

//              PreparedStatement insertStatement = dbcon.prepareStatement(insertSql);


            while (movieId.contains(movie.getId()))
                movie.setId(movie.getId() + "1");
            movieId.add(movie.getId());

            psInsertRecord.setString(1, movie.getId());
            psInsertRecord.setString(2, movie.getTitle());
            psInsertRecord.setInt(3, movie.getYear());
            psInsertRecord.setString(4, movie.getDirector());

            ratingStatement.setString(1, movie.getId());

//                psInsertRecord.executeUpdate();
//            System.out.println("prepare stat finished");
            HashMap<String, Integer> tempMovie = oldMovies.getOrDefault(movie.getDirector(), new HashMap<String, Integer>());
            tempMovie.put(movie.getTitle(), movie.getYear());
            oldMovies.put(movie.getDirector(), tempMovie);
            if (movie.getGenres() != null && movie.getGenres().size() > 0) {
                for (String genre : movie.getGenres()) {
                    if (genre == null) continue;
                    gimStatement.setString(1, genre);
                    gimStatement.setString(2, movie.getId());
                    //                    gimStatement.executeUpdate();
                    gimStatement.addBatch();
                }
            }

            psInsertRecord.addBatch();

            ratingStatement.addBatch();
//                System.out.println("debug000");
        }

        psInsertRecord.executeBatch();
        gimStatement.executeBatch();

        ratingStatement.executeBatch();
        conn.commit();

        System.out.println("debug111");
//            psInsertRecord.executeUpdate();
        System.out.println("insert movies and genres_in_movies finished!");


//=================================================================
//        insert stars data:
//=================================================================


        HashMap<String, Integer> oldStars = new HashMap<>();

        query = "select * from stars";

        statement = conn.prepareStatement(query);

        result = statement.executeQuery();

        while(result.next()){
            try{
                oldStars.put(result.getString("name"), result.getInt("birthYear"));
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
        int number = Integer.parseInt(starId.substring(2, starId.length()));

        query = "insert into stars (id, name, birthYear) values (?,?,?) ";
        statement = conn.prepareStatement(query);

        System.out.println("debug333......");

        System.out.println("mystars size is: "+myStars.size());

        for(Stars star : myStars){

//            System.out.println("debug222......");

            if(oldStars.containsKey(star.getName()) && oldStars.get(star.getName())==star.getBirthYear()){
                continue;
            }

            String id = prefix + (++number);

            statement.setString(1, id);
            statement.setString(2, star.getName());
            statement.setInt(3, star.getBirthYear());

            oldStars.put(star.getName(), star.getBirthYear());

            statement.addBatch();

//            System.out.println("add 1 star!");
        }
        statement.executeBatch();
        conn.commit();

        System.out.println("insert stars data into db finished!!!");




        result.close();
        statement.close();
        psInsertRecord.close();

    }
}
