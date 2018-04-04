package wasdev.sample.methods;


import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Random;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class AdminPage {
	
	private AdminPage() { throw new IllegalStateException("AdminPage class"); }
	
	public static String sendMassText(String sendTo, String subject, String bodyText){
		String completedTask = "";
		ArrayList<String[]> tempRecipients = new ArrayList<>();
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		if(subject.equals("")){ return "SBJ";}
		else if(bodyText.contentEquals("")){ return "BDY";}
		else if(sendTo.contentEquals("")){ return "GRP";}
		printMassTextToLogs(sendTo, subject, bodyText);
		
		subject+= " ";
		bodyText += " ";
		
		Random r = new Random();
		StringBuilder ticketID = new StringBuilder();
		
		for(int i = 0; i < 5; i++){
			int i1 = r.nextInt(35);
			ticketID.append(ApplicationConstants.RANDOMID[i1]);
		}
		
		//connect to Database
		Connection db = DatabaseConnection.ConnectDB();
		if (db == null) {
			Logger.writeToErrorLogs("No Database Connection");
			return "DB";
		}

		try {
			switch (sendTo) {
				case "CIC":
					stmt = db.prepareStatement(ApplicationConstants.SELECT_ALL_CIC); break;
				case "Management":
					stmt = db.prepareStatement(ApplicationConstants.SELECT_ALL_MNG); break;
				default:
					db.close();
					return "GRP";
			}
			
			rs = stmt.executeQuery();

			// save recipients from result set.
			gatherFromResultSet(rs, db, tempRecipients);
			if (tempRecipients != null) {
				//clear the response table
				PreparedStatement pst = null;
				
				try {
					pst = db.prepareStatement(ApplicationConstants.DELETE_ALL_FROM_RESPONSE_TABLE);
					pst.executeUpdate();
				} 
				catch (SQLException e) {
					e.printStackTrace();
				}
				finally{
					if(pst != null && !pst.isClosed()){
						pst.close();
					}
				}

				//add date timestamp
				String dateString = "[" + ApplicationConstants.convertTimeToMonroe(null) + "] ";
				System.out.println("formatted date: " + dateString);
				
				LocalDate todayLocalDate = LocalDate.now( ZoneId.of( "America/Chicago" ) );
				
				System.out.println("Todaylocal: " + todayLocalDate.toString());

				Calendar cal = Calendar.getInstance();
				java.sql.Date datee = new java.sql.Date(cal.getTimeInMillis());
				System.out.println("sql date millis:" + datee);
				
				try {
					pst = db.prepareStatement(ApplicationConstants.INSERT_NEW_TICKET);
					pst.setString(1, ticketID.toString());
					pst.setDate(2, datee);
					pst.setString(3, bodyText);
					pst.executeUpdate();
					
					ApplicationConstants.setCurrentTicketID(ticketID.toString());
				} 
				catch (SQLException e) {
					e.printStackTrace();
				}
				finally{
					if(pst != null && !pst.isClosed()){
						pst.close();
					}
				}
				

				for (String[] names : tempRecipients) {
					PreparedStatement pstForFullName = null;

					try {
						pstForFullName = db.prepareStatement(ApplicationConstants.INSERT_USER_TO_TICKET_TABLE);
						pstForFullName.setString(1, names[1]);
						pstForFullName.executeUpdate();

					} catch (SQLException e) {
						e.printStackTrace();
					} finally{
						if(pstForFullName != null && !pstForFullName.isClosed()){
							pstForFullName.close();
						}
					}
				}
			}
			db.commit();
		} catch (SQLException e) {
			completedTask = "failed";
			e.printStackTrace();
		}
		finally{
			SQLCloser.close(rs);
			SQLCloser.close(stmt);
			SQLCloser.close(db);
		}

		if (tempRecipients != null) {
			// log in to g-mail
			String from = "IBMMonroeENS@gmail.com"; // change accordingly
			Session session = ConnectToGmail.createGmailSession();
			
			MimeMessage messageText = null;
			
			try{
				messageText = new MimeMessage(session);
				
				messageText.setFrom(new InternetAddress(from, " ENSM@us.ibm.com"));
				messageText.setSubject(subject);
				
				//add recipients
				for (int i = 0; i < tempRecipients.size(); i++) {
					try {
						messageText.addRecipients(Message.RecipientType.TO, InternetAddress.parse(tempRecipients.get(i)[0]));
					} catch (MessagingException e) {
						e.printStackTrace();
					}
				}

				messageText.setText(bodyText);
				Transport.send(messageText);
				
				System.out.println("Sent message successfully: " + subject + "\n" +
										"to " + tempRecipients.size() + " users.");
				completedTask = "sent";
			} catch(MessagingException ex){
				ex.printStackTrace();
				return "failed";
			} catch (UnsupportedEncodingException e1) {
				e1.printStackTrace();
				return "failed";
			}
		}
		return completedTask;
	}
	
	private static void gatherFromResultSet(ResultSet rs, Connection db, ArrayList<String[]> tempRecipients){
		try {
			while (rs.next()) {
				boolean duplicate = false;
				//gather db user info
				String dbPhoneNumber = rs.getString("phonenumber").trim();
				String dbCarrier = rs.getString("carrier").trim();
				String email = rs.getString("email").trim();

				//create user phone number
				String toPhoneNumber = getAddressOfCarrier(db, dbPhoneNumber, dbCarrier);
				
				//check if phone number is already in list
				for (String[] temp : tempRecipients) {
					if (temp[0].equals(toPhoneNumber)) {
						duplicate = true;
						break;
					}

				}
				//if its not in list, add it to the list
				if (!duplicate) {
					String[] newReceiver = {toPhoneNumber, email};
					tempRecipients.add(newReceiver);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static String getAddressOfCarrier(Connection db, String userPhonenumber, String userCarrier) {
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			pst = db.prepareStatement(ApplicationConstants.SELECT_PHONE_CARRIER);
			pst.setString(1, userCarrier.trim());
			rs = pst.executeQuery();
			
			if (rs.next()) {
				String _addrs = rs.getString("phoneaddress").trim();
				userPhonenumber += _addrs;
			}
		}
		catch(Exception e){
			e.printStackTrace();
		}
		finally{
			try{
				rs.close();
			} catch(SQLException se){
				se.printStackTrace();
			}
			
			try{
				pst.close();
			} catch(SQLException se){
				se.printStackTrace();
			}
		}

		return userPhonenumber;
	} 
	
	private static void printMassTextToLogs(String sendTo, String subject, String bodyText){
		System.out.println("Emergency Text" + "\n" +
				"-------------" + "\n" +
				"to: " + sendTo + "\n" +
				"subject: " + subject + "\n" +
				"body: " + bodyText);
	}
	
}
