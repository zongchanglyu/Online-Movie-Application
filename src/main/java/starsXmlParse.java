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
import java.util.Iterator;
import java.util.List;


public class starsXmlParse {
//    List<Stars> myStars;
    HashSet<Stars> myStars;
    Document dom;

    public starsXmlParse() {
        //create a list to hold the employee objects
//        myStars = new ArrayList<>();
        myStars = new HashSet<>();

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
            dom = db.parse("actors63.xml");

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
        NodeList nl = docEle.getElementsByTagName("actor");
        if (nl != null && nl.getLength() > 0){
            for (int i = 0; i < nl.getLength(); i++){
                Element actor = (Element) nl.item(i);

                Stars star = getStar(actor);

                if(star!=null){
                    myStars.add(star);
                }

            }
        }
    }

    
    private Stars getStar(Element star){
        try{
            String name = getTextValue(star, "stagename");
            int birthYear = getIntValue(star, "dob");

            Stars s = new Stars(name, birthYear);
            return s;
        }catch (Exception e){
            return null;
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

        System.out.println("No of Stars '" + myStars.size() + "'.");

        Iterator<Stars> it = myStars.iterator();
        while (it.hasNext()) {
            System.out.println(it.next().toString());
        }

    }

    public HashSet<Stars> getMyStars(){
        return myStars;
    }



    public void run() throws ClassNotFoundException, SQLException, InstantiationException, IllegalAccessException {
        //create an instance
        starsXmlParse dpe = new starsXmlParse();

        //call run example
        dpe.runExample();
    }



    public static void main(String[] args) throws SQLException, IllegalAccessException, InstantiationException, ClassNotFoundException {
        //create an instance
        starsXmlParse dpe = new starsXmlParse();

        //call run example
        dpe.runExample();

    }

}
