import com.google.gson.JsonObject;
import org.jasypt.util.password.StrongPasswordEncryptor;

import javax.annotation.Resource;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

@WebServlet(name = "AddNewStar", urlPatterns = "/api/add-new-star")
public class AddNewStarServlet extends HttpServlet {
    private static final long serialVersionUID = 2L;

    // Create a dataSource which registered in web.xml
    @Resource(name = "jdbc/moviedb")
    private DataSource dataSource;

    /**
     * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {

        PrintWriter out = response.getWriter();

        String starName = request.getParameter("starName");
        String birthYear = request.getParameter("birthYear");
        String starId;

        try {
            // Get a connection from dataSource
            Connection dbcon = dataSource.getConnection();
            String query = "select max(id) as starMaxId from stars;";
            PreparedStatement statement = dbcon.prepareStatement(query);
            ResultSet rs = statement.executeQuery();
            rs.next();
            String starMaxId = rs.getString("starMaxId");
            String starIdPrefix = starMaxId.substring(0, 2);
            String starIdNum = starMaxId.substring(2);
            String starNextIdNum = String.valueOf(Integer.parseInt(starIdNum) + 1);
            starId = starIdPrefix + starNextIdNum;
            statement.close();
            rs.close();

            if("".equals(birthYear)){
                String insertQuery = "insert into stars(id, name) values(?, ?);";
                PreparedStatement insertStatement = dbcon.prepareStatement(insertQuery);
                insertStatement.setString(1, starId);
                insertStatement.setString(2, starName);
                insertStatement.executeUpdate();
                insertStatement.close();
            }else{
                String insertQuery = "insert into stars(id, name, birthYear) values(?, ?, ?);";
                PreparedStatement insertStatement = dbcon.prepareStatement(insertQuery);
                insertStatement.setString(1, starId);
                insertStatement.setString(2, starName);
                insertStatement.setInt(3, Integer.parseInt(birthYear));
                insertStatement.executeUpdate();
                insertStatement.close();
            }

            JsonObject responseJsonObject = new JsonObject();
            responseJsonObject.addProperty("status", "success");
            responseJsonObject.addProperty("newStarId", starId);
            out.write(responseJsonObject.toString());

            dbcon.close();
            out.close();
        } catch (Exception e) {

            // write error message JSON object to output
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("errorMessage", e.getMessage());
            out.write(jsonObject.toString());

            // set reponse status to 500 (Internal Server Error)
            response.setStatus(500);

            out.close();
        }
    }
}
