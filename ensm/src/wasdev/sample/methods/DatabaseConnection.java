package wasdev.sample.methods;

import java.sql.Connection;
import java.sql.DriverManager;

public class DatabaseConnection {
	
	private DatabaseConnection() { throw new IllegalStateException("DatabaseConnection class"); }
	
	public static Connection ConnectDB() {
		Connection db = null;
		try {
			Class.forName("org.postgresql.Driver");
		} catch (java.lang.ClassNotFoundException e) {
			e.printStackTrace();
		}
		String url = "jdbc:postgresql://echo.db.elephantsql.com:5432/eatprjtv";
		String username = "eatprjtv";
		String password = "OzTpqul-C2J97UymapxBgiJuVgh-6DJY";

		try {
			db = DriverManager.getConnection(url, username, password);
			db.setAutoCommit(false);
		} catch (java.sql.SQLException e) {
			e.printStackTrace();
		}
		return db;
	}
}
