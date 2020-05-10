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
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class ParseStar {

    // field
    Set<Stars> myStars;
    Document dom;

    // constructor
    public ParseStar() {
        myStars = new HashSet<>();
    }

    // getter and setter
    public Set<Stars> getMyStars(){
        return myStars;
    }

    // methods
    public void run() throws SQLException, IllegalAccessException, ClassNotFoundException, InstantiationException {

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

        System.out.println("No of Stars '" + myStars.size() + "'.");

//        Iterator<Stars> it = myStars.iterator();
//        while (it.hasNext()) {
//            System.out.println(it.next().toString());
//        }

    }
}
