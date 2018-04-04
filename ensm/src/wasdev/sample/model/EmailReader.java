package wasdev.sample.model;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.Date;
import java.util.Properties;

import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.Session;
import javax.mail.Store;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import wasdev.sample.methods.ApplicationConstants;
import wasdev.sample.methods.DatabaseConnection;
import wasdev.sample.methods.Logger;
import wasdev.sample.methods.SQLCloser;
import wasdev.sample.methods.SendEmail;

@WebListener("/Servlet")
public class EmailReader implements ServletContextListener{
	FetchMail fm = null;
	FetchMail oldFM = null;

	@Override
	public void contextInitialized(ServletContextEvent arg0) {
		fm = new FetchMail(this);
		fm.start();
		System.out.println("email reader class started!!");
		
		System.out.println("Setting initial ticketID!");
		
		gatherTicketId();
		
		//set salt
		ApplicationConstants.setAppSalt("=} ’œîÒW=+­ÛëÁ÷@3Æ-*Îõº|M' æôO".getBytes());
	}

	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
		fm.setRunning();
		System.out.println("email reader Shut Down!!");
	}
	
	public class FetchMail extends Thread {
		boolean running = true;
		EmailReader er = null;
		
		void setRunning(){running = false;}
		
		FetchMail(EmailReader er){
			this.er = er;
		}
		
		@Override
		public void run() {
			System.out.println("New Thread started.");
			do{

				try {
					Thread.sleep(10000);
				} catch (InterruptedException e) {
					e.printStackTrace();
					
					restartFetchMail();
					System.out.println("email reader class re-started from sleep t/c!!");
				}
				
				Session session = null;
				Store store = null;
				Folder emailInboxFolder = null;
				
				if(oldFM != null && oldFM.getState().toString().equals("TERMINATED")){
					oldFM = null;
					System.out.println("old thread terminated (...finally)");
				}
				
				try {
					//create email connection
					Properties props = System.getProperties();
					props.setProperty("mail.store.protocol", "imaps");
					session = Session.getDefaultInstance(props, null);
					
					store = session.getStore("imaps");
					
					store.connect("imap.gmail.com", "IBMMonroeENS@gmail.com", "5afeWord");
					
				} catch(Exception e){
					e.printStackTrace();
					try {
						if(store.isConnected()){
							store.close();
						}
					} catch (MessagingException e1) {
						e1.printStackTrace();
					}

					restartFetchMail();
					System.out.println("email reader class re-started from store connect t/c!!");
				}
					
				try{
					//open inbox folder
					if(store.isConnected()){
					    emailInboxFolder = store.getFolder("INBOX");
						emailInboxFolder.open(Folder.READ_WRITE);
					}
					else{
						continue;
					}
				} catch(Exception e){
					e.printStackTrace();

					try { if(emailInboxFolder != null && emailInboxFolder.isOpen()){ emailInboxFolder.close(true); } }
					catch (MessagingException ex) { ex.printStackTrace(); }
					try { store.close(); }
					catch (MessagingException exx) { exx.printStackTrace(); }
					
					restartFetchMail();
					System.out.println("email reader class re-started from folder open t/c!!");
				}
				
				try{
					Message messages[] = null;
					if(emailInboxFolder != null && emailInboxFolder.isOpen()){
						messages = emailInboxFolder.getMessages();
						
						for (int i = 0; i < messages.length; i++) {
							System.out.println("--------------------------" + "\n" +
													"MESSAGE #" + (i + 1) + ":");
		
							readMessage(messages[i]);

							messages[i].setFlag(Flags.Flag.DELETED, true);
						}
					}
					else{
						Logger.writeToErrorLogs("email folder wasnt open... \n");
					}
					
				} catch (Exception e) {
					e.printStackTrace();
				}
				finally{
					try {
						if(emailInboxFolder != null){ emailInboxFolder.close(true); }
					} catch (MessagingException e) {
						e.printStackTrace();
					}
					try {
						store.close();
					} catch (MessagingException e) {
						e.printStackTrace();
					}
				}
			}while(running);
			
			System.out.println("exited do/while loop..........");
		}
		
		private void restartFetchMail(){
			setRunning();
			er.oldFM = this;
			System.out.println("timer status: " + String.valueOf(running));
			er.fm = new FetchMail(er);
			er.fm.start();
			er.oldFM.interrupt();
		}

		public boolean readMessage(Message message) {
			Date date = null;
			boolean completedTask = false;
			
			//display to logs and gather the date received
			try {
				logEmailInfo(message);
				date = message.getSentDate();
			} catch (Exception e) {
				e.printStackTrace();
			}

			try{
				String smsAddress = "";
				smsAddress = message.getFrom()[0].toString();
				
				if(message.isMimeType("text/plain")) {
					System.out.println("This is plain text" + "\n" +
											"---------------------------");
	
					completedTask = unloadPlainText(message, smsAddress, date);
				}
				else if(message.isMimeType("multipart/*")) {
					System.out.println("This is a Multipart" + "\n" +
							"---------------------------");
					
					Multipart mmp = (Multipart) message.getContent();
					int count = mmp.getCount();
					for (int i = 0; i < count; i++){
						boolean completedMultipart = false;
						completedMultipart = unloadMultiPart(mmp.getBodyPart(i), smsAddress, date);
						if(completedMultipart){
							completedTask = true;
							break;
						}
					}
				}
			} catch(Exception e){
				e.printStackTrace();
			}
			
			return completedTask;
		}
		
		public boolean unloadPlainText(Message message, String smsAddress, Date date) {
			String text = "";
			String fullText = "";
			boolean completedTask = false;

			try {
				fullText = message.getContent().toString();
			} catch (IOException|MessagingException e) {
				e.printStackTrace();
			}
			
			if (fullText.contains("Original Message")) {
				//get start index for this line
				int substr = fullText.indexOf("----");
				
				//only keep the text up to that point
				fullText = fullText.substring(0, substr);
				fullText = fullText.trim();
			}
			
			//capture keywords
			if (fullText.toLowerCase().contains("yes")) {
				text = "Yes";
			} else if (fullText.toLowerCase().contains("no")) {
				text = "No";
			} else if (fullText.toLowerCase().contains("activate")) {
				text = "Confirm";
			} else if (fullText.toLowerCase().contains("delete")) {
				text = "Delete";
			} else {
				text = "Unknown";
			}
			//add date timestamp
			String dateString = "[" + ApplicationConstants.convertTimeToMonroe(date) + "] ";
			
			//add to logs

			System.out.println("Value: " + text + ", Body: " + fullText);
			
			
			switch (text) {
				case "Unknown":
				case "Yes":
				case "No":
					updateUserTicket(smsAddress, dateString, fullText);
					completedTask = true;
					break;
	
				case "Confirm":
					updateUserConfirmToken(smsAddress);
					completedTask = true;
					break;
	
				case "Delete":
					deleteUser(smsAddress);
					completedTask = true;
					break;
	
				default:
					System.out.println("No Valid Token: " + text +",    " + fullText);
					break;
			}
			
			return completedTask;
		}
		
		public boolean unloadMultiPart(Part bodyPart, String smsAddress, Date date) {
			String text = "";
			String fullText = "";
			boolean completedTask = false;
			
			//convert to object to check the instance of
			Object object = null;
			try {
				object = bodyPart.getContent();
			} catch (IOException|MessagingException e) {
				e.printStackTrace();
			} 

			if (object instanceof String) {
				//display original formatting to logs
				System.out.println("This is a string from multipart" + "\n" + 
						   "-------------------------------" + "\n" + "\n" +
						   "Before fix for user response:");
				
				fullText = object.toString();
				System.out.println("before: \n" + fullText);
				fullText = fullText.trim();
				String[] textLines = fullText.split("\\r\\n|\\n|\\r");
				fullText = "";
				for(int i = 0; i < textLines.length; i++){
					if(!textLines[i].contains("<")){
						textLines[i] = textLines[i].trim();
						System.out.println(textLines[i]);
						fullText += textLines[i];
					}
				}
				
				//if contains "sent by 'company', do not use
				if(fullText.contains("=====")){return false;}
				
				//display finalized text to logs
				System.out.println("full response: " + fullText);
				
				//capture keywords
				if (fullText.toLowerCase().contains("yes")) {
					text = "Yes";
				} else if (fullText.toLowerCase().contains("no")) {
					text = "No";
				} else if (fullText.toLowerCase().contains("activate")) {
					text = "Confirm";
				} else if (fullText.toLowerCase().contains("delete")) {
					text = "Delete";
				} else {
					text = "Unknown";
				}
			}
			else if (object instanceof InputStream) {
				System.out.println("This is just an input stream" + "\n" + 
						   "-------------------------------" + "\n" + "\n" +
						   "Possible image received. Skipping...");
				return false;
				/* possible image file received.
				 * 
				InputStream is = (InputStream) o;
				//here is where we read pictures
				BufferedImage image = ImageIO.read(is);
				String location = "C:/Users/IBM_ADMIN/Desktop/picture.png";
				File imageFile = new File(location);
				ImageIO.write(image, "png", imageFile);
				is.close();
				
				*/
			}
			else if(object instanceof Multipart){
				System.out.println("nested multipart" + "\n" + 
						   "-------------------------------");
				
				Multipart mmp = (Multipart) object;
				int count = 0;
				try {
					count = mmp.getCount();
				} catch (MessagingException e) {
					e.printStackTrace();
				}
				
				for (int i = 0; i < count; i++){
					boolean completedMultipart = false;
					try {
						completedMultipart = unloadMultiPart(mmp.getBodyPart(i), smsAddress, date);
					} catch (MessagingException e) {
						e.printStackTrace();
					}
					if(completedMultipart){
						completedTask = true;
					}
				}
				return completedTask;
			}
			else {
				System.out.println("This is an unknown type" + "\n" + 
						   "-------------------------------" + "\n" +
						   "Type is: " + object.toString());
				return false;
			}

			//add timestamp and display to logs
			if(fullText.trim().equals("")){return false;}
			String dateString = "[" + ApplicationConstants.convertTimeToMonroe(date) + "] ";
			
			switch (text) {
				case "Unknown":
				case "Yes":
				case "No":
					updateUserTicket(smsAddress, dateString, fullText);
					completedTask = true;
					break;
		
				case "Confirm":
					updateUserConfirmToken(smsAddress);
					completedTask = true;
					break;
		
				case "Delete":
					deleteUser(smsAddress);
					completedTask = true;
					break;
		
				default:
					System.out.print("did not answer: " + text +",    " + fullText);
					break;
	
			}

			return completedTask;
		}

		public void logEmailInfo(Message message) {
			//From
			try {
				System.out.println(message.getFrom()[0].toString());
			} catch (MessagingException e) {
				e.printStackTrace();
			}

			//Date
			Date d = null;
			try {
				d = message.getSentDate();
			} catch (MessagingException e) {
				e.printStackTrace();
			}
			
			System.out.println("SendDate: " + (d != null ? ApplicationConstants.convertTimeToMonroe(d) : "UNKNOWN"));
			
			//Message Type
			try {
				System.out.println(message.getContentType());
			} catch (MessagingException e) {
				e.printStackTrace();
			}

		}
		
		private void updateUserTicket(String smsAddress, String date, String fullText) {
			//Set Data.
			String phoneNumber = smsAddress.replaceAll("\\D+", "");
			if(phoneNumber.length() > 10){phoneNumber = phoneNumber.substring(1);} //if phone number contains area code, remove it
			String email = "";
			PreparedStatement pst = null;
			ResultSet rs = null;

			// Test database connection.
			Connection db = DatabaseConnection.ConnectDB();
			if (db == null) {
				Logger.writeToErrorLogs("No DB connection");
			}
			
			//Find phone number in ticket table.
			try {
				pst = db.prepareStatement(ApplicationConstants.SELECT_USER_BY_PHONE);
				pst.setString(1, phoneNumber);
				rs = pst.executeQuery();
				
				if (rs.next()) {
					email = rs.getString("email");
				}
				else{
					System.out.println("No phone number match found!.");
					return;
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				SQLCloser.close(rs);
				SQLCloser.close(pst);
			}
			
			//Find phone number in ticket table.
			try {
				pst = db.prepareStatement(ApplicationConstants.INSERT_TO_USERTEXTS);
				pst.setString(1, email);
				pst.setString(2, date);
				pst.setString(3, fullText);
				pst.setString(4, ApplicationConstants.getCurrentTicketID());
				pst.executeUpdate();
				db.commit();
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				SQLCloser.close(rs);
				SQLCloser.close(pst);
				SQLCloser.close(db);
			}
		}
		
		private void updateUserConfirmToken(String smsAddress) {
			String phoneNumber = smsAddress.replaceAll("\\D+", "");
			if(phoneNumber.length() > 10){phoneNumber = phoneNumber.substring(1);} //if phonenumber contains area code, remove it
			Boolean dbconfirmedPhone = null;
			PreparedStatement pst = null;
			ResultSet rs = null;

			// Test database connection.
			Connection db = DatabaseConnection.ConnectDB();
			if (db == null) {
				System.err.println("No Database Connection");
			}

			try {
				// SQL select statement to grab user activation code.
				pst = db.prepareStatement(ApplicationConstants.SELECT_USER_BY_PHONE); //check if they are a user
				pst.setString(1, phoneNumber);
				rs = pst.executeQuery();

				// Retrieve data from database.
				if (rs.next()) {
					dbconfirmedPhone = rs.getBoolean("confirmedphone");
					
					SQLCloser.close(pst);
					
					//if user hasn't confirmed their phone
					if (!dbconfirmedPhone) {
						pst = db.prepareStatement(ApplicationConstants.UPDATE_USER_CONFIRMED_PHONE);
						pst.setBoolean(1, true);
						pst.setString(2, phoneNumber);
						pst.executeUpdate();
						db.commit();

						// Prepare confirmation sms message.
						String subject = "Success!";
						String body = "Your mobile phone has been confirmed!";
						SendEmail.sendEmail(smsAddress, subject, body);
					}
				}
				else{
					String subject = "Failure!";
					String body = "Something went wrong, please try again or contact your manager.";
					SendEmail.sendEmail(smsAddress, subject, body);
				}

			} catch (Exception e) {
				e.printStackTrace();
			} 
			finally {
				SQLCloser.close(rs);
				SQLCloser.close(pst);
				SQLCloser.close(db);
			}
		}
		
		private void deleteUser(String smsAddress) {
			String phoneNumber = smsAddress.replaceAll("\\D+", "");
			if(phoneNumber.length() > 10){phoneNumber = phoneNumber.substring(1);} //remove if contains area code
			System.out.println(phoneNumber + " Is deleting their account!!!!!!");
			PreparedStatement pst = null;
			ResultSet rs = null;
			
			// Test database connection.
			Connection db = DatabaseConnection.ConnectDB();
			if (db == null) {
				System.err.println("No Database Connection");
			}

			try {
				// SQL select statement to grab user activation code.
				pst = db.prepareStatement(ApplicationConstants.SELECT_USER_BY_PHONE);
				pst.setString(1, phoneNumber);
				rs = pst.executeQuery();
				
				if(rs.next()){
					String _email = rs.getString("email");
					SQLCloser.close(pst);
					
					//delete user from user table
					pst = db.prepareStatement(ApplicationConstants.DELETE_FROM_USERS_WHERE_PHONENUMBER);
					pst.setString(1, phoneNumber);
					pst.executeUpdate();
					SQLCloser.close(pst);
					
					//delete user from passwordReset table
					pst = db.prepareStatement(ApplicationConstants.DELETE_FROM_PASSWORD_RESET_WHERE_EMAIL);
					pst.setString(1, _email);
					pst.executeUpdate();
					SQLCloser.close(pst);
					
					//delete user from response table
					pst = db.prepareStatement(ApplicationConstants.DELETE_FROM_RESPONSE_TABLE_WHERE_PHONENUMBER);
					pst.setString(1, phoneNumber);
					pst.executeUpdate();
					
					//send message letting user know they were removed
					String subject = "Success!";
					String body = "Your account has been removed.";
					SendEmail.sendEmail(smsAddress, subject, body);
				}
				else{
					String subject = "Failure!";
					String body = "Your account has not been removed, please try again or contact your manager.";
					SendEmail.sendEmail(smsAddress, subject, body);
				}
			} 
			catch (Exception e) {
				e.printStackTrace();
			} 
			finally {
				try {
					if(!rs.isClosed()){
						rs.close();
					}
					if(!pst.isClosed()){
						pst.close();
					}
					if (db != null){
						db.commit();
						db.close();
					}
				} catch (SQLException se) {
					se.printStackTrace();
				}
			}
		}

	}
	
	
	
	public void gatherTicketId(){
		PreparedStatement pst = null;
		ResultSet rs = null;
		
		// Test database connection.
		Connection db = DatabaseConnection.ConnectDB();
		if (db == null) {
			System.err.println("No Database Connection");
		}

		try {
			// SQL select statement to grab user activation code.
			pst = db.prepareStatement(ApplicationConstants.SELECT_STARTUP_TICKETID);
			rs = pst.executeQuery();
			
			if(rs.next()){
				ApplicationConstants.setCurrentTicketID(rs.getString("ticketid"));
				System.out.println(ApplicationConstants.getCurrentTicketID());
			}
			else{
				System.out.println("No ticketids stored!");
			}
		} 
		catch (Exception e) {
			e.printStackTrace();
		} 
		finally {
			SQLCloser.close(rs);
			SQLCloser.close(pst);
			SQLCloser.close(db);
			
		}
	}
	
}
