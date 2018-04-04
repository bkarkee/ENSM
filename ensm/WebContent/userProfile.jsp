<%@ page import="wasdev.sample.model.User"%>
<%@ page import="wasdev.sample.model.Carrier"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
 
<!DOCTYPE html>
<html >

	<head>
		<title>ENSM User Profile</title>
		
		<link rel="stylesheet" type="text/css" href="/css/userProfile.css">
		<link rel="stylesheet" href="http://kendo.cdn.telerik.com/2016.2.504/styles/kendo.common.min.css"/>
		<link rel="stylesheet" href="http://kendo.cdn.telerik.com/2016.2.504/styles/kendo.rtl.min.css"/>
		<link rel="stylesheet" href="http://kendo.cdn.telerik.com/2016.2.504/styles/kendo.silver.min.css"/>
		<link rel="stylesheet" href="http://kendo.cdn.telerik.com/2016.2.504/styles/kendo.mobile.all.min.css"/>
		<link rel="shortcut icon" type="image/x-icon" href="/images/favicon.png">
		
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
					if(u.isManagement()){
						response.sendRedirect("/adminPage");
					}
				}
			}
			catch(Exception ex){
				out.println(ex);
			}
		%>
	
		<script>
			function askForCarriers() {
				var carrierSelect = document.getElementById("carrier");
				<c:forEach items="${carrierArray}" var= "carrier">
			        var option = document.createElement('option');
			        option.text = "${carrier.getProvider()}";
			        option.value = "${carrier.getProvider()}";
			        carrierSelect.add(option, 0);
				</c:forEach>
			}
			function userInfo() {
				document.getElementById("fname").value = "${user.getFirstname()}";
				document.getElementById("lname").value = "${user.getLastname()}";
				document.getElementById("email").value = "${user.getEmail()}";
				document.getElementById("carrier").value = "${user.getCarrier()}";
				
				var phone = "${user.getPhonenumber()}";
				$("#phone").kendoMaskedTextBox({
				    mask: "(000) 000-0000",
				    value: "(" + phone.substring(0,3) + ")" + " " + phone.substring(3,6) + "-" + phone.substring(6,10)
				});
				
				var phoneElement = document.getElementById("phone");

				phoneElement.style.borderColor = "black";
				phoneElement.style.backgroundColor = "black";
				phoneElement.style.color = "white";
				phoneElement.style.width = "190px";
				phoneElement.style.height = "25px";
				phoneElement.style.textIndent = "0px";
				phoneElement.style.borderRadius = "0px";
				
				var tok = "";
		        tok=  "<%=request.getParameter("token")%>";
		        
		        var lblNotif = document.getElementById("notification");
		        
		        if (tok == "success")  {
		        	lblNotif.style.display = "inline-block";
		        	lblNotif.innerHTML = "Your changes have been saved.";
		        }
		        else if(tok == "successPhone"){
		        	lblNotif.style.display = "inline-block";
		        	lblNotif.innerHTML = "Your changes have been saved. <br/>Since your phone details have changed, you will receive a confirmation text.";
		        }
		        else if(tok == "noDB"){
		        	lblNotif.style.display = "inline-block";
		        	lblNotif.innerHTML = "Database connect failure. <br/>Please try again.";
		        }
		        else if(tok == "err"){
		        	lblNotif.style.display = "inline-block";
		        	lblNotif.innerHTML = "Your update failed. <br/>Please try again.";
		        }
		        else{
		        	lblNotif.style.display = "none";
		        }
			}
			
			function logout(){
				document.getElementById("requestType").value="logout";
				document.getElementById("frm_optout").submit();
			}
		</script>
	
	</head>
	
	<body onload="askForCarriers(); userInfo()">
	
		<div style="margin-left: auto; margin-right: auto;">
			<h1 align="center" style="color: #ebebe0;">Emergency Notification System: User Profile</h1>
				
			<div style="text-align: center;">
				<img src="/images/logoibm.jpg" />
			</div>
		</div><br/><br/>
	
		<form id=frm_optout action="Servlet" method="post" accept-charset=utf-8>
			<input type="hidden" value="optoutUser" name="requestType" id="requestType">
			
			<div style="float: right; display: inline;">
				<a id="aChangePass" href="/retrievePassword" style="display: inline;">Change Password</a>
				<button id="LogOut" onclick="logout();" style="display: inline;">Log Out</button>
			</div>
	
			<div>
				<label style="color: white; margin-left: 0px;">Name: </label> <br/>
				<input type="text" id="fname" name="fname"	value="">
				<input type="text" id="lname" name="lname" value="">
	
			</div><br/>
			
			<div style="width: 95%; height: 80px;">
				<div style="width: 400px; float: left;">
					<div style="display: inline; float: left">
						<label style="color: white; margin-left: 0px;">Phone Number: </label> <br/>
						<input type="text" name="phone" id="phone" maxlength="10">
					</div>
					
					<div style="display: inline;">
						<label style="color: white; margin-left: 8px;">Phone Carrier: </label><br/>
						<select id="carrier" name="carrier"></select>
					</div>
				</div>
				
				<label id="notification"></label>
			</div>
			
			<div class="Same">
				<label style="color: white; margin-left: 0px;">Email Address: </label><br/>
				<input type="email" name="email" id="email" value="" readonly="readonly">
			</div>
			
			<br/>
			 
			<div style=" width: 627px;">
				<div style="width: 100px; margin: 0 auto;">
					<button type = "submit" class="buttonopt">Update</button>
				</div>
			</div>
			
			<div style="width: 500px;">
				<label id="lblFees">*Standard data fees and text messaging rates may apply based on your plan with your mobile phone carrier</label> 
			</div>
		
		</form>
	</body>
</html>

