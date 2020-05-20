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

// Declaring a WebServlet called SingleStarServlet, which maps to url "/api/single-star"
@WebServlet(name = "pageServlet", urlPatterns = "/api/page")
public class PageServlet extends HttpServlet {
    private static final long serialVersionUID = 2L;

    // Create a dataSource which registered in web.xml
    @Resource(name = "jdbc/moviedb")
    private DataSource dataSource;

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
     * response)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json"); // Response mime type

        // Retrieve parameters from url request.
        String prevOrNext = request.getParameter("page");
        // Output stream to STDOUT
        PrintWriter out = response.getWriter();

        try {
            // get session, rewrite values
            HttpSession session = request.getSession();
//                jsonArray.add(jsonObject);

            JsonObject movieParameter = (JsonObject) session.getAttribute("movieParameter");

            JsonElement pageElement = movieParameter.get("page");
            String previousPage = pageElement == null ? "0" : pageElement.getAsString();
            int previousPageInt = Integer.parseInt(previousPage);

            if("prev".equals(prevOrNext)){
                previousPageInt--;
            }else{
                previousPageInt++;
            }

            String newPage = String.valueOf(previousPageInt);

            movieParameter.addProperty("page", newPage);
            session.setAttribute("movieParameter", movieParameter);

            JsonObject responseJsonObject = new JsonObject();
            responseJsonObject.addProperty("status", "success");

            out.write(responseJsonObject.toString());
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
        //close it;
    }

}