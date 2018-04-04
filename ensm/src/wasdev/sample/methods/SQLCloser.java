package wasdev.sample.methods;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SQLCloser {
	
	private SQLCloser() { throw new IllegalStateException("SQLCloser class"); }
	
	public static void close(ResultSet rs){
		try{
			if(rs != null){
				rs.close();
			}
		} catch(SQLException e){
			e.printStackTrace();
		}
	}
	
	public static void close(PreparedStatement pst){
		try{
			if(pst != null){
				pst.close();
			}
		} catch(SQLException e){
			e.printStackTrace();
		}
	}
	
	public static void close(Connection db){
		try{
			if(db != null){
				db.close();
			}
		} catch(SQLException e){
			e.printStackTrace();
		}
	}
}
