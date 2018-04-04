<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>

<!DOCTYPE html>
<html>
	<head>
		<link rel="stylesheet" type="text/css" href="userProfile.css">
		<link rel="shortcut icon" type="image/x-icon" href="/images/favicon.png">
	</head>
	
	<body id="result_body">
	
		<div style="margin-top: 20%"></div>
		
		<form id = frm_Result >
			<h1 align = 'center'><font color="Red" style="text-shadow: 2px 2px 2px #000000;">Message sent successfully .</font></h1>
			<h1 align = 'center'><font color="white">Please check your email to recover your password</font></h1>
			<p align="center"><a href="/login" style="color:white;">Go-Back To Home Page</a></p>
		</form>
		
		<div style="margin-bottom: 20%"></div>
	
		<script type="text/javascript">
			setTimeout(function() {
				window.location = '/login';
			}, 7000);
		</script>
		
	</body>
</html>