<%@ page import="java.io.Console"%>
<%@ page import="java.sql.*"%>
<%@ page import="javax.servlet.http.HttpServlet"%>
<%@ page import="wasdev.sample.model.User"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/sql" prefix="sql"%>

<html>

<head>
	<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
	<title>ENSM Users List</title>
	
	<script type="text/javascript" src="/jquery/jquery-1.11.1.js"></script>
	<script type="text/javascript" src="/jquery/jquery.tablesorter.js"></script>
	<link rel="shortcut icon" type="image/x-icon" href="/images/favicon.png">
	
	<link rel="stylesheet" type="text/css" href="/css/userListTable.css">
	
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
		function redirectToSendText() { window.location = "/adminPage?token=st"; }
		function redirectToProfile() { window.location = "/adminPage?token=pr"; }
		function redirectToAddUsers(){ window.location = "/addUsers"; }
		
		function exportToAdmin()
		{
		   
		}
		
		function onChange(elementID) {
			var element = document.getElementById(elementID);
			//get  row number
			var row = element.id;
			row = row.replace(/\D/g, '');
			row = parseInt(row);

			//get entire row 
			var curRow = document.getElementById(("r" +row)).cells;
			// get column name that is being changed
			var name = element.name;

			//name = name.replace(/\d+/g, '');
			//get changed users email
			var email = curRow[2].innerHTML;

			//get the hidden elements
			var hiddenFloor = document.getElementById("hiddenFloor");
			var hiddenGroupBool = document.getElementById("hiddenGroupBool");
			var hiddenEmail = document.getElementById("hiddenEmail");
			var hiddenElementName = document.getElementById("hiddenName");

			//set hidden variables to element names
			document.getElementById("hiddenEmail").value = email;
			document.getElementById("hiddenName").value = name;
			
			if (name == "flr") {
				hiddenFloor.value = element.value;
				
				if(hiddenFloor.value > -1){ submitToServer(); }
				hiddenFloor.value = "";
			} else {
				hiddenGroupBool.value = element.value;
				
				if(element.value == "true"){element.style.background = '#e6ffe6';}
				else{element.style.background = '#ffe6e6';}
				
				submitToServer();
				hiddenGroupBool.value = "";
			}

		}
		
		function highlightRow(element){
			resetBackgrounds();
			
			var children = element.childNodes;
			var i = 0;
			
			for(i; i < children.length; i++){
				children[i].style.borderColor = 'red';
				children[i].style.borderWidth = 'thin';
			}
		}
		
		function resetBackgrounds(){
			var rows = document.getElementById("TBODY").rows;
			
			var i = 0;
			var j;
			
			for(i; i < rows.length; i++){
				j = 0;
				var children = rows[i].childNodes;
				
				for(j; j < children.length; j++){
					children[j].style.borderColor = 'white';
					children[j].style.borderWidth = 'thin';
				}
			}
		}
		
		function autoArrange(){
			//simulate click on last names to arrange
			var curRow = document.getElementById("myTable").rows[0].cells;
			curRow[1].click();
		}
		
		function submitToServer() {
			var params = {
				requestType: "tableUpdate",
				hiddenEmail: document.getElementById("hiddenEmail").value,
				hiddenName: document.getElementById("hiddenName").value,
				hiddenFloor: document.getElementById("hiddenFloor").value,
				hiddenGroupBool: document.getElementById("hiddenGroupBool").value
			};
		
			$.post("Servlet", $.param(params));
		}
		function capFirstLetter(string) {
		    return string.charAt(0).toUpperCase() + string.slice(1);
		}
		</script>

</head>

<body onload="autoArrange();">
	<H1>Emergency Notification System: User Table</H1>
	<div id="imagefile">
		<img src="/images/logoibm.jpg" />
	</div><br>
	

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
		
			<form  id=frm_dbTable action="" method="post" accept-charset=utf-8>
				<script >
					$(document).ready(function() {$("#myTable").tablesorter();}); 
				</script>
		
				<input type="hidden" value="tableUpdate" name="requestType">
				<input type="hidden" value="" name="hiddenEmail" id="hiddenEmail"> 
				<input type="hidden" value="" name="hiddenName" id="hiddenName"> 
				<input type="hidden" value="" name="hiddenFloor" id="hiddenFloor">
				<input type="hidden" value="" name="hiddenGroupBool" id="hiddenGroupBool">
				
				<div style="width: 100%;">
					<div style="float: left; margin-left: 10px;">
						<label id="userCount">Users: </label>
					</div>
				</div><br>
	
				<TABLE BORDER="1" id="myTable" class="tablesorter"> 
					<thead>
						<tr>
							<th>First Name</th>
							<th>Last Name</th>
							<th>Email</th>
							<th>Phone Number</th>
							<th style="width: 200px;">Carrier</th>
							<th>Phone Confirmed</th>
							<th>floor</th>
							<th>Manager</th>
						</tr>
					</thead>
				</TABLE>
			
				<script>     
				    var table = document.getElementById('myTable');
				    table.border='1';
				    
				    var tableBody = document.createElement('TBODY');
				    tableBody.id = "TBODY";
				    table.appendChild(tableBody);
				    
				    var userCount = 0;
				    
				    <c:forEach items="${userArray}" var="user">
				    	userCount++;
				    	var tr = document.createElement('TR');
					    var rowNum = table.rows.length;
				    	tr.id = "r" + (rowNum - 1);
				    	tr.onclick = function(){highlightRow(this);};
				    	tr.class = "row";
					    tr.style.color='white';
					    tableBody.appendChild(tr);
					    
					    var userValues = [capFirstLetter("${user.getFirstname()}"), 
					    		capFirstLetter("${user.getLastname()}"), "${user.getEmail()}", 
					    		"${user.getPhonenumber()}", "${user.getCarrier()}", 
					    		capFirstLetter("${user.getConfirmedPhone()}")];
					    
					    for(var i = 0; i < userValues.length; i++){
					    	var td = document.createElement('TD');
						    td.appendChild(document.createTextNode(userValues[i]));
						    tr.appendChild(td);
					    }
					    
					    var floor = document.createElement('INPUT');
					    floor.id = "flr" + (rowNum-1);
					    floor.name = "flr";
					    floor.type = "number";
					    floor.onchange = function(){onChange(this.id);};
					    floor.value = "${user.getFloor()}";
					    floor.style.width = "50px";
					       
					    var td = document.createElement('TD');	       
					    td.appendChild(floor);
					    tr.appendChild(td);
					    
					    var select = document.createElement('select');
				    	select.id = "mng" + (rowNum-1);
					    select.name = "mng";
					    select.onchange = function(){onChange(this.id);};
					    var optionT = document.createElement('option');
					    optionT.text = "True";
					    optionT.value = "true";
					    optionT.style.backgroundColor = '#33ff33';
					    select.add(optionT, 0);
					    var optionF = document.createElement('option');
					    optionF.text = "False";
					    optionF.value = "false";
					    optionF.style.backgroundColor = '#ff6666';
					    select.add(optionF, 1);
					    select.value = "${user.isManagement()}";
					    if(select.value == "true"){select.style.background = '#33ff33';}
						else{select.style.background = '#ff6666';}
					       
					    var td1 = document.createElement('TD');	       
					    td1.appendChild(select);
					    tr.appendChild(td1);
		       		</c:forEach>
		       		

					document.getElementById("userCount").innerHTML = "Total Users: " + userCount;
				</script>
			</form>
		</div>
	</div>
		<button type= "submit" id="btnExport" onclick="window.open('/userListExport')"> EXPORT </button>
</body>

</html>
