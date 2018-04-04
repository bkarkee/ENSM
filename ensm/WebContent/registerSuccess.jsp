<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>

<!DOCTYPE html>
<html>
<head>
	<link rel="stylesheet" type="text/css" href="/css/register.css">
	<link rel="stylesheet" type="text/css" href="/css/registerSuccess.css">
	<link rel="shortcut icon" type="image/x-icon" href="/images/favicon.png">
</head>
<body onload="myFunction()" style="margin:0;">

<div id="cycle"></div>

<div style="display:none;" id="registered" class="animate-bottom">
<br><br><br><br>
    <h2>Success!</h2>
  <p>Thank you <%= request.getParameter("firstname")%> for signing up for IBM Monroe CIC Emergency SMS Notifications. </p>
  <p><strong>Please check your email to activate your account.</strong></p> 
  <p>You will be automatically redirected in 10 seconds or you can <A HREF="/login">Click Here</A> to login and update settings.</p> 

</div>
    
<script>
var myTimer;

function myFunction() {
    myTimer = setTimeout(showPage, 3000);
}

function showPage() {
  timer=setTimeout(function(){ window.location='/login';}, 7000)
  document.getElementById("cycle").style.display = "none";
  document.getElementById("registered").style.display = "block";
}
</script>
</body>
</html>