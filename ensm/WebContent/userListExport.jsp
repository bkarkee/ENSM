<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ page trimDirectiveWhitespaces="true" %>
<%
response.setContentType("application/vnd.ms-excel");
response.setHeader("Content-Disposition","inline; filename=ENSMUserListPage.xls");
%>
<%@ page import="java.io.*,java.util.*,java.sql.*"%>
<%@ page import="javax.servlet.http.*,javax.servlet.*" %>
<%@ page import="wasdev.sample.model.User"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/sql" prefix="sql"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
		<link rel="shortcut icon" type="image/x-icon" href="/images/favicon.png">
		<title>Insert title here</title>
	
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
	</head>
	
	<body>
	
		<sql:setDataSource var="snapshot" driver="org.postgresql.Driver"
	     url="jdbc:postgresql://jumbo.db.elephantsql.com:5432/qtfzuzju"
	     user="qtfzuzju"  password="tWv20srSWK_3LOJwwimggnWIZd50lLwB"/>
		
		<sql:query var="result" dataSource="${snapshot}">
		select firstname as a_First_Name,lastname as b_Last_Name,email as c_Email,phonenumber as d_Phone_Number,carrier as e_Carrier,confirmedphone as f_Confirmed_Phone, floor as g_floor, management as h_Management
		from users
		</sql:query>
		
		<table border="2px">
			<c:forEach items="${result.rows}" var="row" varStatus="count">
				<c:if test="${count.first}">
				<tr bgcolor='#87AFC6'>
				<c:forEach items="${row}" var="data" varStatus="loop">
					<th style="text-transform: capitalize"><c:out value="${fn:replace(fn:substringAfter(data.key, '_'), '_', ' ')}"/></th>
				</c:forEach>
				</tr>
				</c:if>
				<tr>
				<c:forEach items="${row}" var="data" varStatus="loop">
					<td><c:out value="${data.value}"/></td>
				</c:forEach>
				</tr>
			</c:forEach>
		</table> 
	</body>
</html>