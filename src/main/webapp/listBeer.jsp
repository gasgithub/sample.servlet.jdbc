<%@page import="java.sql.ResultSet"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>

<H1>Lista Piw</H1>
  <table border="1">
  <%
  ResultSet rs = (ResultSet)request.getAttribute("rs");
  while(rs.next()) {
	  %>
	  <tr><td><%=rs.getString(1) %></td><td><%=rs.getString(2) %></td><td><%=rs.getString(3) %></td><td><%=rs.getString(4) %></td></tr>
	  <%
  }
 
  %>
  
  
  </table>
  
  <a href="/JDBCApp">Powrot</a>