<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ page import="wasdev.sample.model.Carrier"%>
<%@ page import="wasdev.sample.model.User"%>

<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
	<title>ENSM Add IBMers</title>
	<link rel="icon" type="image/png" href="/images/favicon.png">
	
	<link rel="stylesheet" type="text/css" href="/css/addUsers.css">
	<script type="text/javascript" src="/jquery/jquery-1.11.1.js"></script>
	
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
		var carriers, carrierCount = 0;
		
		carriers = new Array(20);
		for (var i = 0; i < 20; i++) {
			carriers[i] = new Array(2);
		}
		
		<c:forEach items="${carrierArray}" var= "carrier">
			carriers[carrierCount][0] = "${carrier.getProvider()}";
	        carriers[carrierCount][1] = "${carrier.getPhoneAddress()}";
	        carrierCount++;
		</c:forEach>
	
		function createRow(){
			var body = document.getElementById("tableBody");
			var rowCount = document.getElementById("tableBody").rows.length;
			if(rowCount == 10){return;}
			
			var tr = document.createElement('TR');
			tr.id = rowCount + "";
			
			//first
			var td1 = document.createElement('TD');
			var inp1 = document.createElement('INPUT');
			inp1.name = rowCount.toString() + "1";
			inp1.type = "text";
			td1.appendChild(inp1);
			tr.appendChild(td1);

			//last
			var td2 = document.createElement('TD');
			var inp2 = document.createElement('INPUT');
			inp2.name = rowCount.toString() + "2";
			inp2.type = "text";
			td2.appendChild(inp2);
			tr.appendChild(td2);

			//email
			var td3 = document.createElement('TD');
			var inp3 = document.createElement('INPUT');
			inp3.name = rowCount.toString() + "3";
			inp3.type = "text";
			inp3.style.width = "200px";
			td3.appendChild(inp3);
			tr.appendChild(td3);

			//phone
			var td4 = document.createElement('TD');
			var inp4 = document.createElement('INPUT');
			inp4.name = rowCount.toString() + "4";
			inp4.type = "text";
			td4.appendChild(inp4);
			tr.appendChild(td4);

			//carrier
			var td5 = document.createElement('TD');
			var sel1 = document.createElement('SELECT');
			sel1.name = rowCount.toString() + "5";
			td5.appendChild(sel1);
			tr.appendChild(td5);
        	
        	for(var i = carrierCount - 1; i > -1; i--){
        		var opt = document.createElement('OPTION');
        		opt.value = carriers[i][0];
				opt.text = carriers[i][0];
        		sel1.appendChild(opt);
        	}

			//floor
        	var td6 = document.createElement('TD');
			var inp5 = document.createElement('INPUT');
			inp5.name = rowCount.toString() + "6";
			inp5.type = "number";
			inp5.min = "0";
			inp5.max = "3";
			inp5.value = 0;
			td6.appendChild(inp5);
			tr.appendChild(td6);

			//manager
			var td7 = document.createElement('TD');
			var inp6 = document.createElement('INPUT');
			inp6.name = rowCount.toString() + "7";
			inp6.type = "text";
			td7.appendChild(inp6);
			tr.appendChild(td7);
			
			//Delete Row
			var td8 = document.createElement('TD');
			var inp7 = document.createElement('INPUT');
			inp7.name = "del" + rowCount.toString();
			inp7.type = "button";
			inp7.value = "Delete";
			inp7.onclick = function() {deleteRow(this)};
			td8.appendChild(inp7);
			tr.appendChild(td8);
			
        	body.appendChild(tr);
		}
		
		function deleteRow(element){
			var row = $(element).closest("tr").index();
			
			document.getElementById("tableBody").deleteRow(row);
			if(document.getElementById("tableBody").rows.length < 1){createRow();}
		}
		
		function submitToServer(){
			var frm = document.getElementById("frm_addUsers");
			var count = document.getElementById("tableBody").rows.length;
			
			
			for(var i = 0; i < count; i++){
				if(!validateTableBeforeSubmit(i)){return;}
				var status = callServletForData(i);
			}
			
		}
		
		function callServletForData(rowNum){
			var row = document.getElementById("tableBody").rows[rowNum];
			var cells = row.cells;
			
			var params = {
					requestType: "addUsers",
					first: cells[0].childNodes[0].value,
					last: cells[1].childNodes[0].value,
					email: cells[2].childNodes[0].value,
					phone: cells[3].childNodes[0].value,
					carrier: cells[4].childNodes[0].value,
					floor: cells[5].childNodes[0].value,
					manager: cells[6].childNodes[0].value
				};
			
			$.post('/Servlet', params, 
			    function(returnedData){
					switch(returnedData){
						case "db": alert("database insert failed for: " + cells[0].childNodes[0].value + cells[1].childNodes[0].value); return;
						case "duplicate": alert("duplicate user found: " + cells[0].childNodes[0].value + cells[1].childNodes[0].value); break;
						case "error": alert("error in the server: " + cells[0].childNodes[0].value + cells[1].childNodes[0].value); break;
						case "success": removeRow(row); break;
						case "failed": alert("Failed to reach the server: " + cells[0].childNodes[0].value + cells[1].childNodes[0].value); break;
					}
				}).fail(
					function(){
						alert("Failed to reach the server at all.");
					});
		}
		
		function removeRow(row){
			var table = document.getElementById("tableBody");
			table.deleteRow(row);
			if(table.rows.length < 1){createRow();}
		}
		
		function validateTableBeforeSubmit(rowNum){
			var tableBody = document.getElementById("tableBody");
			var count = tableBody.rows.length;
			
			var row = tableBody.rows[rowNum];
			var cells = row.cells;
			
			resetCellBorders(cells);
			
			if(!validateRowEntries(cells)){return false;}
			
			return true;
		}
		
		function resetCellBorders(cells){
			for(var i = 0; i < 7; i++){
				cells[i].childNodes[0].style.border = "1px solid white";
			}
		}
		
		function validateRowEntries(cells){
			var successCheck = true;
			var fname = cells[0].childNodes[0].value;
			var lname = cells[1].childNodes[0].value;
			var email = cells[2].childNodes[0].value;
			var phone = cells[3].childNodes[0];
			var carrier = cells[4].childNodes[0].value;
			var floor = parseInt(cells[5].childNodes[0].value);
			var manager = cells[6].childNodes[0].value;

			if(fname == null || fname == ""){ cells[0].childNodes[0].style.border = "1px solid red"; successCheck = false; }
			if(lname == null || lname == ""){ cells[1].childNodes[0].style.border = "1px solid red"; successCheck = false; }
			if(carrier == null || carrier == ""){ cells[4].childNodes[0].style.border = "1px solid red"; successCheck = false; }
			if(floor == null || floor < 0 || floor > 3){ cells[5].childNodes[0].style.border = "1px solid red"; successCheck = false; }
			if(manager == null || manager == ""){ cells[6].childNodes[0].style.border = "1px solid red"; successCheck = false; }
			
			if(email == null || email == "" || email.includes(" ") ||
			  !email.includes("ibm.com") || !email.includes("@")){ cells[2].childNodes[0].style.border = "1px solid red"; successCheck = false; }
			
			if(phone.value == null || phone.value == ""){ cells[3].childNodes[0].style.border = "1px solid red"; successCheck = false; }
			while(phone.value.includes("-")){ phone.value = phone.value.replace("-", ""); }
			while(phone.value.includes(" ")){ phone.value = phone.value.replace(" ", ""); }
			
			if(phone.value.length < 10){cells[3].childNodes[0].style.border = "1px solid red"; successCheck = false;}
			
			return successCheck;
		}

		function redirectToSendText() {
			window.location = "/adminPage?token=st";
		}
		function redirectToProfile(){
			window.location = "/adminPage?token=pr";
		}
		function redirectToAddUsers(){
			window.location = "/addUsers";
		}
	</script>

</head>
<body onload="createRow()">
	<h1 id="pageTitle" align="center">Emergency Notification System: Add Users Page</h1>
	<div id="imagefile">
		<img src="/images/logoibm.jpg" />
	</div>
	<br>
	
	<div id="maindiv">
		<div id="subdiv1" style="position: relative">
			<form action="Servlet" method="post" style="margin-top: 5px; margin-bottom: 0px" accept-charset="utf-8">
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
			
			<form name="logoutfrm" id="logoutfrm" action="Servlet" method="post" style="position: absolute; bottom: -10px; left: 10px" accept-charset="utf-8">
				<input type="hidden" value="logout" name="requestType">
				<button id="logout">Log Out</button>
			</form>
		</div>
		
		<div id="subdiv2">
			<TABLE BORDER="1" id="userTable" class="tablesorter">
				<thead>
					<tr>
						<th>First Name</th>
						<th>Last Name</th>
						<th>Email</th>
						<th>Phone</th>
						<th>Carrier</th>
						<th>Floor</th>
						<th>Manager</th>
						<th style="width: 46px;">Delete Row</th>
					</tr>
				</thead>
					
				<tbody id="tableBody"></tbody>
			</table>
			
			<input type="button" value="Submit" onClick="submitToServer();" id="submit"/>
			<input type="button" value="Add Row" onClick="createRow();" id="createRow"/>
		</div>
	</div>
	
</body>
</html>