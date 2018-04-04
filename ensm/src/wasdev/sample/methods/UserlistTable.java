package wasdev.sample.methods;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import wasdev.sample.model.User;

public class UserlistTable {
	
	private UserlistTable() { throw new IllegalStateException("UserlistTable class"); }
	
	public static void databaseTable(HttpServletRequest request, HttpServletResponse response) {
		List<User> userArray = new ArrayList<>();
		// setting up the connection
		Connection db = DatabaseConnection.ConnectDB();

		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			pst = db.prepareStatement("SELECT * FROM Users ");
			rs = pst.executeQuery();
			while (rs.next()) {
				User userDBObject = new User();
				
				userDBObject.setFirstname(rs.getString("firstname").trim());
				userDBObject.setLastname(rs.getString("lastname").trim());
				userDBObject.setPhonenumber(rs.getString("phonenumber").trim());
				userDBObject.setEmail(rs.getString("email").trim());
				userDBObject.setCarrier(rs.getString("carrier").trim());
				userDBObject.setFloor(rs.getInt("floor"));
				userDBObject.setManagement(rs.getBoolean("management"));
				userDBObject.setConfirmedPhone(rs.getBoolean("confirmedphone"));

				userArray.add(userDBObject);
				
				}
			
			HttpSession session = request.getSession(true);
			session.setAttribute("userArray", userArray);

			response.sendRedirect("/usersTable");
		} catch (SQLException|IOException e) {
			e.printStackTrace();
		} finally{
			SQLCloser.close(rs);
			SQLCloser.close(pst);
			SQLCloser.close(db);
		}
	}
	
	public static void updateUserTable(HttpServletRequest request, HttpServletResponse response) throws IOException {
		String email = request.getParameter("hiddenEmail");
		String nameOfElement = request.getParameter("hiddenName");
		String changedBool = request.getParameter("hiddenGroupBool");
		int floor = Integer.parseInt(request.getParameter("hiddenFloor"));
		Boolean groupBool = Boolean.valueOf(changedBool);
		
		Connection db = DatabaseConnection.ConnectDB();
		if (db == null) {
			Logger.writeToErrorLogs("No Database Connection");
			response.sendRedirect("/login");
			return;
		}

		
		PreparedStatement pst = null;
		try {
			switch (nameOfElement) {
				case "flr":
					pst = db.prepareStatement(ApplicationConstants.UPDATE_FLOOR);
					pst.setInt(1, floor);
	
					break;
				case "mng":
					pst = db.prepareStatement(ApplicationConstants.UPDATE_MNG);
					pst.setBoolean(1, groupBool);
	
					break;
				default: pst = null; break;
			}
			
			if(pst != null){
				pst.setString(2, email);
				pst.executeUpdate();
				db.commit();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		finally{
			SQLCloser.close(pst);
			SQLCloser.close(db);

		}

	}
}
