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
import java.util.*;

public class starsInMoviesXmlParse {

    static HashMap<String, String> myStarsInMovies;
    Document dom;

//    private String tempDirectorName;
//    private HashSet<String> allKindsOfGenres;

    public starsInMoviesXmlParse() {
        //create a list to hold the employee objects
        myStarsInMovies = new HashMap<>();

    }

    public void runExample() throws SQLException, IllegalAccessException, ClassNotFoundException, InstantiationException {
        //parse the xml file and get the dom object
        parseXmlFile();

        //get each employee element and create a Employee object
        parseDocument();

        //Iterate through the list and print the data
        printData();

        System.out.println("begin to insertData.....");
//        insertData();
    }


    private void parseXmlFile() {
        //get the factory
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

        try {

            //Using factory get an instance of document builder
            DocumentBuilder db = dbf.newDocumentBuilder();

            //parse using builder to get DOM representation of the XML file
            dom = db.parse("casts124.xml");

        } catch (ParserConfigurationException pce) {
            pce.printStackTrace();
        } catch (SAXException se) {
            se.printStackTrace();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }


    private void parseDocument(){
        //get the root elememt
        Element docEle = dom.getDocumentElement();

        //get a nodelist of <employee> elements
        NodeList nl = docEle.getElementsByTagName("dirfilms");
        if (nl != null && nl.getLength() > 0){
            for (int i = 0; i < nl.getLength(); i++){
                Element el = (Element) nl.item(i);
                NodeList filmc = el.getElementsByTagName("filmc");
                if (filmc != null && filmc.getLength() > 0){
                    for (int j = 0; j < filmc.getLength(); j++){
                        Element f = (Element) filmc.item(j);

                        NodeList m = f.getElementsByTagName("m");

                        if (m != null && m.getLength() > 0){
                            for (int k = 0; k < m.getLength(); k++){
                                Element M = (Element) m.item(k);

                                String movieId = getTextValue(M, "f");
                                String stageName = getTextValue(M, "a");

                                if(stageName==null || movieId==null) continue;

                                myStarsInMovies.put(stageName, movieId);
//                                System.out.println("debug00000000");
                            }

                        }
                    }
                }
            }
        }
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
            if(el.getFirstChild()!=null){
                textVal = el.getFirstChild().getNodeValue();
            }
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

        System.out.println("numbers of data '" + myStarsInMovies.size() + "'.");

//       for(String s: myStarsInMovies.keySet()){
//           System.out.println("star is: "+s);
////           for(String id: myStarsInMovies.get(s)){
////               System.out.println("Star is: "+s+", movieId is: "+id);
////
////           }
//       }
    }


    public HashMap<String, String> getMyStarsInMovies(){
        return myStarsInMovies;
    }

    public void run() throws ClassNotFoundException, SQLException, InstantiationException, IllegalAccessException {
        //create an instance
        starsInMoviesXmlParse dpe = new starsInMoviesXmlParse();

        //call run example
        dpe.runExample();
    }




    public static void main(String[] args) throws SQLException, IllegalAccessException, InstantiationException, ClassNotFoundException {
        //create an instance
        starsInMoviesXmlParse dpe = new starsInMoviesXmlParse();

        //call run example
        dpe.runExample();

    }
}
