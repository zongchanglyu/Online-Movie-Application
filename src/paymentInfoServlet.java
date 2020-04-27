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
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

// Declaring a WebServlet called SingleStarServlet, which maps to url "/api/single-star"
@WebServlet(name = "paymentInfoServlet", urlPatterns = "/api/payment-info")
public class paymentInfoServlet extends HttpServlet {
    private static final long serialVersionUID = 2L;

    // Create a dataSource which registered in web.xml
    @Resource(name = "jdbc/moviedb")
    private DataSource dataSource;

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
     * response)
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json"); // Response mime type

        String firstName = request.getParameter("firstName");
        String lastName = request.getParameter("lastName");
        String cardNumber = request.getParameter("cardNumber");
        String date = request.getParameter("date");

        // Output stream to STDOUT
        PrintWriter out = response.getWriter();

        try {
                // Get a connection from dataSource
                Connection dbcon = dataSource.getConnection();

                String query = "select creditcards.* from creditcards where creditcards.id = ?;";

                PreparedStatement movieStatement = dbcon.prepareStatement(query);

                movieStatement.setString(1, cardNumber);

                // Perform the query
                ResultSet rs = movieStatement.executeQuery();


                JsonObject responseJsonObject = new JsonObject();

                if(!rs.next()){
                    responseJsonObject.addProperty("status", "fail");
                }else{
                    String dbfirstName = rs.getString("firstName");
                    String dblastName = rs.getString("lastName");
                    String dbdate = rs.getString("expiration");

                    if(firstName.equals(dbfirstName) && lastName.equals(dblastName) && date.equals(dbdate)){
                        responseJsonObject.addProperty("status", "success");

                        HttpSession session = request.getSession();
                        Customer user = (Customer) session.getAttribute("user");
                        HashMap<String, JsonObject> cardItem = (HashMap<String, JsonObject>) session.getAttribute("cardItem");
                        SimpleDateFormat sdf = new SimpleDateFormat();
                        sdf.applyPattern("yyyy-MM-dd");
                        Date d = new Date();
                        String nowDate = sdf.format((d));

                        for(JsonObject obj : cardItem.values()){
                            String insertSql = "insert into sales(customerId, movieId, saleDate) values ( ?, ?, ? );";
                            PreparedStatement insertStatement = dbcon.prepareStatement(insertSql);

                            insertStatement.setString(1, String.valueOf(user.getId()));
                            insertStatement.setString(2, obj.get("movie_id").getAsString());
                            insertStatement.setString(3, nowDate);

                            int insertRS = insertStatement.executeUpdate();
                            System.out.println(insertRS);

                            insertStatement.close();
                        }

                    }
                    else{
                        responseJsonObject.addProperty("status", "fail");
                    }
                }


                // write JSON string to output
                out.write(responseJsonObject.toString());
                // set response status to 200 (OK)
                response.setStatus(200);

                rs.close();
                movieStatement.close();
                dbcon.close();

        } catch (Exception e) {

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

}
