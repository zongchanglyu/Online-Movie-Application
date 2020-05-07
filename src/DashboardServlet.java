import com.google.gson.JsonArray;
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


// Declaring a WebServlet called StarsServlet, which maps to url "/api/movies"
@WebServlet(name = "DashboardServlet", urlPatterns = "/api/dashboard")
public class DashboardServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    // Create a dataSource which registered in web.xml
    @Resource(name = "jdbc/moviedb")
    private DataSource dataSource;

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        response.setContentType("application/json"); // Response mime type
        // Output stream to STDOUT
        PrintWriter out = response.getWriter();

        try {
            HttpSession session = request.getSession();
            Employee employee = (Employee)session.getAttribute("employee");

            JsonObject jsonObject = new JsonObject();

            if(employee != null){
                jsonObject.addProperty("empFullname", employee.getFullname());
            }

            Connection database = dataSource.getConnection();
            JsonArray jsonArray = new JsonArray();
            String query = "select table_name, group_concat(concat(column_name, ':', column_type)) as attributes " +
                           "from information_schema.columns " +
                           "where table_schema = 'moviedb' and table_name != 'customers_backup' " +
                           "group by table_name;";
            Statement statement = database.createStatement();
            ResultSet rs = statement.executeQuery(query);
            while(rs.next()){
                String table_name = rs.getString("table_name");
                String attributes = rs.getString("attributes");

                JsonObject tmpJsonObject = new JsonObject();
                tmpJsonObject.addProperty("table_name", table_name);
                tmpJsonObject.addProperty("attributes", attributes);

                jsonArray.add(tmpJsonObject);
            }
            jsonObject.add("metadata", jsonArray);

            rs.close();
            statement.close();
            database.close();

            // write JSON string to output
            out.write(jsonObject.toString());
            // set response status to 200 (OK)
            response.setStatus(200);
        } catch (Exception e) {

            // write error message JSON object to output
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("errorMessage", e.getMessage());
            out.write(jsonObject.toString());

            // set reponse status to 500 (Internal Server Error)
            response.setStatus(500);

        }
        out.close();

    }
}