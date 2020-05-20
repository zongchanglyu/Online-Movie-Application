package main.java;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ParseMovie {
    // field
    List<Movies> myMovies;
    Document dom;
    private String tempDirectorName;
    private Set<String> allKindsOfGenres;

    // constructor
    public ParseMovie() {
        myMovies = new ArrayList<>();
        allKindsOfGenres = new HashSet<>();
    }

    // getter and setter
    public List<Movies> getMyMovies(){
        return myMovies;
    }

    public Set<String> getAllKindsOfGenres(){
        return allKindsOfGenres;
    }

    public void run(){

        //parse the xml file and get the dom object
        parseXmlFile();

        //get each employee element and create a Employee object
        parseDocument();

        //Iterate through the list and print the data
        printData();

    }

    private void parseXmlFile() {
        //get the factory
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

        try {

            //Using factory get an instance of document builder
            DocumentBuilder db = dbf.newDocumentBuilder();

            //parse using builder to get DOM representation of the XML file
            dom = db.parse("mains243.xml");

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
                            if(m != null)
                                myMovies.add(m);
                        }
                    }
                }
            }
        }
    }

    private Movies getMovie(Element film){
        try{
            String title = getTextValue(film, "t");
            String id = getTextValue(film, "fid");
            int year = getIntValue(film, "year");
            String director = tempDirectorName;
            if(title == null || id == null || director == null) return null;

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
        }catch (Exception e){
            return null;
        }
    }

    private String getTextValue(Element ele, String tagName) {
        String textVal = null;
        NodeList nl = ele.getElementsByTagName(tagName);
        if (nl != null && nl.getLength() > 0) {
            Element el = (Element) nl.item(0);
            textVal = el.getFirstChild().getNodeValue();
        }

        return textVal;
    }

    private int getIntValue(Element ele, String tagName) {
        //in production application you would catch the exception
        return Integer.parseInt(getTextValue(ele, tagName));
    }

    private void printData() {

        System.out.println("No of Movies '" + myMovies.size() + "'.");

//        Iterator<Movies> it = myMovies.iterator();
//        while (it.hasNext()) {
//            System.out.println(it.next().toString());
//        }
    }
}
