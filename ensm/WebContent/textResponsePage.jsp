<%@page import="java.io.Console"%>
<%@ page import="java.sql.*"%>
<%@ page import="javax.servlet.http.HttpServlet"%>
<%@ page import="wasdev.sample.model.User"%>
<%@ page import="wasdev.sample.model.UserResponses"%>
<%@ page import="java.io.*,java.util.*" %>
<%@ page import="javax.servlet.jsp.*" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/sql" prefix="sql"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
	<title>ENSM Response Page</title>
	
	<link rel="stylesheet" type="text/css" href="/css/textResponsePage.css">
	
	<script type="text/javascript" src="/jquery/jquery-1.11.1.js"></script>
	<script type="text/javascript" src="/jquery/jquery.tablesorter.js"></script>
	<link rel="shortcut icon" type="image/x-icon" href="/images/favicon.png">
	
	<c:set var="totalTickets" value="${fn:length(ticketids)}" />
	
	<%
		try{
			response.setHeader("Cache-Control","no-cache");
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
	
	<script type="text/javascript">
		
		var updateInterval, currentTicket, tableBorderInterval, tableOpacity = 1;
	
		function fnExcelReport(){
		    var tab_text="<table border='2px'><tr bgcolor='#87AFC6'>";
		    var textRange; var j=0;
		    tab = document.getElementById('responseTable'); // id of table
		    
			//add ticket info to the report
			var ticketid = document.getElementById("ticketSelect").value;
			var ticketDate = document.getElementById("ticketDate").innerHTML;
			var ticketText = document.getElementById("ticketText").innerHTML;

		    tab_text += "<TR><TD>TicketId: " + ticketid + "</TD>";
		    tab_text += "<TD>" + ticketDate + "</TD>";
		    tab_text += "<TD>" + ticketText + "</TD></TR>";
		
		    for(j = 0 ; j < tab.rows.length ; j++) 
		    {     
		    	if(tab.rows[j].style.display != "none"){
		        	tab_text = tab_text + tab.rows[j].innerHTML + "</tr>";
		    	}
		    }
		
		    tab_text=tab_text+"</table>";
		    tab_text= tab_text.replace(/<A[^>]*>|<\/A>/g, "");//remove if u want links in your table
		    tab_text= tab_text.replace(/<img[^>]*>/gi,""); // remove if u want images in your table
		    //tab_text= tab_text.replace(/<input[^>]*>|<\/input>/gi, ""); // reomves input params
		
		    var ua = window.navigator.userAgent;
		    var msie = ua.indexOf("MSIE "); 
		
		    if (msie > 0 || !!navigator.userAgent.match(/Trident.*rv\:11\./))      // If Internet Explorer
		    {
		        txtArea1.document.open("txt/html","replace");
		        txtArea1.document.write(tab_text);
		        txtArea1.document.close();
		        txtArea1.focus(); 
		        sa=txtArea1.document.execCommand("SaveAs",true,"textResponsePage.xls");
		    }  
		    else                 //other browser not tested on IE 11
		        sa = window.open('data:application/vnd.ms-excel,' + encodeURIComponent(tab_text));  
		
		    return (sa);
		}
		
		function gatherResponseData(){
			//gathers the initial ticket ids
			var selectTicketIDS = document.getElementById("ticketSelect");
			/*var lblTicketDate = document.getElementById("ticketDate");
			var lblTicketText = document.getElementById("ticketText");*/
			
			//
			<c:forEach items="${ticketids}" var="tid" varStatus="ticketCounter">
			 	var option = document.createElement('option');
			 	var ticketId = "${tid[0]}";
			 	var ticketDate = "${tid[1]}";
			 	var ticketText = "${tid[2]}";
			 	
			    option.text = ticketDate + ticketText.substring(0, 29); //This is probably not how you do substring in js
			    option.value = ticketId;
			    selectTicketIDS.add(option, 0);
				<c:if test="${ticketCounter.count == totalTickets}">
					/*lblTicketDate.innerHTML = "Date: " + ticketDate;
					lblTicketText.innerHTML = "Message: " + ticketText;*/
					currentTicket = ticketId;
				</c:if>
			</c:forEach>
			
			selectTicketIDS.selectedIndex = 0;
			
			updateInterval = createInterval(10000);
			
		}
		
		function changeTicketTable(element){
			var value = element.value;
			var tableBody = document.getElementById("tableBody");
			/*var lblTicketDate = document.getElementById("ticketDate");
			var lblTicketText = document.getElementById("ticketText");*/

	        var oldtrs = tableBody.childNodes;

	        for(var x=0; x < oldtrs.length; x++)
	        {
	        	if(oldtrs[x].name != value){
	            	oldtrs[x].style.display = 'none';
	            }
	        }
			
			
			/*<c:forEach items="${ticketids}" var="tid">
				if("${tid[0]}" == value){
					lblTicketDate.innerHTML = "Date: " + "${tid[1]}";
					lblTicketText.innerHTML = "Message: " + "${tid[2]}";
				}
			</c:forEach>*/
			
			//creates the recurring text calls
			var servletUrl, requestType;
			servletUrl = 'http://ensm.mybluemix.net/Servlet?requestType=pastTicket&tid=' + value;
			
			var successURL = 'http://ensm.mybluemix.net/UserResponses.jsp?tid=' + value;
			
			
			var ticketMap = new Object;
			
			var tableRows = document.getElementsByName(value);
			
			if (tableRows.length < 1){
				$.ajax({
		            type: 'POST',
		            url: servletUrl,
		            dataType:'text',
		            success: function (data) {
		            	tableBody.innerHTML += data;
		            },
		            error: function(){
		            	console.log("error");
		            }
				});
			}
			else{
				var trs = document.getElementsByName(value);

		        for(var x=0; x < trs.length; x++)   // comparison should be "<" not "<="
		        {
		        	if(trs[x].style.display != 'table-row'){
		        		trs[x].style.display = 'table-row';
		            }
		        }
			}
			
		}
		
		function backgroundSet(element){
			var cells = element.cells;
			
			cells[0].style.borderColor = cells[1].style.borderColor = cells[2].style.borderColor = cells[3].style.borderColor = "white";
			
			
			//border-width: 2px;
			cells[0].style.borderWidth = cells[1].style.borderWidth = cells[2].style.borderWidth = cells[3].style.borderWidth = "1px";

			
			//set the labels
        	setCounters();
		}
		

		function setCounters(){
			//create variables for the counters
			var yesCounter = 0, noCounter = 0, nullCounter = 0, newCounter = 0, totalCounter = 0;
			
			//loop through the users to get the amounts
			var table = document.getElementById("responseTable");
			
			for(var i = 1; i < table.rows.length; i++){
				var cells = table.rows[i].cells;
				
				if(table.rows[i].style.display == "none"){continue;} //Check to see if this works correctly
				
				totalCounter++;
				
				if(cells[0].style.borderColor != "white"){ newCounter++; }
					    				
				switch(cells[2].innerHTML){
					case "Yes": yesCounter++; break;
					case "No": noCounter++; break;
					default: nullCounter++; break;
				}
				
				
			}
			//set the labels
			document.getElementById("yesCounter").innerHTML = "Yes: " + yesCounter;
			document.getElementById("noCounter").innerHTML = "No: " + noCounter;
			document.getElementById("nullCounter").innerHTML = "Null: " + nullCounter;
			document.getElementById("newCounter").innerHTML = "New: " + newCounter;
			document.getElementById("totalCounter").innerHTML = "Total: " + totalCounter; 
		}
		
		function createInterval(timer){
			return setInterval(function() {
				if(currentTicket == document.getElementById("ticketSelect").value){
					callServletForData();
				}
			}, timer);
		}
		function callServletForData(){
			//creates the recurring text calls
			var servletUrl, requestType, isUpdate;
			servletUrl = 'http://ensm.mybluemix.net/Servlet?';
			requestType = 'requestType=databaseResponse';
			isUpdate = 'update=yes';
			servletUrl += requestType + "&" + isUpdate;
			
			$.ajax({
	            type: 'POST',
	            url: servletUrl,
	            success: function (responseData) {
	            	//create variables for the counters
	            	if(responseData != null || responseData != ""){
		            	var dataString = responseData.split("%");
		            	var table = document.getElementById("tableBody");
		            	var update = false;
		            	
		            	for(var i = 0; i < dataString.length; i++){
		            		if(dataString[i].indexOf('FN') > -1){
		            			update = true;
		            			var userUpdate = dataString[i].split("$");
		            			
		            			for(var j = 0; j < table.rows.length; j++){
		            				var row = table.rows[j];
		            				var cells = row.cells;
		            				
		            				var responsetext = cells[2].innerHTML;
		            				responsetext = responsetext.replace(/(\r\n|\n|\r)/gm,"");
		            				var bodytext = cells[3].innerHTML;
		            				
		            				if(cells[0].innerHTML == userUpdate[1]){
		            					if(((responsetext != userUpdate[2]) || (bodytext != userUpdate[3]))){
			            					cells[2].innerHTML = userUpdate[2];
			            					cells[3].innerHTML = userUpdate[3];
			            					
			            					for(var k = 0; k < 4; k++){
				            					cells[k].style.borderColor = "rgb(42, 207, 9)";//new response outlined in red
				            					cells[k].style.borderWidth = "2px"; //new respones increase in border width
			            					}
			            					
			            					if (table.moveRow) {        // Internet Explorer
				            	                table.moveRow (j, 1);
				            	            } 
				            	            else {        // Cross browser
				            	                var firstRow = table.rows[0];
				            	                var secondRow = table.rows[j];
				            	                table.insertBefore(secondRow, firstRow);
				            	            }
		            					}
		            					break;
		            				}
		            			}
		            		}
		            	}
    					//set the labels
    	            	setCounters();
    	            	setTableBorderInterval();
		            }
	            },
	            error: function(){
	            	console.log("error");
	            }
	        });
		}
		
		function openSettings(){
			document.getElementById('divSettings').style.display = "block";
			document.getElementById("settingsNotif").innerHTML = "";
		}
		function closeSettings(){
			document.getElementById('divSettings').style.display = "none";
			document.getElementById("settingsNotif").innerHTML = "";
		}
		
		function changeRefreshRate(){
			clearInterval(updateInterval);
			var refreshDivVal = document.getElementById("inpRefreshRate").value;
			var timer = refreshDivVal * 1000;
			updateInterval = createInterval(timer);
			
			var notifDiv = document.getElementById("settingsNotif");
			notifDiv.innerHTML = "Rate set to: " + refreshDivVal;
		}
		function stopRefresh(){
			clearInterval(updateInterval);
			document.getElementById("inpRefreshRate").value = 0;
			
			var notifDiv = document.getElementById("settingsNotif");
			notifDiv.innerHTML = "Auto Update Terminated.";
		}
		function forceRefresh(){
			callServletForData();
		}
		
		function setTableBorderInterval(){
			clearInterval(tableBorderInterval);
			tableOpacity = 1;
			tableBorderInterval = setInterval(function() {
				var table = document.getElementById("responseTable");
				tableOpacity -= .005;
				if(tableOpacity > 0){
					table.style.border = "1px solid rgba(255, 255, 0, " + tableOpacity + ")";
				}
				else{
					clearInterval(tableBorderInterval);
				}
			}, 1);
		}
		
		function deleteTicketFunc(){
			var deleteForm = document.getElementById('frmDeleteTicket');
			var deleteTicketId = document.getElementById('deleteticketid');
			
			deleteTicketId.value = document.getElementById('ticketSelect').value;
			
			deleteForm.submit();
		}
		
		function autoArrange(){
			//simulate click on last names to arrange
			var curRow = document.getElementById("responseTable").rows[0].cells;
			curRow[2].click();
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
		
		// When the user clicks anywhere outside of the modal, close it
		window.onclick = function(event) {
		    if (event.target == document.getElementById('divSettings')) {
		    	document.getElementById('divSettings').style.display = "none";
		    }
		}
		
	</script>
</head>

<body onload="gatherResponseData(), setCounters(), autoArrange()">
	<H1>Emergency Notification System: Response Page</H1>
	<div id="imagefile">
		<img src="/images/logoibm.jpg" />
	</div><br>
	
	<div id="maindiv">
		<div id="subdiv1" style="position: relative">
			<form action="Servlet" method="post" style="position: inline; margin-top: 5px; margin-bottom: 0px" accept-charset="utf-8">
				<input type="hidden" value="databaseTable" name="requestType">
				<button id="database" type=submit>Database</button>
			</form>
			
			<button id="sendText" onclick="redirectToSendText()" style="position: inline; margin-top: 5px">Send Text</button>
			<button id="profile" onclick="redirectToProfile()" style="position: inline; margin-top: 5px">Profile</button>
			
			<form action="Servlet" method="post" style="position: inline; margin-top: 5px; margin-bottom: 0px" accept-charset="utf-8">
				<input type="hidden" value="databaseResponse" name="requestType">
				<input type="hidden" value="isUpdate" name="Yes">
				<button id="response">Responses</button>
			</form>
			
			<button id="profile" onclick="redirectToAddUsers()" style="position: inline; margin-top: 5px">Add Users</button>
			
			<form name="logoutfrm" id="logoutfrm" action="Servlet" method="post" style="position: absolute; bottom: 10px; left: 10px" accept-charset="utf-8">
				<input type="hidden" value="logout" name="requestType">
				<button id="logout">Log Out</button>
			</form>
		</div>
		
		<div id="divTicketSelect">
			<label style="color: white; margin-left: 5px;">Ticket: </label>
			<select id="ticketSelect" onchange="changeTicketTable(this);">
				
			</select>
			<!-- <label id="ticketDate" style="color: white; margin-left: 5px;"><strong>Date:</strong> </label>
			<label id="ticketText" style="color: white; margin-left: 5px;"><strong>Message:</strong> </label>-->
			
			<div style="float: right;">
				<input type="button" id="btnRefreshNow" name="btnRefreshNow" value="Update Now" onClick="forceRefresh();">
				<a id="settingsHover" title="Settings"><img id="settingsPic" src="/images/settings.png" onClick="openSettings()" /></a>
			</div>
			
			<div id="divSettings" class="settings">
				<div class="settings-content">
					<span class="close" onClick="closeSettings()">&times;</span>
					<div style="float: left;">
						<label style="color: white; margin-right: 5px;">Refresh Rate (Seconds): </label>
						<input style="width: 40px;" type="number" id="inpRefreshRate" name="inpRefreshRate" value="10">
						<input type="button" id="btnRefreshRate" name="btnRefreshRate" value="Change" onClick="changeRefreshRate();">
						<br/>
						<label id="settingsNotif"></label>
						<input type="button" id="btnStopRefresh" name="btnStopRefresh" value="Stop Auto Refresh" onClick="stopRefresh();">
					</div>
				</div>
			</div>
		</div>
		
		<div id="subdiv2">
			<div style="width: 100%;">
				<div style="width:20%" class="responseCounters">
					<label id="newCounter">New: </label>
				</div>
				<div style="width:20%" class="responseCounters">
					<label id="yesCounter">Yes: </label>
				</div>
				<div style="width:20%" class="responseCounters">
					<label id="noCounter">No: </label>
				</div>
				<div style="width:20%" class="responseCounters">
					<label id="nullCounter">Null: </label>
				</div>
				<div style="width:18%" class="responseCounters">
					<label id="totalCounter">Total Users: </label>
	      		</div>
																											
			</div>
			
			<script>
				var p2 = document.getElementById("Part2");
			
			</script>

			<TABLE BORDER="1" id="responseTable" class="tablesorter">
				<thead>
					<tr>
						<th style="width: 15%">FullName</th>
						<th style="width: 10%">Phone</th>
						<th style="width: 5%">Response</th>
						<th style="width: 50%">Message</th>
					</tr>
				</thead>
					
				<tbody id="tableBody"></tbody>
			</table>
			
			<script>
				var currentTicket;
				<c:forEach items="${ticketids}" var="tid" varStatus="ticketCounter">
					<c:if test="${ticketCounter.count == totalTickets}">
						currentTicket = "${tid[0]}";
					</c:if>
				</c:forEach>
				
				var body = document.getElementById("tableBody");
		        
		        <c:forEach items="${userResponseArray}" var="user">
		        	var row = "<TR onclick='backgroundSet(this)' name=" + currentTicket + ">";
		        	row += "<TD>" + "${user.getFullName()}" + "</TD>";
		        	row += "<TD>" + "${user.getPhoneNumber()}" + "</TD>";
		        	row += "<TD>" + "${user.getResponseType()}" + "</TD>";
		        	row += "<TD>" + "${user.getResponseText()}" + "</TD>";
		        	row += "</TR>";
		        	
		        	body.innerHTML += row;
		        	
				</c:forEach>
		        
				$(document).ready(function() {
					$("#responseTable").tablesorter();
				});
			</script>
			
		</div>
		
		<div style="width: 99%">
			<button id="btnExport" onclick="fnExcelReport();"> EXPORT </button>	
			<button id="btnDeleteTicket" onclick="deleteTicketFunc();">Delete Ticket</button>
		</div>
		
	</div>
	
	<form id="frmDeleteTicket" action="Servlet" method="post" accept-charset=utf-8>
		<input type="hidden" value="deleteTicket" name="requestType">
		<input type="hidden" value="" id="deleteticketid" name="deleteticketid">
	</form>
	
	
</body>

</html>