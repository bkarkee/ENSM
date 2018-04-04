<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1" %>
<%@ page import="wasdev.sample.model.Carrier"%> 
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<html>
	<head>
	<link rel="stylesheet" type="text/css" href="/css/register.css">
	<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
	
	
	<link rel="stylesheet" href="http://kendo.cdn.telerik.com/2016.2.504/styles/kendo.common.min.css"/>
	<link rel="stylesheet" href="http://kendo.cdn.telerik.com/2016.2.504/styles/kendo.rtl.min.css"/>
	<link rel="stylesheet" href="http://kendo.cdn.telerik.com/2016.2.504/styles/kendo.silver.min.css"/>
	<link rel="stylesheet" href="http://kendo.cdn.telerik.com/2016.2.504/styles/kendo.mobile.all.min.css"/>
	
	<script src="/jquery/jquery-1.11.1.js"></script>
	<script src="/jquery/jquery.validateRegistration.js"></script>
	<script src="http://kendo.cdn.telerik.com/2016.2.504/js/kendo.all.min.js"></script>
	<link rel="shortcut icon" type="image/x-icon" href="/images/favicon.png">
	
	<title>ENSM Registration</title>
	
	</head>
	<body onload="askForCarriers(), checkParameters(), setPhone()"> 


		<form id="signUpForm" action="Servlet" method="post" accept-charset=utf-8>
			<input type="hidden" name="requestType" value="signup">
			
			
			<div style="margin-left: auto; margin-right: auto;">
				<h1 align="center" style="color: #ebebe0;">IBM Monroe Emergency Notification System: Registration Page</h1>
					
				<div style="text-align: center;">
				<p id=h5>Already signed up for Notifications? Click the logo below to login and update your account.</p>
				<a id="indexReturn" href="/login" tabindex="-1">
					<img src="/images/logoibm.jpg">
				</a> <br><br>
				</div>
			</div>
				
			<div id="signUpFields">
				<label id="notification"></label><br/>
				
				<div class="signUpFieldHolder">
					<label class="fieldTitle" >First Name: </label> <br>
					<input  type="text" name="firstname" tabindex="1"/>
				</div>
				
				<div class="signUpFieldHolder">
					<label class="fieldTitle" >Last Name: </label> <br>
					<input  type="text" name="lastname" tabindex="2"/>
				</div>
				
				<div class="signUpFieldHolder">
					<label class="fieldTitle" >Phone Number </label> <br>
					<input type="text" id="phone" name="phone" maxlength="10" tabindex="3" style="width: 99%"/>
				</div>
				
				<div class="signUpFieldHolder">
					<label class="fieldTitle" >Select A Carrier </label> <br>
					<select  id="carrier" name="carrier" tabindex="4"></select>
				</div>
				
				<div class="signUpFieldHolder">
					<label class="fieldTitle" >Email Address </label> <br>
					<input id = "email" type="text" name="email" tabindex="5" /> 
				</div>
				
				<div class="signUpFieldHolder">
					<label class="fieldTitle" >Confirm Email Address </label> <br>
					<input id = "cemail" type="text" name="cemail" tabindex="6"/> 
				</div>
				
				<div class="signUpFieldHolder">
					<label class="fieldTitle" >Password</label> <br>
					<input id = "password" type="password" name="password" tabindex="7" />  
				</div>
					
				<div class="signUpFieldHolder">
					<label class="fieldTitle" >Confirm Password</label> <br>
					<input id = "cpassword" type="password" name="cpassword" tabindex="8" /> 
				</div>	 
				
				<br>
				<input type="submit" id="create" value="Create Account" tabindex="9">
				
				<p id=h5>By clicking "Create Account", You acknowledge that standard data fees and text messaging rates may apply based on your plan with your mobile carrier.</p>
			</div>
				
		</form>
	
	
		<script>
			$(document).ready(function() {
				$("#phone").kendoMaskedTextBox({
				    mask: "(000) 000-0000",
				    value: "(111) 222-3333"
				});
				$("#signUpForm").validate({
					rules : {
						firstname : {
							required : true
						},
	
						lastname : {
							required : true
						},
	
						email : {
							required : true,
							email : true
						},
	
						cemail : {
							required : true,
							email : true,
							equalTo : "#email"
						},
						password : {
							required : true,
							minlength : 5
						},
						cpassword : {
							required : true,
							minlength : 5,
							equalTo : "#password"
						},
						phone : {
							required : true,
							maxlength : "14",
							minlength : "14"
						},
	
						carrier : {
							required : true
						}
					},
	
					messages : {
						firstname : "Please enter your First Name.",
	
						lastname :  "Please enter your Last Name.",
	
						email : {
							required : "Please enter your IBM email address.",
							email : "Email must be a valid IBM email address.",
						},
	
						cemail : {
							required : "Please enter your IBM email address.",
							email : "Email must be a valid IBM email address.",
							equalTo : "Email does not match."
						},
	
						password : {
							required : "Please enter your password.",
							minlength : "Password must be at least 5 characters.",
						},
	
						cpassword : {
							required : "Please enter your password.",
							minlength : "Password must be at least 5 characters.",
							equalTo : "Password does not match."
						},
	
						phone : {
							required : "Please enter your phone number.",
							digits : "Phone number can only contain digits."
						},
	
						carrier : "Please select your phone carrier."
					},
				});
			})
	
			function askForCarriers() {
	
					var carrierSelect = document.getElementById("carrier");
	
					<c:forEach items="${carrierArray}" var= "carrier">
				        var option = document.createElement('option');
				        option.text = "${carrier.getProvider()}";
				        option.value = "${carrier.getProvider()}";
				        carrierSelect.add(option, 0);
				        document.getElementById("carrier").selectedIndex = 0;
					</c:forEach>
				}
	
			function checkParameters(){
				var notification = "<%=request.getParameter("notif")%>";
	
				if(notification != "null"){
					var notif = document.getElementById("notification");
					notif.style.display = "block";
	
					if(notification == "duplicate"){
						notif.innerHTML = "User is already registered or in activation process.";
					}
					else if(notification == "error"){
						notif.innerHTML = "An error occurred. Please try again, or contact your manager.";
					}
					else if(notification == "noDB"){
						notif.innerHTML = "Database connection failure. Please try again, or contact your manager.";
					}
					else{
						notif.innerHTML = "Request failed. Please try again, or contact your manager.";
					}
				}
			}
			
			function setPhone(){
				var phoneElement = document.getElementById("phone");
				
				phoneElement.style.borderColor = "white";
				phoneElement.style.backgroundColor = "black";
				phoneElement.style.color = "white";
				phoneElement.style.borderRadius = "0px";
				phoneElement.style.width = "450px";
				phoneElement.style.height = "40px";
			}
		</script>
	</body>
</html>