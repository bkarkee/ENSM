<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>

<!DOCTYPE html>
<html>
	<head>
		<link rel="stylesheet" type="text/css" href="/css/register.css">
		<link rel="stylesheet" type="text/css" href="registerSuccess.css">
		<link rel="shortcut icon" type="image/x-icon" href="/images/favicon.png">
	</head>
	
	<body style="margin: 0;" onload="myFunction()">
	
	
	<div id="cycle"></div>
	
	<div style="display:none;">
		<form id="active" name = "active" method="POST" action="Servlet">
		    <input type="hidden" name="requestType" value="userActivate">
		    <input type="text" name="email" value="<%= request.getParameter("email")%>">
		    <input type="text" name="acode" value="<%= request.getParameter("acode")%>">
		</form>
		
		<script>
			function myFunction() {
				var myTimer;
			    myTimer = setTimeout(showPage, 3000);
			}
		
			function showPage() {
			  document.getElementById("cycle").style.display = "none";
			}
			
			document.getElementById("active").submit();
		</script>
	</div>
	
	</body>
</html>  