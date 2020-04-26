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

// Declaring a WebServlet called SingleStarServlet, which maps to url "/api/single-star"
@WebServlet(name = "AdvSearchServlet", urlPatterns = "/api/adv-search")
public class AdvSearchServlet extends HttpServlet {
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
        String title = request.getParameter("title");
        title = "%" + title + "%";
        String year = request.getParameter("year");
        if("".equals(year)) year = "%";
        else if(year.length() < 4) year = "11111";
        String director = request.getParameter("director");
        director = "%" + director + "%";
        String starName = request.getParameter("starName");
        starName = "%" + starName + "%";

        // Output stream to STDOUT
        PrintWriter out = response.getWriter();

        try {
            // get session, rewrite values
            HttpSession session = request.getSession();
//                jsonArray.add(jsonObject);

            JsonObject movieParameter = (JsonObject) session.getAttribute("movieParameter");
            if (movieParameter == null) {
                JsonObject newMovieParameter = new JsonObject();
                newMovieParameter.addProperty("status", "adv-search");
                newMovieParameter.addProperty("title", title);
                newMovieParameter.addProperty("year", year);
                newMovieParameter.addProperty("director", director);
                newMovieParameter.addProperty("starName", starName);
                newMovieParameter.addProperty("page", "0");
                session.setAttribute("movieParameter", newMovieParameter);
            }else{
                movieParameter.addProperty("status", "adv-search");
                movieParameter.addProperty("title", title);
                movieParameter.addProperty("year", year);
                movieParameter.addProperty("director", director);
                movieParameter.addProperty("starName", starName);
                movieParameter.addProperty("page", "0");
                session.setAttribute("movieParameter", movieParameter);
            }

            JsonObject responseJsonObject = new JsonObject();
            responseJsonObject.addProperty("status", "success");

            System.out.println("set attribute finished");

            out.write(responseJsonObject.toString());
            System.out.println(responseJsonObject.toString());
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