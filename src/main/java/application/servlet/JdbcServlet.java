package application.servlet;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

/**
 * Servlet implementation class JdbcServlet
 */
@WebServlet("/DBQuery")
public class JdbcServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    
    private final String GET_ALL = "select * from pilsisko";
    private final String GET_BY_CITY = "select * from pilsisko where beer_name=?";

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    @Resource(lookup = "jdbc/exampleDS")
    DataSource ds1;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Statement stmt = null;
        Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		response.setContentType("text/html");

        try {
            con = ds1.getConnection();
            
            String name = request.getParameter("beer_name");
            if(name != null) {
            	pstmt = con.prepareStatement(GET_BY_CITY);
            	pstmt.setString(1, name);
            }
            else {
            	pstmt = con.prepareStatement(GET_ALL);
            }

            rs = pstmt.executeQuery();
            request.setAttribute("rs", rs);
            request.getRequestDispatcher("listBeer.jsp").include(request, response);
            //while(rs.next()) {
            // display the county information for the city.
            //response.getWriter().print("<BR>" + rs.getString(1) + " " + rs.getString(2) + " " + rs.getString(3)+ " " + rs.getString(4));
            //System.out.println("The county for myHomeCity is " + result.getString(1));
            //}
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if(rs != null) rs.close();
                if(pstmt != null) pstmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            try {
                con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

    }

}
