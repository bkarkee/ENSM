<!DOCTYPE html> 
<%@ page import="wasdev.sample.model.Carrier"%>
<%@ page import="wasdev.sample.model.User"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<html>
	<head>
		<title>ENSM Admin Page</title>
		<link rel="icon" type="image/png" href="/images/favicon.png">
		
		<link rel="stylesheet" type="text/css" href="/css/adminPage.css">
		<link rel="stylesheet" href="http://kendo.cdn.telerik.com/2016.2.504/styles/kendo.common.min.css"/>
		<link rel="stylesheet" href="http://kendo.cdn.telerik.com/2016.2.504/styles/kendo.rtl.min.css"/>
		<link rel="stylesheet" href="http://kendo.cdn.telerik.com/2016.2.504/styles/kendo.silver.min.css"/>
		<link rel="stylesheet" href="http://kendo.cdn.telerik.com/2016.2.504/styles/kendo.mobile.all.min.css"/>
		
		<script src="/jquery/jquery-1.11.1.js"></script>
		<script src="/jquery/jquery.validate.js"></script>
		<script src="/jquery/jquery.validate.min.js"></script>
		<script src="http://kendo.cdn.telerik.com/2016.2.504/js/kendo.all.min.js"></script>
		
		<%
			try{
				response.setHeader("Cache-Control","no-cache");
				response.setHeader("Cache-Control","no-store");
				response.setHeader("Pragma","no-cache");
				response.setDateHeader ("Expires", 0);
				
				if(session.getAttribute("user")==null){
				  	response.sendRedirect("/login");
				}
				else{
					User u = (User) session.getAttribute("user");
					if(!u.isManagement()){
						response.sendRedirect("/userProfile");
					}
				}
			}
			catch(Exception ex){
				out.println(ex);
			}
		%>
		<script>
			function onloadFunction(){
				askForCarriers();
				userInfo();
				updatePhoneInput();

				 // If Internet Explorer, return version number
			    if(navigator.appName == 'Microsoft Internet Explorer' ||  !!(navigator.userAgent.match(/Trident/) || 
			       navigator.userAgent.match(/rv 11/)) || navigator.userAgent.toUpperCase().indexOf('MSIE') == 1){
			        document.getElementById("subject").style.width = "24.5%";
			    }
			    else{
			        document.getElementById("subject").style.width = "24.3%";
			    }
			}
			function askForCarriers() {
				var carrierSelect = document.getElementById("carrier");
		
				<c:forEach items="${carrierArray}" var= "carrier">
			        var option = document.createElement('option');
			        option.text = "${carrier.getProvider()}";
			        carrierSelect.add(option, 0);
			        document.getElementById("carrier").value = "${carrier.getPhoneAddress()}";
				</c:forEach>
			}
			function userInfo() {
				document.getElementById("fname").value = "${user.getFirstname()}";
				document.getElementById("lname").value = "${user.getLastname()}";
				document.getElementById("email").value = "${user.getEmail()}";
				document.getElementById("carrier").value = "${user.getCarrier()}";
		
				var text = "";
				text = "<%=request.getParameter("sent")%>";
				var notif = document.getElementById("lblNotification");
				notif.style.fontWeight = "900";
				notif.style.color = "#d10000";
				notif.style.fontSize = "large";
				
				if (text == "true") { notif.innerHTML = "Message Sent Successfully!"; } 
				else if (text == "false") { notif.innerHTML = "Message Send Failed: Please Select the recipient"; }
				else if (text == "nosubject") { notif.innerHTML = "Message Send Failed: Enter the subject"; }
				else if (text == "nobody") { notif.innerHTML = "Message Send Failed: Body can't be empty."; } 
				else if (text == "nogroup") { notif.innerHTML = "Message Send Failed: Select a recipient group."; }
				else { notif.innerHTML = ""; }
		
				var token = "";
				token = "<%=request.getParameter("token")%>";
				if(token == "pr"){
					redirectToProfile();
					var token2 = "";
					token2 = "<%=request.getParameter("token2")%>";
					var pNotif = document.getElementById("notification");
					pNotif.style.display = "inline-block";
			        
					if (token2 == "success")  { pNotif.innerHTML = "Your changes have been saved."; }
			        else if(token2 == "noDB"){ pNotif.innerHTML = "Database connect failure. Please try again."; }
			        else if(token2 == "err"){ pNotif.innerHTML = "Your update failed. Please try again."; }
			        else{ pNotif.style.display = "none"; }
					
				}
				else if(text != "null" && text != ""){
					var sbj, bdy, sendTo;

					sbj = "<%=request.getParameter("SBJ")%>";
					bdy = "<%=request.getParameter("BDY")%>";
					sendTo = "<%=request.getParameter("GRP")%>";
					
					if(sbj != null && sbj != ""){ document.getElementById("SubjectText").value = sbj; }
					if(bdy != null && bdy != ""){ document.getElementById("txt").value = bdy; }
					if(sendTo != null && sendTo != ""){ document.getElementById("toDiv").innerHTML = sendTo; }
				}
				
				var titleValue = "";
				titleValue = "<%=request.getParameter("token")%>";
				var title = document.getElementById("pageTitle");
				if(titleValue == 'st'){
					title.innerHTML = "Emergency Notification System: Send Text";
				}
				else{
					title.innerHTML = "Emergency Notification System: Admin Profile";
				}
			}
			
			function updatePhoneInput(){
				var phoneElement = document.getElementById("phone");
			
				phoneElement.style.borderColor = "white";
				phoneElement.style.backgroundColor = "black";
				phoneElement.style.color = "white";
				phoneElement.style.borderRadius = "0px";
				phoneElement.style.width = "190px";
				phoneElement.style.height = "25px";
				phoneElement.style.textIndent = "0px";
				phoneElement.style.borderRadius = "0px";
			}
		
			function MsgContent(element) {
				document.getElementById("SubjectText").value = element.value;
			}
		
			function receiver(element) {
				var clickedString = element.value;
				if(clickedString == ""){return false;}
				var toLbl = document.getElementById("toLbl");
				toLbl.innerHTML = clickedString;
				if(toLbl.style.display == "none"){toLbl.style.display = "inline";}
			}
		
			function sendbutton() {
				document.getElementById("groupsToSend").value = document.getElementById("toLbl").innerHTML;
			}
		
			function redirectToSendText() {
				document.getElementById("subform").style.display = "none";
				document.getElementById("main").style.display = "block";
				document.getElementById('pageTitle').innerHTML = "Emergency Notification System: Send Text";
			}
		
			function redirectToProfile() {
				document.getElementById("subform").style.display = "block";
				document.getElementById("main").style.display = "none";
				document.getElementById('pageTitle').innerHTML = "Emergency Notification System: Admin Profile";
				document.getElementById("lblNotification").innerHTML = "";
			}
			function redirectToAddUsers(){
				window.location = "/addUsers";
			}
			
			function checkboxfunction() {
				if (document.getElementById("checker").checked) {
					document.getElementById("condi").style.display = "block";
				} else {
					document.getElementById("condi").style.display = "none";
				}
			}
			function limitText(limitField, limitCount, limitNum) {
				if (limitField.value.length > limitNum) {
					limitField.value = limitField.value.substring(0, limitNum);
				} else {
					limitCount.value = limitNum - limitField.value.length;
				}
			}
		</script>
	
	</head>
	
	<body onload="onloadFunction();">
		<h1 id="pageTitle" align="center">Emergency Notification System</h1>
		<div id="imagefile">
			<img src="/images/logoibm.jpg" />
		</div>
		<br>
		
		<div id="maindiv">
			<div id="subdiv1" style="position: relative">
			<form action="Servlet" method="post" style="position: inline; margin-top: 5px" accept-charset="utf-8">
				<input type="hidden" value="databaseTable" name="requestType">
				<button id="database" type=submit>Database</button>
			</form>
			
			<button id="sendText" onclick="redirectToSendText()" style="position: inline; margin-top: 5px">Send Text</button>
			<button id="profile" onclick="redirectToProfile()" style="position: inline; margin-top: 5px">Profile</button>
			
			<form action="Servlet" method="post" style="position: inline; margin-top: 5px; margin-bottom: 0px" accept-charset="utf-8">
				<input type="hidden" value="databaseResponse" name="requestType">
				<button id="response">Responses</button>
			</form>
			
			<button id="profile" onclick="redirectToAddUsers()" style="position: inline; margin-top: 5px">Add Users</button>
			
			<form name="logoutfrm" id="logoutfrm" action="Servlet" method="post" style="position: absolute; bottom: 10px; left: 10px" accept-charset="utf-8">
				<input type="hidden" value="logout" name="requestType">
				<button id="logout">Log Out</button>
			</form>
		</div>
	
			<div id="subdiv2">
				<form id="main" action="Servlet" method="post" accept-charset=utf-8>
					<input type="hidden" value="jspadmin" name="requestType"> 
					<input type="hidden" id="groupsToSend" value="" name="groupsToSend">
	
					<div id="sendTextContainer">
					
						<div id="toSubDiv">
							<label style="float: left; margin-left: 5px;">To: </label>
							<label id="toLbl" style="float: left; display: none;"></label>
						</div>
					
						<span id="subjectSpan">
							<Input class="textboxes" id="SubjectText" name="subject" maxlength="10" placeholder="Subject"/>
							<select class="textboxes" id="subject" name="quickSubject" onchange="MsgContent(this)">
								<option value=""></option>
								<option value="Alert">Alert</option>
								<option value="Warning">Warning</option>
								<option value="Emergency">Emergency</option>
								<option value="Pls Reply">Pls Reply</option>
							</select>
						</span>
						
						<textarea class="textboxes" id="txt" name="Message"
							onKeyDown="limitText(this.form.txt,this.form.countdown,117);"
							onKeyUp="limitText(this.form.txt,this.form.countdown,117);" placeholder="Message"></textarea>
						
						<br>
						
						<label id="textRemaining" style="margin-left: 0px;"> 
							You have 
								<input id="counterBox" readonly type="text" name="countdown" value="117"/>
							characters left.
						</label> 
					</div>
	
					<div id="navigator">
						<label style="font-size: 19px; margin-left: 0px;">Recipients</label>
						
						<select id="Employee" name="To" onclick="receiver(this)">
							<option value=""></option>
							<option id="CIC" value="CIC">CIC</option>
							<option id="mngmt" value="Management">Management</option>
						</select>

						<button id="send" onclick="sendbutton()">Send</button>
	
					</div>
	
				</form>
	
				<form id="subform" action="Servlet" method="post" accept-charset=utf-8>
					<input type="hidden" value="optoutUser" name="requestType">
	
					<div id="leftDiv">
						<p class="profileP">
							Name: <br>
							<input type="text" id="fname" name="fname"> <input type="text" id="lname" name="lname">
		
						</p>
						
						<p class="profileP">
							Phone Number: 
							&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
							Phone Carrier: <br/>
							
							<input type="text" name="phone" id="phone" maxlength="10">
							<select id="carrier" name="carrier" style="color: white; background-color: black;">
								<option	value=""></option>
							</select> <br/>
						</p>
						
						<p class="profileP">
							Email Address: <br>
							<input type="text" name="email" id="email" readonly="readonly">
						</p>
					</div>
					<div id="rightDiv">
						<a id="aChangePass" href="/retrievePassword"> Change Password</a>
						
						<br/><br/>
						<label id="notification" style="display: none;"></label>
						
						<br/>
					</div>
					<button type="submit" id="buttonopt">Update</button>
				</form>
			</div>
		</div>
		
		<h3 id="lblNotification" style="color: red;"></h3>
		
		<script>
			$(document).ready(function() {
				var phone = "${user.getPhonenumber()}";
				$("#phone").kendoMaskedTextBox({
				    mask: "(000) 000-0000",
				    value: "(" + phone.substring(0,3) + ")" + " " + phone.substring(3,6) + "-" + phone.substring(6,10)
				});
			})
		</script>
		
	</body>
</html>
