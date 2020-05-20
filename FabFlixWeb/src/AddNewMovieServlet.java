import com.google.gson.JsonObject;

import javax.annotation.Resource;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

@WebServlet(name = "AddNewMovie", urlPatterns = "/api/add-new-movie")
public class AddNewMovieServlet extends HttpServlet {
    private static final long serialVersionUID = 2L;

    // Create a dataSource which registered in web.xml
    @Resource(name = "jdbc/moviedb")
    private DataSource dataSource;

    /**
     * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {

        PrintWriter out = response.getWriter();

        String movieTitle = request.getParameter("movieTitle");
        String movieYear = request.getParameter("movieYear");
        String movieDirector = request.getParameter("movieDirector");
        String starName = request.getParameter("starName");
        String genreName = request.getParameter("genreName");
        String movieMessage = "";
        String starMessage = "";
        String genreMessage = "";

        try {
            // Get a connection from dataSource
            Connection dbcon = dataSource.getConnection();
            String query = "call add_movie(?, ?, ?, ?, ?, ?, ?, ?);";
            CallableStatement cs = dbcon.prepareCall(query);
            cs.setString(1, movieTitle);
            cs.setString(2, movieYear);
            cs.setString(3, movieDirector);
            cs.setString(4, starName);
            cs.setString(5, genreName);
            cs.setString(6, movieMessage);
            cs.setString(7, starMessage);
            cs.setString(8, genreMessage);
            cs.executeQuery();

            movieMessage = cs.getString("movieMessage");
            starMessage = cs.getString("starMessage");
            genreMessage = cs.getString("genreMessage");

            JsonObject responseJsonObject = new JsonObject();
            responseJsonObject.addProperty("status", "success");
            responseJsonObject.addProperty("movieMessage", movieMessage);
            responseJsonObject.addProperty("starMessage", starMessage);
            responseJsonObject.addProperty("genreMessage", genreMessage);
            out.write(responseJsonObject.toString());

            cs.close();
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
