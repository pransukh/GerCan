package com.fis.demo;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

/**
 * Servlet implementation class ServletDataSource
 */
@WebServlet("/ServletDataSource")
public class ServletDataSource extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		System.out.println("In Get MEthod of ServletDataSource");

		
		Context ctx = null;
		Connection con = null;
		Statement stmt = null;
		ResultSet rs = null;
		try{
			
			ctx = new InitialContext();
			DataSource ds = (DataSource) ctx.lookup("java:/comp/env/jdbc/MyDB_Match_In_ContextDotXmlOfServer"+request.getParameter("DataSource"));
			

			
			
			Connection conn = ds.getConnection();
			con = ds.getConnection();
			stmt = con.createStatement();
			if(request.getParameter("DataSource")!=null && 
					request.getParameter("DataSource").equalsIgnoreCase("_PRACTICE"))
			{
				rs = stmt.executeQuery("select city from login");
				
			}
			else
			{
				rs = stmt.executeQuery("select ename from empinfo");
				
			}
			
			
			PrintWriter out = response.getWriter();
			SimpleDateFormat format = new SimpleDateFormat("dd-MMM-yyyy hh:mm:ss a");
			
            response.setContentType("text/html");
            out.print("<html><body><h2>Cities :- "+format.format(new Date())+"</h2>");
            out.print("<table border=\"1\" cellspacing=10 cellpadding=5>");
            
            out.print("<th>City Name</th>");
            
            while(rs.next())
            {
                out.print("<tr>");
               
                out.print("<td>" + rs.getString(1) + "</td>");
                out.print("</tr>");
            }
            out.print("</table></body><br/>");
            
            //lets print some DB information
            out.print("<h3>Database Details</h3>");
            out.print("Database Product: "+con.getMetaData().getDatabaseProductName()+"<br/>");
            out.print("Database Driver: "+con.getMetaData().getDriverName());
            out.print("</html>");
            System.out.println("------- DONE -------");
		}catch(NamingException e){
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			try {
				rs.close();
				stmt.close();
				con.close();
				ctx.close();
			} catch (SQLException e) {
				System.out.println("Exception in closing DB resources");
			} catch (NamingException e) {
				System.out.println("Exception in closing Context");
			}
			
		}
	
	}

	

}
