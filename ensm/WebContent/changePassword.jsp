<%@ page import="java.sql.*"%>
<%@ page import="javax.servlet.http.HttpServlet"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>

<!DOCTYPE html>
<html>

<head>
	<title>ENSM Change Password</title>
	
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
	<link rel="stylesheet" type="text/css" href="/css/index.css">
	<script src="/jquery/jquery-1.11.1.js"></script>
	<link rel="shortcut icon" type="image/x-icon" href="/images/favicon.png">
	
	<%
		try{
			response.setHeader("Cache-Control","no-cache");
			response.setHeader("Cache-Control","no-store");
			response.setHeader("Pragma","no-cache");
			response.setDateHeader ("Expires", 0);
			
			if(session.getAttribute("passToken")==null){
			  	response.sendRedirect("/login");
			}
			else{
				String token = (String) session.getAttribute("passToken");
				if(!token.equals("confirmed")){
					response.sendRedirect("/login");
				}
			}
		}
		catch(Exception ex){
			out.println(ex);
		}
	%>
</head>

<body>


	<div align="center">
		<h1 style="color: #ebebe0;">Emergency Notification System</h1>
		<img style="align: center" src="/images/logoibm.jpg">


		<form id="passwordForm" action="Servlet" method="post" accept-charset=utf-8>
			<input type="hidden" name="requestType" value="change"></input>
			<input type="hidden" id="hiddenEmail" name="hiddenEmail" value="" ></input>
			<input type="hidden" id="hiddenUID" name="hiddenUID" value="" ></input>

			<div>
				<h3>Enter new password for:  <label id="labelEmail">email@SPOT.com</label> </h3> 
			</div>

			<div id="passwordDiv">
				<p>New password:</p>
				<input maxlength=40 autocomplete="off"  type="password" name="newPassword"></input>
				<p>Confirm password:</p>
				<input maxlength=40 autocomplete="off"  type="password" name="cpass"></input><br><br>
				<button class="buttonopt">Update</button>
			</div>
		</form>

		<script>
        	document.getElementById("labelEmail").innerHTML = "<%= session.getAttribute("email") %>";
        	document.getElementById("hiddenEmail").value = "<%= session.getAttribute("email") %>";
		</script>

	</div>
</body> 
</html>




 