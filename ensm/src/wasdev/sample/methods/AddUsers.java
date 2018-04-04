package wasdev.sample.methods;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class AddUsers {
	
	private AddUsers() { throw new IllegalStateException("AddUsers class"); }

	public static void addUsers(HttpServletRequest request, HttpServletResponse response) throws IOException {
		
		String fname = (String) request.getParameter("first");
		String lname = (String) request.getParameter("last");
		String email = (String) request.getParameter("email");
		String phone = (String) request.getParameter("phone");
		String carrier = (String) request.getParameter("carrier");
		int floor = Integer.parseInt((String) request.getParameter("floor"));
		String management = (String) request.getParameter("manager");
		
		String status = UserProfile.signUpUser(fname, lname, email, "ibm@monroe", phone, carrier, floor, management, true);
		
		//write the status of the insert to the page
		response.setContentType("text/plain");
	    PrintWriter out = response.getWriter();
    	out.print(status);
	    out.flush();
	    out.close();
		
	}
}
