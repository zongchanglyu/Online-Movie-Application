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
import java.util.HashMap;

// Declaring a WebServlet called SingleStarServlet, which maps to url "/api/single-star"
@WebServlet(name = "UpdateCartItemQuantity", urlPatterns = "/api/updateCartItemQuantity")
public class UpdateCartItemQuantity extends HttpServlet {
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

        String movieId = request.getParameter("id");
        String operate = request.getParameter("operate");

        // Output stream to STDOUT
        PrintWriter out = response.getWriter();

        try {

                HttpSession session = request.getSession();
                HashMap<String, JsonObject> cartItem = (HashMap<String, JsonObject>) session.getAttribute("cartItem");

                String quantity = cartItem.get(movieId).get("quantity").getAsString();
                if("plus".equals(operate)) {
                    quantity = String.valueOf(Integer.parseInt(quantity) + 1);
                }else{
                    quantity = "1".equals(quantity) ? "1" : String.valueOf(Integer.parseInt(quantity) - 1);
                }

                cartItem.get(movieId).addProperty("quantity", quantity);

                JsonArray jsonArray = new JsonArray();
                for (JsonObject i : cartItem.values()) {
                    jsonArray.add(i);
                }

                // write JSON string to output
                out.write(jsonArray.toString());
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
