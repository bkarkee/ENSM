package wasdev.sample.methods;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import wasdev.sample.model.Carrier;

public class GatherCarriers {
	
	private GatherCarriers() { throw new IllegalStateException("GatherCarriers class"); }
	
	public static List<Carrier> getPhoneCarriers() {
		ArrayList<Carrier> carriers = new ArrayList<>();
		PreparedStatement pst = null;
		ResultSet rs = null;

		// setting up the connection
		Connection db = DatabaseConnection.ConnectDB();

		try {
			pst = db.prepareStatement(ApplicationConstants.SELECT_ALL_CARRIERS);
			rs = pst.executeQuery();
			
			while (rs.next()) {
				Carrier	carrier = new Carrier(rs.getString("provider").trim(), rs.getString("phoneaddress").trim());
				carriers.add(carrier);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally{
			SQLCloser.close(rs);
			SQLCloser.close(pst);
			SQLCloser.close(db);
		}
		return carriers;
		
	}
}
