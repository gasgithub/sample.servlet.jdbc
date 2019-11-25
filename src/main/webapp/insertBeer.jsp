<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Dodaj piwo</title>
</head>
<body>

	<form action="/JDBCApp/DBSetup">
	<table border="1">
	<tr>
	 	<td>Nazwa</td><td><input type="text" name="beer_name"></td>
	</tr>
	<tr>
	 	<td>Alkohol</td><td><input type="text" name="alcohol"></td>
	 </tr>
	 <tr>
	 	<td>Fermentacja</td><td><input type="text" name="top_fermentation"></td>
	 </tr>
	 <tr>
	 	<td><input type="hidden" name="action" value="insert">
	 </tr>
	 <tr>
	 	<td></td><td><input type="submit" name="Dodaj" value="Dodaj"></td></tr>
	</table>
	</form>
	
</body>
</html>