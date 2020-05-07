package main.java;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class xmlParse {

    List<Movies> myMovies;
    Document dom;

    private String tempDirectorName;

    public xmlParse() {
        //create a list to hold the employee objects
        myMovies = new ArrayList<>();
    }

    public void runExample() {

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

    public static void main(String[] args) {
        //create an instance
        xmlParse dpe = new xmlParse();

        //call run example
        dpe.runExample();
    }

}

