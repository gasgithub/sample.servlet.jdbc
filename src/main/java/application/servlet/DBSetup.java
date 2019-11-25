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
 * Servlet implementation class DBSetup
 */
@WebServlet("/DBSetup")
public class DBSetup extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
    @Resource(lookup = "jdbc/exampleDS")
    DataSource ds1;
    
    private final String CREATE_TABLE = "create table cities (name varchar(50) not null primary key, population int, county varchar(30))";
    private final String CREATE_TABLE_ORCL = "create table pilsisko (id_beer INTEGER NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1), " + 
    		"   beer_name varchar(32)," + 
    		"   alcohol numeric(4,2) default 5.5," + 
    		"   top_fermentation int default 0" + 
    		"   )";
    //private final String INSERT_PSTMT = "insert into cities values (?,?,?)";
    private final String INSERT_PSTMT = "insert into pilsisko (beer_name, alcohol, top_fermentation) values (?,?,?)";
    
    private final String DROP_PSTMT = "drop table pilsisko";

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		
		Statement stmt = null;
		PreparedStatement pstmt = null;
        Connection con = null;

        try {
            con = ds1.getConnection();
            
            
            String action = request.getParameter("action");
            if("insert".equals(action)) {
            	response.getWriter().append("Insterted");
            	System.out.println("Inserting...");
            	pstmt = con.prepareStatement(INSERT_PSTMT);
            	pstmt.setString(1, request.getParameter("beer_name"));
            	pstmt.setDouble(2, Double.parseDouble(request.getParameter("alcohol")));
            	pstmt.setInt(3, Integer.parseInt(request.getParameter("top_fermentation")));
            	pstmt.executeUpdate();
            	
            }
            else if("drop".equals(action)) {
            	response.getWriter().append("Droping table: ");
            	System.out.println("droping db...");
            	stmt = con.createStatement();
	            // create a table
	            stmt.executeUpdate(DROP_PSTMT);
            }
            else {
            	response.getWriter().append("Creating table: ").append(CREATE_TABLE);
            	System.out.println("creating db...");
            	stmt = con.createStatement();
	            // create a table
	            stmt.executeUpdate(CREATE_TABLE_ORCL);
	            stmt.executeUpdate("insert into pilsisko (beer_name, alcohol) values ('Pinta Atak Chmielu',6.0)"); 
	            stmt.executeUpdate("insert into pilsisko (beer_name, alcohol) values ('Brewmeister Snake Venom', 67.5)"); 
	            stmt.executeUpdate("insert into pilsisko (beer_name, alcohol, top_fermentation) values ('Pinta Hazy Morning', 4.4, 1)");
	            
	            
            }
            response.getWriter().append("<BR><a href=\"/JDBCApp\">Back </a>");
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if(stmt != null) stmt.close();
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
