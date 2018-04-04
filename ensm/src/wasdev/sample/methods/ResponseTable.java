package wasdev.sample.methods;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import wasdev.sample.model.UserResponses;

public class ResponseTable {
	
	private ResponseTable() { throw new IllegalStateException("ResponseTable class"); }
	
	@SuppressWarnings("unchecked") //this is because of List<User> cast.
	public static void gatherResponseData(HttpServletRequest request, HttpServletResponse response) {
		List<UserResponses> users = new ArrayList<>();
		List<UserResponses> userResponseArray = new ArrayList<>();
		List<UserResponses> userUpdatedResponse = new ArrayList<>();
		HashMap<String, String> ticketMap = new HashMap<>();
		PreparedStatement pst = null;
		ResultSet rs = null;
		String isUpdate = null;
		List<String[]> tickets = null;

		HttpSession session = request.getSession(true);
		if(request.getParameter("update") != null){
			isUpdate = (String) request.getParameter("update");
			if(session.getAttribute("userResponseArray") != null){
				userResponseArray = (List<UserResponses>) session.getAttribute("userResponseArray"); //here
			}
		}
		else{
			session.setAttribute("userResponseArray", null);
		}
		
		// setting up the connection
		Connection db = DatabaseConnection.ConnectDB();
		
		try {
			if(db == null){
				response.sendRedirect("/adminPage?token=st");
				return;
			}
			
			pst = db.prepareStatement(ApplicationConstants.SELECT_ALL_RESPONSE_TABLE);
			rs = pst.executeQuery();
			
			while (rs.next()) {
				UserResponses userDBObject = new UserResponses();

				userDBObject.setEmail(rs.getString("email"));
				
				users.add(userDBObject);
			}
			rs.close();
			pst.close();
			
			for(UserResponses u: users){
				pst = db.prepareStatement(ApplicationConstants.SELECT_ALL_USERS_WHERE_EMAIL);
				pst.setString(1, u.getEmail());
				rs = pst.executeQuery();
				
				if(rs.next()) {
					String dbFullname = rs.getString("firstname").trim() + " " + rs.getString("lastname").trim();
					String dbPnumber = rs.getString("phonenumber").trim();
	
					u.setFullName(dbFullname);
					u.setPhoneNumber(dbPnumber);
				}
				rs.close();
				pst.close();
			}
		} catch(SQLException e){
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			SQLCloser.close(rs);
			SQLCloser.close(pst);
		}

		for(UserResponses u: users){
			try{
				pst = db.prepareStatement(ApplicationConstants.SELECT_USER_FROM_USERTEXTS_TABLE);
				pst.setString(1, u.getEmail());
				pst.setString(2, ApplicationConstants.getCurrentTicketID());
				rs = pst.executeQuery();
				
				while(rs.next()){
					String message = rs.getString("date") + " " + rs.getString("text");
					
					//capture keywords
					if (message.toLowerCase().contains("yes")) {
						u.setResponseType("Yes");
					} else if (message.toLowerCase().contains("no")) {
						u.setResponseType("No");
					}
					
					if(u.getResponseText() == null || u.getResponseText().equals("")){
						u.setResponseText(message);
					}
					else{
						u.setResponseText(u.getResponseText() + "<br/>" + message);
					}
				}
			} catch(SQLException e){
				e.printStackTrace();
			} finally {
				SQLCloser.close(rs);
				SQLCloser.close(pst);
			}
			
			if(isUpdate != null){
				for(UserResponses existingUser: userResponseArray){
					if(u.getFullName().equals(existingUser.getFullName())){
						if(u.getResponseText() != null &&
						!u.getResponseText().equals(existingUser.getResponseText())) {
							userUpdatedResponse.add(u);
							existingUser.setResponseType(u.getResponseType());
							existingUser.setResponseText(u.getResponseText());
						}
						break;
					}
				}
			}
			else{
				userResponseArray.add(u);
				tickets = gatherTickets(request, db);
			}
		}

		if(tickets != null){
			ticketMap.put(ApplicationConstants.getCurrentTicketID(), "loaded");
			for(String[] t: tickets){
				if(!t[0].equals(ApplicationConstants.getCurrentTicketID()) ){
					ticketMap.put(t[0], "notloaded");
				}
			}
			session.setAttribute("ticketMap", ticketMap);
			session.setAttribute("ticketids", tickets);
			session.setAttribute("userResponseArray", userResponseArray);
		}
		
		try{
			if(isUpdate != null){
				writeToResponse(response, userUpdatedResponse);
			}
			else{
				response.sendRedirect("/textResponsePage");
			}
		} catch(IOException e){
			e.printStackTrace();
		} finally {
			SQLCloser.close(db);
		}
	}
	
	public static void gatherPastTicket(HttpServletRequest request, HttpServletResponse response) {
		List<UserResponses> users = new ArrayList<UserResponses>();
		PreparedStatement pst = null;
		ResultSet rs = null;
		String ticketID = null;

		HttpSession session = request.getSession(true);
		
		//check to be sure that we are grabbing a hashmap
		if(!(((Object) session.getAttribute("ticketMap")) instanceof HashMap<?, ?>)){return;}
		
		@SuppressWarnings("unchecked") //must suppress of we will get warnings (Unavoidable)
		HashMap<String, String> ticketMap = (HashMap<String, String>) session.getAttribute("ticketMap");
		
		if(request.getParameter("tid") != null){
			ticketID = (String) request.getParameter("tid");
			System.out.println(ticketID);
		}
		else{
			System.out.println("failed to get ticket id");
			return;
		}
		
		// setting up the connection
		Connection db = DatabaseConnection.ConnectDB();
		
		try {
			if(db == null){
				response.sendRedirect("/adminPage?token=st");
				return;
			}
			
			pst = db.prepareStatement(ApplicationConstants.SELECT_USER_FROM_PAST_USERTEXTS_TABLE);
			pst.setString(1, ticketID);
			rs = pst.executeQuery();
			
			while(rs.next()){
				String email = rs.getString("email");
				boolean found = false;
				if(users.size() > 0){
					for(UserResponses u: users){
						if(email.equals(u.getEmail())){
							System.out.println("existing user");
							extractUserMessage(u, rs);
							found = true;
						}
						if(!found){
							System.out.println("new user");
							UserResponses user = new UserResponses();
							user.setEmail(email);
							extractUserMessage(user, rs);
							users.add(user);
							break;
						}
					}
				}
				else{
					System.out.println("users was empty");
					UserResponses user = new UserResponses();
					user.setEmail(email);
					extractUserMessage(user, rs);
					users.add(user);
				}
			}
		} catch(SQLException e){
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			SQLCloser.close(pst);
		}

		try {
			if(users.size() > 0){
				for(UserResponses u: users){
					pst = db.prepareStatement(ApplicationConstants.SELECT_ALL_USERS_WHERE_EMAIL);
					pst.setString(1, u.getEmail());
					rs = pst.executeQuery();
					
					if(rs.next()) {
						String dbFullname = rs.getString("firstname").trim() + " " + rs.getString("lastname").trim();
						String dbPnumber = rs.getString("phonenumber").trim();
	
						System.out.println(dbFullname);
						u.setFullName(dbFullname);
						u.setPhoneNumber(dbPnumber);
					}
					pst.close();
				}
			    
			    ticketMap.put(ticketID, "loaded");
			    
			    writeTableHTML(response, users, ticketID);
				
				session.setAttribute("ticketMap", ticketMap);
			}
			
		} catch(SQLException e){
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			SQLCloser.close(rs);
			SQLCloser.close(pst);
			SQLCloser.close(db);
		}
	}
	
	private static List<String[]> gatherTickets(HttpServletRequest request, Connection db){
		PreparedStatement pst = null;
		ResultSet rs = null;
		List<String[]> tickets = new ArrayList<String[]>();
		
	
		try{
			pst = db.prepareStatement(ApplicationConstants.SELECT_TICKET_IDS);
			rs = pst.executeQuery();
			
			while(rs.next()){
				
				Date d = new Date(rs.getTimestamp("date").getTime());

				//add date timestamp
				String dateString = "[ " + ApplicationConstants.convertTimeToMonroe(d) + " ] ";
				
				String text = rs.getString("text").replaceAll("\\r?\\n", " ");
				
				text = text.replaceAll(String.valueOf('"'), "`");
				
				String ticketID[] = {rs.getString("ticketid"), dateString, text};
				tickets.add(ticketID);
			}
			
			HttpSession session = request.getSession(true);
			session.setAttribute("ticketids", tickets);
			
			
		} catch(SQLException e){
			e.printStackTrace();
		} finally {
			SQLCloser.close(rs);
			SQLCloser.close(pst);
		}
		
		return tickets;
	}
	
	private static void writeToResponse(HttpServletResponse response, List<UserResponses> userUpdatedResponse) throws IOException{
		response.setContentType("text/plain");
	    PrintWriter out = response.getWriter();
	    
	    if(userUpdatedResponse.size() > 0){
		    for(UserResponses u: userUpdatedResponse){
		    	out.print("FN$" + u.getFullName() + "$" + u.getResponseType()+ "$" + u.getResponseText() + "$%");
			    out.flush();
		    }
	    }
	    else{
	    	out.print("NA%");
		    out.flush();
	    };
	    
	    out.close();
	}
	
	private static void writeTableHTML(HttpServletResponse response, List<UserResponses> userUpdatedResponse, String ticket) throws IOException{
		response.setContentType("text/plain");
	    PrintWriter out = response.getWriter();
	    
	    if(userUpdatedResponse.size() > 0){
		    for(UserResponses u: userUpdatedResponse){
		    	out.print("<TR onclick=backgroundSet(this) name='" + ticket + "'>");
		    	out.print("<TD>" + u.getFullName() + "</TD>");
		    	out.print("<TD>" + u.getPhoneNumber() + "</TD>");
		    	out.print("<TD>" + u.getResponseType() + "</TD>");
		    	out.print("<TD>" + u.getResponseText() + "</TD>");
		    	out.print("</TR>");
			    out.flush();
		    }
	    }
	    else{
	    	out.print("<TR name='" + ticket + "'>");
	    	out.print("<TD>Empty</TD>");
	    	out.print("<TD>User</TD>");
	    	out.print("<TD>Responses</TD>");
	    	out.print("<TD>In DB</TD>");
	    	out.print("</TR>");
		    out.flush();
	    };
	    
	    out.close();
	}
	
	private static void extractUserMessage(UserResponses user, ResultSet rs) throws SQLException{
		String message = rs.getString("date") + " " + rs.getString("text");
		System.out.println(message + rs.getString("ticketid"));
		
		//capture keywords
		if (message.toLowerCase().contains("yes")) {
			user.setResponseType("Yes");
		} else if (message.toLowerCase().contains("no")) {
			user.setResponseType("No");
		}
		
		if(user.getResponseText() == null || user.getResponseText().equals("")){
			user.setResponseText(message);
		}
		else{
			user.setResponseText(user.getResponseText() + "<br/>" + message);
		}
	}
	
	public static boolean deleteTicket(String tid) {
		System.out.println("Deleting ticket: " + tid);
		
		boolean completedTask = false;
		PreparedStatement pst = null;
		
		Connection db = DatabaseConnection.ConnectDB();

		if (db == null) {
			System.err.println("No Database Connection");
			return completedTask;
		}
		
		if(tid == ApplicationConstants.getCurrentTicketID()){
			try{
				pst = db.prepareStatement(ApplicationConstants.DELETE_ALL_FROM_RESPONSE_TABLE);
				pst.executeUpdate();
				db.commit();
				completedTask = true;
				
			} catch (SQLException e) {
				e.printStackTrace();
				completedTask = false;
			} finally{
				SQLCloser.close(pst);
			}
		}
		

		try{
			pst = db.prepareStatement(ApplicationConstants.DELETE_TICKETID);
			pst.setString(1, tid);
			pst.executeUpdate();
			db.commit();
			completedTask = true;
			
		} catch (SQLException e) {
			e.printStackTrace();
			completedTask = false;
		} finally{
			SQLCloser.close(pst);
		}
		
		try{
			pst = db.prepareStatement(ApplicationConstants.DELETE_ALL_FROM_USERTEXTS_WHERE_TICKETID);
			pst.setString(1, tid);
			pst.executeUpdate();
			db.commit();
			completedTask = true;
			
		} catch (SQLException e) {
			e.printStackTrace();
			completedTask = false;
		} finally{
			SQLCloser.close(pst);
			SQLCloser.close(db);
		}
		
		return completedTask;
	} 
}
