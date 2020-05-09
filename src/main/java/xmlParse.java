package main.java;

import com.google.gson.JsonObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.sql.DataSource;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.sql.*;
import java.util.*;


public class xmlParse {

    List<Movies> myMovies;
    Document dom;

    private String tempDirectorName;
    private HashSet<String> allKindsOfGenres;

    public xmlParse() {
        //create a list to hold the employee objects
        myMovies = new ArrayList<>();

        allKindsOfGenres = new HashSet<String>();
    }

    public void runExample() throws SQLException, IllegalAccessException, ClassNotFoundException, InstantiationException {

        //parse the xml file and get the dom object
        parseXmlFile();

        //get each employee element and create a Employee object
        parseDocument();

        //Iterate through the list and print the data
        printData();

        System.out.println("begin to insertData.....");
        insertData();
    }


    private void parseXmlFile() {
        //get the factory
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

        try {

            //Using factory get an instance of document builder
            DocumentBuilder db = dbf.newDocumentBuilder();

            //parse using builder to get DOM representation of the XML file
            dom = db.parse("mains243test.xml");

        } catch (ParserConfigurationException pce) {
            pce.printStackTrace();
        } catch (SAXException se) {
            se.printStackTrace();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    private void parseDocument() {
        //get the root elememt
        Element docEle = dom.getDocumentElement();

        //get a nodelist of <employee> elements
        NodeList nl = docEle.getElementsByTagName("directorfilms");
        if (nl != null && nl.getLength() > 0) {
            for (int i = 0; i < nl.getLength(); i++) {

                //get the directorfilms element
                Element el = (Element) nl.item(i);

                NodeList dir = el.getElementsByTagName("director");
                if (dir != null && dir.getLength() > 0) {
                    Element director = (Element) dir.item(0);
                    NodeList dirn = director.getElementsByTagName("dirname");
                    if (dirn != null && dirn.getLength() > 0) {
                        Element dirname = (Element) dirn.item(0);
                        tempDirectorName = dirname.getFirstChild().getNodeValue();
                    }
                }

                NodeList films = el.getElementsByTagName("films");
                if (films != null && films.getLength() > 0) {
                    Element fs = (Element) films.item(0);

                    NodeList film = fs.getElementsByTagName("film");
                    if (film != null && film.getLength() > 0) {
                        for (int j = 0; j < film.getLength(); j++) {

                            //get the film element
                            Element Film = (Element) film.item(j);

                            //get the movie object
                            Movies m = getMovie(Film);

                            //add it to list
                            myMovies.add(m);
                        }
                    }

                }

            }
        }
    }



    /**
     * I take an film element and read the values in, create
     * an Movies object and return it
     * 
     * @param film
     * @return
     */
        private Movies getMovie(Element film){
            String title = getTextValue(film, "t");
            String id = getTextValue(film, "fid");
            int year = getIntValue(film, "year");
            String director = tempDirectorName;

            List<String> tempGenres = new ArrayList<String>();
            NodeList cats = film.getElementsByTagName("cats");
            if(cats != null && cats.getLength()>0){
                Element Cats = (Element) cats.item(0);
                NodeList cat = Cats.getElementsByTagName("cat");
                if(cat != null && cat.getLength()>0){
                    for (int i = 0; i < cat.getLength(); i++){
                        Element Cat = (Element) cat.item(i);
                        tempGenres.add(Cat.getFirstChild().getNodeValue());
                        allKindsOfGenres.add(Cat.getFirstChild().getNodeValue());
                    }

                }
            }

            List<String> genres = tempGenres;

            Movies m = new Movies(title, id, year, director, genres);

            return m;

        }

    /**
     * I take a xml element and the tag name, look for the tag and get
     * the text content
     * i.e for <employee><name>John</name></employee> xml snippet if
     * the Element points to employee node and tagName is name I will return John
     * 
     * @param ele
     * @param tagName
     * @return
     */
    private String getTextValue(Element ele, String tagName) {
        String textVal = null;
        NodeList nl = ele.getElementsByTagName(tagName);
        if (nl != null && nl.getLength() > 0) {
            Element el = (Element) nl.item(0);
            textVal = el.getFirstChild().getNodeValue();
        }

        return textVal;
    }

    /**
     * Calls getTextValue and returns a int value
     * 
     * @param ele
     * @param tagName
     * @return
     */
    private int getIntValue(Element ele, String tagName) {
        //in production application you would catch the exception
        return Integer.parseInt(getTextValue(ele, tagName));
    }

    /**
     * Iterate through the list and print the
     * content to console
     */
    private void printData() {

        System.out.println("No of Movies '" + myMovies.size() + "'.");

        Iterator<Movies> it = myMovies.iterator();
        while (it.hasNext()) {
            System.out.println(it.next().toString());
        }
    }


        private void insertData() throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {

            System.out.println("debugging connection....");

            Connection conn = null;
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            String jdbcURL="jdbc:mysql://localhost:3306/moviedb";


            try {
                conn = DriverManager.getConnection(jdbcURL,"mytestuser", "mypassword");
                System.out.println("connect to db");
            }
            catch (SQLException e) {
                e.printStackTrace();
            }

            HashMap<String, HashMap<String, Integer>> oldMovies = new HashMap<>();

            String query = "select * from movies";

            PreparedStatement statement = conn.prepareStatement(query);

            ResultSet result = statement.executeQuery();

            while(result.next()){
                HashMap<String, Integer> tempMovie = oldMovies.getOrDefault(result.getString("director"), new HashMap<>());
                tempMovie.put(result.getString("title"), result.getInt("year"));
                oldMovies.put(result.getString("director"), tempMovie);
            }

            System.out.println("old movies size is: "+oldMovies.size());

            conn.setAutoCommit(false);

            query = "insert into genres (name) select ? from DUAL where NOT EXISTS(select name from genres where name = ?);";

            statement = conn.prepareStatement(query);

            for(String genre : allKindsOfGenres){
                statement.setString(1,genre);
                statement.setString(2, genre);
                statement.addBatch();
            }

            statement.executeBatch();
            conn.commit();

            System.out.println("execute genres sql finished!!!");


//insert movies
            conn.setAutoCommit(false);

            PreparedStatement psInsertRecord=null;

            String insertSql = "insert into movies(id, title, year, director) values ( ?, ?, ?, ? );";

            String gimSql = "insert into genres_in_movies (genreId, movieId) values ((select id from genres where name = ?), ?)";

            PreparedStatement gimStatement = conn.prepareStatement(gimSql);

            for(int i = 0; i < myMovies.size(); i++){
                Movies movie = myMovies.get(i);

                if (oldMovies.containsKey(movie.getDirector()) && oldMovies.get(movie.getDirector()).containsKey(movie.getTitle()) &&
                        oldMovies.get(movie.getDirector()).get(movie.getTitle()).equals(movie.getYear()) || movie.getId()==null) {
                    continue;
                }

//              PreparedStatement insertStatement = dbcon.prepareStatement(insertSql);

                psInsertRecord=conn.prepareStatement(insertSql);

                psInsertRecord.setString(1, movie.getId());
                psInsertRecord.setString(2, movie.getTitle());
                psInsertRecord.setInt(3, movie.getYear());
                psInsertRecord.setString(4, movie.getDirector());

                psInsertRecord.executeUpdate();
//                System.out.println("prepare stat finished");
                HashMap<String, Integer> tempMovie = oldMovies.getOrDefault(result.getString("director"), new HashMap<>());
                tempMovie.put(result.getString("title"), result.getInt("year"));
                oldMovies.put(result.getString("director"), tempMovie);

                for(String genre : movie.getGenres()){
                    gimStatement.setString(1, genre);
                    gimStatement.setString(2, movie.getId());
                    gimStatement.executeUpdate();
                }

//                psInsertRecord.addBatch();
//                gimStatement.addBatch();


            }
//            psInsertRecord.executeBatch();
//            gimStatement.executeBatch();
//            conn.commit();

//            psInsertRecord.executeUpdate();
            result.close();
            statement.close();
            psInsertRecord.close();

            System.out.println("insert movies and genres_in_movies finished!");

        }



    public static void main(String[] args) throws SQLException, IllegalAccessException, InstantiationException, ClassNotFoundException {
        //create an instance
        xmlParse dpe = new xmlParse();

        //call run example
        dpe.runExample();

    }

}

