<!DOCTYPE html>
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">	
		<link rel="stylesheet" type="text/css" href="/css/index.css">
		<title>ENSM Sign-In</title>
	
		<link rel="icon" type="image/png" href="/images/favicon.png">
		
		<script src="/jquery/jquery-1.11.1.js"></script>
		<script src="/jquery/jquery.validate.js"></script>
		
		<script>
			window.onload = function() {
		        var tok = "";
		        tok=  "<%=request.getParameter("token")%>";
		        
		        if (tok == "err")  {
			        document.getElementById("credentials").style.display = "inline";
			        document.getElementById("credentials").innerHTML = "Wrong Username and/or password.";
		        }
		        else if(tok == "loggedout"){
		        	<%session.invalidate();%>
			        document.getElementById("credentials").style.display = "inline";
			        document.getElementById("credentials").innerHTML = "You have been logged out.";
		        }
		        else if(tok == "timeout"){
			        document.getElementById("credentials").style.display = "inline";
			        document.getElementById("credentials").innerHTML = "Your sesson has timed out.";
		        }
			}
		</script>
		
	</head>
	<body>
		<br>
		<h1>Emergency Notification System: Monroe</h1>
		<img style="align: center;" src="/images/logoibm.jpg">
		
		<div align="center">
			<form action="Servlet" method="post" accept-charset=utf-8>
				<input type="hidden" value="login" name="requestType">
				
				<h3>Log in with your IBM credentials:</h3>
				
				<p id= email>Email:  &nbsp;  &nbsp;  &nbsp;  &nbsp;  </p>
				<input type="text" name="Email"  tabindex="2"/><br><br>
				 
				<p>Password:   &nbsp;  &nbsp; &nbsp;  &nbsp; &nbsp;  &nbsp; &nbsp; &nbsp;  &nbsp; 
				<a id= "fpas" href="/retrievePassword" style="color: #53acef; font-size: 14px;" tabindex="-1">Forgot Password?</a></p>		
				<input maxlength=40  type="password" name="pass" tabindex="2"/>
	
				<br><label  id="credentials" style= "display: none; color:red; text-shadow: 2px 2px black;"  >Wrong email and/or password.</label>
				
				<br><br>
				<button id="signin" tabindex="-1">Sign In</button>
				<br><br>
				 
				<p>Not signed up?</p>
				<a href="Servlet?requestType=register" style="height:-70px; color: #53acef;" tabindex="-1">Register Here</a>
				
			
			</form>
		</div>
	</body>
</html>