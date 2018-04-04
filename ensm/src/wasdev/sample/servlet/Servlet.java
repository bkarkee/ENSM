package wasdev.sample.servlet;

import wasdev.sample.methods.AddUsers;
import wasdev.sample.methods.AdminPage;
import wasdev.sample.methods.ApplicationConstants;
import wasdev.sample.methods.GatherCarriers;
import wasdev.sample.methods.Login;
import wasdev.sample.methods.ResponseTable;
import wasdev.sample.methods.UserProfile;
import wasdev.sample.methods.UserlistTable;
import wasdev.sample.model.EmailReader;
import wasdev.sample.model.User;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.io.IOException;

import javaQuery.j2ee.tinyURL;

@WebServlet("/Servlet")
public class Servlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static EmailReader emailReader = new EmailReader();

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException{
		String requestType = request.getParameter("requestType");
		requestType = (requestType != null) ? requestType : "";

		checkEnvironment(request);
		try{
			switch (requestType) {
				case "register":
					HttpSession session = request.getSession(true);
					session.setAttribute(ApplicationConstants.CARRIERARRAY, GatherCarriers.getPhoneCarriers());
					response.sendRedirect("/register");
				
					break;
					
				case "changePassword": UserProfile.validateUserPasswordChange(request, response); break;
				
				default: response.sendRedirect("/login"); break;
			}
		} catch(IOException e){
			e.printStackTrace();
		}
	}
  
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String requestType = request.getParameter("requestType");
		requestType = (requestType != null) ? requestType : "";
		
		checkEnvironment(request);

		try{
			switch (requestType) {
				case "addUsers": AddUsers.addUsers(request, response); break;
				case "change": passwordChange(request, response); break;
				case "deleteTicket": deleteTicket(request, response); break;
				case "databaseResponse": ResponseTable.gatherResponseData(request, response); break;
				case "databaseTable": UserlistTable.databaseTable(request, response); break;
				case "jspadmin": adminSendText(request, response); break;
				case "login": loginUser(request, response); break;
				case "loginSSO": loginUserSSO(request, response); break;
				case "logout": logoutUser(request, response); break;
				case "optoutUser": UserProfile.updateUserProfile(request, response); break;
				case "pastTicket": ResponseTable.gatherPastTicket(request, response); break;
				case "sendMail": UserProfile.changePasswordRequest(request, response); break;
				case "signup": UserProfile.signUpUserRequest(request, response); break;
				case "tableUpdate": UserlistTable.updateUserTable(request, response); break;
				case "userActivate": UserProfile.userActivate(request, response); break;
				case "userDelete": UserProfile.deleteUser(request, response); break;
				case "validateUserChangePassword": UserProfile.validateUserPasswordChange(request, response); break;
				default: System.out.println("No requestType specified!"); break;
			}
		} catch(IOException|ServletException e){
			e.printStackTrace();
		}
	}
	
	private void checkEnvironment(HttpServletRequest request){
		HttpSession session = request.getSession(true);
		String sessionEnvironment = (String) session.getAttribute(ApplicationConstants.SESSIONENV);
		
		if(sessionEnvironment == null){
			if(request.getRequestURL().toString().toLowerCase().contains("localhost")){
				session.setAttribute(ApplicationConstants.SESSIONENV, "test");
			}
			else{
				session.setAttribute(ApplicationConstants.SESSIONENV, "prod");
			}
		}
	}

	// google API URL SHORTNER
	public static String shorturl(String longurl) {
		return new tinyURL().getTinyURL(longurl);
	}

	// this is krishna's code
	private void adminSendText(HttpServletRequest request, HttpServletResponse response) throws IOException {
		//gather all parameter values
		String subject = request.getParameter("subject");
		String bodyText = request.getParameter("Message");
		String sendTo = request.getParameter("groupsToSend");
		
		String redirectPage = AdminPage.sendMassText(sendTo, subject, bodyText);
		
		String parameters = "&SBJ=" + subject + "&BDY=" + bodyText + "&GRP=" + sendTo;

		switch(redirectPage){
			case "SBJ": response.sendRedirect("/adminPage?token=st&sent=nosubject" + parameters); break;
			case "BDY": response.sendRedirect("/adminPage?token=st&sent=nobody" + parameters); break;
			case "DB": response.sendRedirect("/adminPage?token=st&sent=db" + parameters); break;
			case "GRP": response.sendRedirect("/adminPage?token=st&sent=nogroup" + parameters); break;
			case "failed": response.sendRedirect("/adminPage?token=st&sent=failed" + parameters); break;
			case "sent":
				//send user to response table
				ResponseTable.gatherResponseData(request, response);
				
				break; 
			default: response.sendRedirect("/adminPage?token=st&sent=failed" + parameters); break;
		}
	}
	
	private void passwordChange(HttpServletRequest request, HttpServletResponse response) {
		String password = request.getParameter("newPassword");
		String confirmPassword = request.getParameter("cpass");
		String email = request.getParameter("hiddenEmail");
		
		boolean changed = UserProfile.executePasswordChange(password, confirmPassword, email);

		try{
			if(changed){response.sendRedirect("/update");}
			else{response.sendRedirect("/retrievePassword");}
		} catch(IOException e){
			e.printStackTrace();
		}
	}

	private void loginUser(HttpServletRequest request, HttpServletResponse response) {
		String email = request.getParameter("Email");
		String pass = request.getParameter("pass");
		String nextPage = null;
		User user = null;
		
		user = Login.loginUser(email, pass);

		if(user == null){
			nextPage = "/login?token=err";
		}
		else{
			
			HttpSession session = request.getSession(true);
			session.setAttribute("user", user);
			session.setMaxInactiveInterval(1800);

			if(!user.isManagement()){
				nextPage = "/userProfile";
			}
			else{
				nextPage = "/adminPage?token=st";
			}
			
			session.setAttribute(ApplicationConstants.CARRIERARRAY, GatherCarriers.getPhoneCarriers());
		}
		try {
			response.sendRedirect(nextPage);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void loginUserSSO(HttpServletRequest request, HttpServletResponse response) {
		String nextPage = null;
		User user = null;
		
		System.out.println("loginUserSSO");

		HttpSession session = request.getSession(true);

		if(session.getAttribute("user") != null){
			user = (User) session.getAttribute("user");
			System.out.println(user.getFullname());
			
			if(!user.isManagement()){
				nextPage = "/userProfile";
			}
			else{
				nextPage = "/adminPage?token=st";
			}
			
			session.setAttribute(ApplicationConstants.CARRIERARRAY, GatherCarriers.getPhoneCarriers());
		}
		else{
			nextPage = "/login?token=err";
		}
		try {
			response.sendRedirect(nextPage);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void logoutUser(HttpServletRequest request, HttpServletResponse response) {
		HttpSession session = request.getSession(true);
		session.invalidate();
		try {
			response.sendRedirect("/login?token=loggedout");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void deleteTicket(HttpServletRequest request, HttpServletResponse response) {
		String tid = request.getParameter("deleteticketid");
		boolean completedTask = ResponseTable.deleteTicket(tid);
		
		try {
			if(completedTask){
				ResponseTable.gatherResponseData(request, response);
			}
			else{
				response.sendRedirect("/textResponsePage?token=false");
			}
		} catch (IOException e) {
			e.printStackTrace();
		} 
		
	}

	public EmailReader getEmailReader() {
		return emailReader;
	}

	public static void setEmailReader(EmailReader emailReader) {
		Servlet.emailReader = emailReader;
	} 
}
