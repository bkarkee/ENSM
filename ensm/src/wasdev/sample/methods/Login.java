package wasdev.sample.methods;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import wasdev.sample.model.User;

public class Login {
	
	private Login() { throw new IllegalStateException("Login class"); }
	
	public static User loginUser(String email, String _password){
		User user = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		
		Connection db = DatabaseConnection.ConnectDB();

		if (db == null) {
			Logger.writeToErrorLogs("No Database Connection for user login");
			return user;
		}
		
		try {
			pst = db.prepareStatement(ApplicationConstants.SELECT_ALL_LOGIN_WHERE_EMAIL);
		
			pst.setString(1, email);
			rs = pst.executeQuery();
	
			if (rs.next()) {
				byte[] dbPass = rs.getBytes("password");
				
				if (AuthenticateUserPassword.authenticate(_password, dbPass)) {
					SQLCloser.close(rs);
					SQLCloser.close(pst);
					
					pst = db.prepareStatement(ApplicationConstants.SELECT_ALL_USERS_WHERE_EMAIL);
					
					pst.setString(1, email);
					rs = pst.executeQuery();
					
					if(rs.next()){
						user = new User();
						user.setEmail(rs.getString("email").trim());
						user.setFirstname(rs.getString("firstname").trim());
						user.setLastname(rs.getString("lastname").trim());
						user.setPhonenumber(rs.getString("phonenumber").trim());
						user.setCarrier(rs.getString("carrier").trim());
						user.setManagement(rs.getBoolean("management"));
					}
				} else{System.out.println("bad");}
			}
		} catch (SQLException|NoSuchAlgorithmException|InvalidKeySpecException e) {
			e.printStackTrace();
		} finally{
			SQLCloser.close(rs);
			SQLCloser.close(pst);
			SQLCloser.close(db);
		}
		return user;
	}
}
