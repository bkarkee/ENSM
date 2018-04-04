package wasdev.sample.methods;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import wasdev.sample.model.User;
import wasdev.sample.servlet.Servlet;

public class UserProfile {
	
	private UserProfile() { throw new IllegalStateException("UserProfile class"); }
	
	public static void signUpUserRequest(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		// Set data.
		String firstName = request.getParameter("firstname");
		String lastName = request.getParameter("lastname");
		String email = request.getParameter("email");
		String password = request.getParameter("password");
		String phonenumber = request.getParameter("phone");
		String carrier = request.getParameter("carrier");
		
		String status = signUpUser(firstName, lastName, email, password, phonenumber, carrier, 0, "", false);
		
		switch(status){
			case "db": response.sendRedirect("/register?notif=noDB"); break;
			case "duplicate": response.sendRedirect("/register?notif=duplicate"); break;
			case "error": response.sendRedirect("/register?notif=error"); break;
			case "success": request.getRequestDispatcher("/registerSuccess").forward(request, response); break;
			default: response.sendRedirect("/register?notif=error"); break;
		}
		//stop here
		
		
	}
	
	public static String signUpUser(String firstName, String lastName, String email, 
									 String password, String phonenumber, String carrier, 
									 int floor, String management, boolean mngEntered){
		
		String status = "error";
		String authCode = UUID.randomUUID().toString();
		PreparedStatement pst = null;
		ResultSet rs = null;
		
		if(phonenumber.contains("-")){
			String charToDel = "<>[],-()";
			String pattern = "[" + Pattern.quote(charToDel) + "]";
			phonenumber = phonenumber.replaceAll(pattern, "").trim().replace(" ","");
		}
		
		
		// Test database connection.
		Connection db = DatabaseConnection.ConnectDB();
		if (db == null) {
			Logger.writeToErrorLogs("No Database Connection");
			return "db";
		}
		
		boolean userExists = false;
		try {
			// SQL select statement for all email addresses in table users & activation.
			pst = db.prepareStatement(ApplicationConstants.CHECK_EXISTING_USER);
			pst.setString(1, email);
			pst.setString(2, email);
			rs = pst.executeQuery();
			
			if(rs.next()){
				userExists = rs.getBoolean("exists");
			}
		} catch(SQLException e){
			e.printStackTrace();
		} finally {
			SQLCloser.close(pst);
			SQLCloser.close(rs);
		}
		
		if (!userExists) {
			System.out.println("Duplicate email address found! (user sign-up)");
			SQLCloser.close(db);
			return "duplicate";
		}
		
		try{
			// Prepare data encryption.
			byte[] hpwd = AuthenticateUserPassword.getEncryptedVariable(password);

			// SQL insert statement to add register data into table
			// users.
			pst = db.prepareStatement(ApplicationConstants.INSERT_NEW_USER_ACTIVATION);
			pst.setString(1, email);
			pst.setBytes(2, hpwd);
			pst.setString(3, firstName);
			pst.setString(4, lastName);
			pst.setString(5, phonenumber);
			pst.setString(6, carrier);
			pst.setString(7, authCode);
			pst.executeUpdate();
			
			String url = ApplicationConstants.PRODURL;

			// Prepare urls
			String url1 = Servlet.shorturl(url + "/activate?email=" + email + "&acode=" + authCode);
			String url2 = Servlet.shorturl(url + "delete.jsp?email=" + email + "&acode=" + authCode);

			//register email confirmation message.
			String recipient = email;
			String subject = " ENSM Account Created!";
			String body = "";
			if(mngEntered){
				body = firstName + "," + "<br/><br/>"
					+ "   You have been registered for the Emergency Notification System for Monroe by management.<br/> "
					+ "Please click this <a href='" + url1 + "'>Activate Link</a> to activate your account.<br/>"
					+ "Your temporary password is 'ibm@monroe'. After activation, please log in and change your password.<br/>"
					+ "Please also note that your password on this site is currently seperate from your W3 log in.<br/>";
			}
			else{
				body = firstName + "," + "<br/><br/>"
						+ "   Thank you for registering for the Emergency Notification System for Monroe.<br/> "
						+ "Please click this <a href='" + url1 + "'>Activate Link</a> to activate your account.<br/>"
						+ "<strong>If you did not signup for this service</strong>, please "
						+ "click this <a href='" + url2 + "'>Remove Link</a> and <strong>delete this email</strong>.";
			}
			
			SendEmail.sendEmailHTML(recipient, subject, body);

			// Forward user to register success page.
			status = "success";
			
			// Close SQL connection and commit changes.
			db.commit();

		} catch (Exception e) {
			e.printStackTrace();
			status = "error";
		} finally {
			SQLCloser.close(rs);
			SQLCloser.close(pst);
			SQLCloser.close(db);
		}
		return status;
	}
	
	public static void userActivate(HttpServletRequest request, HttpServletResponse response) throws IOException {
		// Set Data.
		String email = request.getParameter("email");
		String authCode = request.getParameter("acode");
		String dbPnumber = "";
		String dbCarrier = "";
		PreparedStatement pst = null;
		ResultSet rs = null;

		// Test database connection.
		Connection db = DatabaseConnection.ConnectDB();
		if (db == null) {
			Logger.writeToErrorLogs("No Database Connection");
			response.sendRedirect("/login");
			return;
		}

		try {
			System.out.println("find activation.");
			// SQL select statement to grab user activation code.
			pst = db.prepareStatement(ApplicationConstants.SELECT_ALL_ACTIVATION_WHERE_EMAIL);
			pst.setString(1, email);
			pst.setString(2, authCode);
			rs = pst.executeQuery();
			
			if (rs.next()) {
				dbPnumber = rs.getString("phonenumber");
				dbCarrier = rs.getString("carrier");
			}
			else{
				System.out.println("couldnt find user.");
				response.sendRedirect("/activateFailure");
				
				System.out.println("No activation code in database found!");
			}
			
		} catch(SQLException e){
			e.printStackTrace();
		} finally{
			SQLCloser.close(rs);
			SQLCloser.close(pst);
		}
		
		if(dbPnumber.equals("") || dbCarrier.equals("")){
			System.out.println(dbPnumber + ", " + dbCarrier + " Failure.");
			SQLCloser.close(db);
			return;
		}
		
		try{
			System.out.println("Start activation to user.");
			// Copy account over to the "active" user list.
			pst = db.prepareStatement(ApplicationConstants.INSERT_FROM_ACTIVATION_TO_USERS);
			pst.setString(1, email);
			pst.executeUpdate();
			db.commit();
		} catch(SQLException e){
			e.printStackTrace();
		} finally {
			SQLCloser.close(pst);
		}
		

		try{
			System.out.println("Start activation to login.");
			// Copy account over to the "active" user list.
			pst = db.prepareStatement(ApplicationConstants.INSERT_FROM_ACTIVATION_TO_LOGIN);
			pst.setString(1, email);
			pst.executeUpdate();
			db.commit();
		} catch(SQLException e){
			e.printStackTrace();
		} finally {
			SQLCloser.close(pst);
		}
		
		try{
			System.out.println("delete from activation.");
			// Remove account from "non-active" user list.
			pst = db.prepareStatement(ApplicationConstants.DELETE_FROM_ACTIVATION_WHERE_EMAIL);
			pst.setString(1, email);
			pst.executeUpdate();
			db.commit();
		} catch(SQLException e){
			e.printStackTrace();
		} finally {
			SQLCloser.close(pst);
		}
		
		try{
			System.out.println("passwordchange add.");
			// add to password change list.
			pst = db.prepareStatement(ApplicationConstants.INSERT_TO_PASSWORDCHANGE);
			pst.setString(1, email);
			pst.executeUpdate();
			db.commit();
		} catch(SQLException e){
			e.printStackTrace();
		} finally {
			SQLCloser.close(pst);
		}

		// Prepare confirmation sms message.
		String recipient = AdminPage.getAddressOfCarrier(db, dbPnumber, dbCarrier);

		String subject = " ENSM: Verify Your Phone";
		String body = " Reply with 'Activate' to verify your number\n"
			 + "or \"DeleteMe\" if this was sent incorrectly.";
		
		SendEmail.sendEmail(recipient.trim(), subject, body);
		
		SQLCloser.close(db);

		// Redirect user to login page.
		response.sendRedirect("/activateSuccess");
		
	}
	
	public static void updateUserProfile(HttpServletRequest request, HttpServletResponse response){
		String firstName = request.getParameter("fname");
		String lastName = request.getParameter("lname");
		String email = request.getParameter("email");
		String phone = request.getParameter("phone").replaceAll("\\D+", "");
		String carrier = request.getParameter("carrier");
		boolean phoneChanged = false;
		PreparedStatement pst = null;
		ResultSet rs = null;

		HttpSession session = request.getSession(true);
		User user = (User) session.getAttribute("user");
		if(user == null){
			try {
				response.sendRedirect("/login?token=timeout");
				return;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		Connection db = DatabaseConnection.ConnectDB();
		if (db == null) {
			try {
				if(!user.isManagement()){response.sendRedirect("/userProfile?token=noDB");}
				else{response.sendRedirect("/adminPage?token=pr&token2=noDB");}
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			System.out.println("No Database Connection");
			return;
		}
		
		try{
			pst = db.prepareStatement(ApplicationConstants.SELECT_ALL_USERS_WHERE_EMAIL);
			pst.setString(1, email);
			rs = pst.executeQuery();
			
			if(rs.next()){
				String dbPhone = rs.getString("phonenumber");
				String dbCarrier = rs.getString("carrier");
				
				if(!dbPhone.trim().equals(phone.trim()) ||
				   !dbCarrier.trim().equals(carrier.trim())){
					phoneChanged = true;
				}
			}
		} catch(SQLException e){
			e.printStackTrace();
		} finally {
			SQLCloser.close(rs);
			SQLCloser.close(pst);
		}

		String statement = null;
		
		try {
			// updating the db with new values
			if(phoneChanged){
				statement = ApplicationConstants.UPDATE_USER_INFO_WITH_PHONE;
				
				// Prepare confirmation sms message.
				String recipient = AdminPage.getAddressOfCarrier(db, phone, carrier);

				String subject = " ENSM: Verify Your Phone";
				String body = " Reply with 'Activate' to verify your number\n"
					 + "or \"DeleteMe\" if this was sent incorrectly.";
				
				SendEmail.sendEmail(recipient.trim(), subject, body);
			}
			else{
				statement = ApplicationConstants.UPDATE_USER_INFO;
			}
			
			pst = db.prepareStatement(statement);
			pst.setString(1, firstName);
			pst.setString(2, lastName);
			pst.setString(3, phone);
			pst.setString(4, carrier);
			pst.setString(5, email);
			pst.executeUpdate();

			if(user != null){
				user.setFirstname(firstName);
				user.setLastname(lastName);
				user.setPhonenumber(phone);
				user.setCarrier(carrier);
				user.setEmail(email);
	
				session.setAttribute("user", user);
				
				if (!user.isManagement()) {
					response.sendRedirect("/userProfile?token=success");
				} else {
					response.sendRedirect("/adminPage?token=pr&token2=success");
				}
			}

		} catch (SQLException | IOException e) {
			try {
				if (user != null && !user.isManagement()) {
					response.sendRedirect("/userProfile?token=err");
				} else {
					response.sendRedirect("/adminPage?token=pr&token2=err");
				}
			} catch (IOException ex) {
				ex.printStackTrace();
			}
			e.printStackTrace();
		}
		finally{
			SQLCloser.close(rs);
			SQLCloser.close(pst);
			SQLCloser.close(db);
		}
	}
	
	public static void changePasswordRequest(HttpServletRequest request, HttpServletResponse response) throws IOException {
		String email = request.getParameter("email");
		PreparedStatement pst = null;
		ResultSet rs = null;
		
		Connection db = DatabaseConnection.ConnectDB();
		if (db == null) {
			Logger.writeToErrorLogs("No Database Connection");
			response.sendRedirect("/login");
			return;
		}

		try {
			pst = db.prepareStatement(ApplicationConstants.SELECT_ALL_USERS_WHERE_EMAIL);
			pst.setString(1, email);

			rs = pst.executeQuery();

			// check resultset to see if it's empty
			if (rs.next()) {
				SQLCloser.close(rs);
				SQLCloser.close(pst);
				
				pst = db.prepareStatement(ApplicationConstants.UPDATE_USER_PASSWORDCHANGE);
				pst.setString(1, email);

				pst.executeUpdate();
				db.commit();
				SQLCloser.close(pst);

				pst = db.prepareStatement(ApplicationConstants.SELECT_UUID_PASSWORDCHANGE);
				pst.setString(1, email);

				rs = pst.executeQuery();
				
				if(rs.next()){
					String uid = rs.getString("uid");
					
					HttpSession session = request.getSession(true);
					String sessionEnvironment = (String) session.getAttribute(ApplicationConstants.SESSIONENV);
					
					String url = "";
					if(sessionEnvironment.equals("prod")){url = ApplicationConstants.PRODURL;}
					else{url = ApplicationConstants.TESTURL;}
					
					String link = url + "Servlet?requestType=changePassword&email=" + email + "&uid=" + uid;
					
					String subject = " IBM ENSM password change request";
					String body = "This email was sent by the Emergency Notification System in "
						+ "response to a request to change your password.<br>"
						+ "To change your password and access your account, click on "
						+ "<a href='" + link + "'>this link.</a><br><br>"
						+ "<strong>This link will expire in 30 minutes.</strong><br><br>"
						+ "If you did not request a password change, please ignore this email.<br>"
						+ "<strong>If you receive multiple change requests, notify your Manager.</strong>";
					
					SendEmail.sendEmailHTML(email, subject, body);
					System.out.println("Sent password recovery message successfully");
	
					response.sendRedirect("/result");
				}
			}

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			SQLCloser.close(rs);
			SQLCloser.close(pst);
			SQLCloser.close(db);
		}
	}
	
	public static void validateUserPasswordChange(HttpServletRequest request, HttpServletResponse response) {
		String email = request.getParameter("email");
		String uid = request.getParameter("uid");

		Connection db = DatabaseConnection.ConnectDB();

		if (db == null) {
			Logger.writeToErrorLogs("No Database Connection");
			try {
				response.sendRedirect("/login");
				return;
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
		PreparedStatement pst = null;
		ResultSet rs = null;

		try{
			pst = db.prepareStatement(ApplicationConstants.SELECT_USER_PASSWORDCHANGE);
			pst.setString(1, email);
			pst.setString(2, uid);
			rs = pst.executeQuery();

			if(rs.next()) {
				HttpSession session = request.getSession(true);
				session.setAttribute("passToken", "confirmed");
				session.setAttribute("email", email);
				
				response.sendRedirect("/changePassword");
				System.out.println("validated user for password change");
			}
			else{
				response.sendRedirect("/login");
				System.out.println("failed to validate user for password change");
			}
		} 
		catch (SQLException e) {
			e.printStackTrace();
		} 
		catch (Exception e) {
			e.printStackTrace();
		}
		finally{
			SQLCloser.close(rs);
			SQLCloser.close(pst);
			SQLCloser.close(db);
		}

	}
	
	public static boolean executePasswordChange(String newPass, String confirmPass, String email) {
		System.out.println("user: " + email + " changing password.");
		boolean completedTask = false;
		PreparedStatement pst = null;
		ResultSet rs = null;
		
		Connection db = DatabaseConnection.ConnectDB();

		if (db == null) {
			Logger.writeToErrorLogs("No Database Connection (Change Password)");
			return completedTask;
		}
		else{
			if (newPass.equals(confirmPass)) {
				try {
					byte[] hpwd = AuthenticateUserPassword.getEncryptedVariable(newPass);
	
					pst = db.prepareStatement(ApplicationConstants.UPDATE_PASSWORD_WHERE_EMAIL);
					pst.setBytes(1, hpwd);
					pst.setString(2, email);
					pst.executeUpdate();

					db.commit();
					completedTask = true;
					
				} catch (Exception e) {
					e.printStackTrace();
				} finally{
					SQLCloser.close(rs);
					SQLCloser.close(pst);
					SQLCloser.close(db);
				}
			} else {
				System.out.println("password does not match");
			}
		}
		return completedTask;
	}
	
	// Deletes the user's pending activation account. Method intended to be
	// activated via a hyperlink cick.
	public static void deleteUser(HttpServletRequest request, HttpServletResponse response) throws IOException {
		String aCode = request.getParameter("acode");
		String email = request.getParameter("email");
		String dbFirstName = null;
		String dbacode = null;
		PreparedStatement pst = null;
		ResultSet rs = null;

		// Test database connection.
		Connection db = DatabaseConnection.ConnectDB();
		if (db == null) {
			Logger.writeToErrorLogs("No Database Connection");
			response.sendRedirect("/login");
		}

		try {
			// SQL select statement to grab user activation code.
			pst = db.prepareStatement(ApplicationConstants.SELECT_ALL_ACTIVATION_WHERE_EMAIL);
			pst.setString(1, email);
			rs = pst.executeQuery();
		} catch(SQLException e){
			e.printStackTrace();
		} finally {
			SQLCloser.close(pst);
		}

		try{
			// Retrieve data from database.
			if (rs != null && rs.next()) {
				dbFirstName = rs.getString("firstname").trim();
				dbacode = rs.getString("acode");
			} else {
				String eol = System.getProperty("line.separator");
				System.out.println("No user found in database." + eol + "Email: " + email);
				response.sendRedirect("/login");
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			SQLCloser.close(rs);
		}
		
		try{
			// Check for activation code match and deletes from activation
			// table.
			if (aCode.equalsIgnoreCase(dbacode)) {
				pst = db.prepareStatement(ApplicationConstants.DELETE_FROM_ACTIVATION_WHERE_EMAIL);
				pst.setString(1, email);
				pst.executeUpdate();
	
				// Prepare email message.
				String recipient = email;
				String subject = "Account removed";
				String eol = System.getProperty("line.separator");
				String body = dbFirstName + "," + eol + "Your account for emergency notifications has been removed."
						+ eol + "You will not receive anymore messages.";
				SendEmail.sendEmail(recipient, subject, body);
	
				// Redirect user to login page.
				response.sendRedirect("/login");
				
				db.commit();
			} 
			else {
				String eol = System.getProperty("line.separator");
				System.out.println("No matching activation code found in database." + eol + "Email: " + email);
				response.sendRedirect("/login");
			}
		} catch (SQLException e){
			e.printStackTrace();
		} finally {
			SQLCloser.close(pst);
			SQLCloser.close(db);
		}
	}
}
