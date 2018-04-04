package wasdev.sample.methods;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class ApplicationConstants {
	
	private ApplicationConstants() { throw new IllegalStateException("Utility class"); }
	
	//Random Ticket Letters
	protected static final String[] RANDOMID = {"A", "B", "C", "D", "E", "F", "G", "H", "I", 
											 "J", "K", "L", "M", "N", "O", "P", "Q", "R", 
											 "S", "T", "U", "V", "W", "X", "Y", "Z", "1", 
											 "2", "3", "4", "5", "6", "7", "8", "9", "0"};
	
	private static String currentTicketID = "";
	public static String getCurrentTicketID(){return currentTicketID;}
	public static void setCurrentTicketID(String passedTicketID){currentTicketID = passedTicketID;}
	private static byte[] appSalt = null;
	public static byte[] getAppSalt(){return appSalt;}
	public static void setAppSalt(byte[] passedAppSalt){appSalt = passedAppSalt;}
	
	//Variable constants
	public static final String CARRIERARRAY = "carrierArray";
	public static final String SESSIONENV = "sessionEnvironment";
	
	//Update queries
	public static final String UPDATE_FLOOR = "UPDATE Users SET floor=? WHERE email=?";
	public static final String UPDATE_MNG = "UPDATE Users SET management=? WHERE email=?";
	public static final String UPDATE_PASSWORD_WHERE_EMAIL = "UPDATE login SET password=? WHERE email=?";
	public static final String UPDATE_USER_CONFIRMED_PHONE = "UPDATE Users SET confirmedphone = ? WHERE phonenumber=?";
	public static final String UPDATE_USER_INFO = "UPDATE Users SET firstname=?, lastname=?, phonenumber=?, carrier=? WHERE email=?";
	public static final String UPDATE_USER_INFO_WITH_PHONE = "UPDATE Users SET firstname=?, lastname=?, phonenumber=?, carrier=?, confirmedPhone=false WHERE email=?";
	public static final String UPDATE_USER_PASSWORDCHANGE = "UPDATE passwordreset SET date=CURRENT_TIMESTAMP + INTERVAL '30 minutes', uid=uuid_generate_v4() WHERE email=?";

	//Select queries
	//login
	public static final String SELECT_ALL_LOGIN_WHERE_EMAIL = "SELECT * FROM login WHERE EMAIL = ?";
	//user
	public static final String SELECT_ALL_USERS_WHERE_EMAIL = "SELECT * FROM Users WHERE EMAIL = ?";
	public static final String CHECK_EXISTING_USER = "select NOT EXISTS (select email from activation where email = ?) and NOT EXISTS (select email from users where email = ?) as exists";
	public static final String SELECT_ALL_CIC = "SELECT * FROM Users";
	public static final String SELECT_USER_BY_PHONE = "SELECT * FROM Users WHERE phonenumber=?";
	public static final String SELECT_ALL_MNG = "SELECT * FROM Users Where management=true";
	public static final String SELECT_PHONE_CARRIER = "SELECT phoneaddress FROM PhoneCarriers WHERE provider=?";
	public static final String SELECT_STARTUP_TICKETID = "SELECT ticketid, date FROM ticketids ORDER BY date DESC LIMIT 1";
	//activation
	public static final String SELECT_ALL_ACTIVATION_WHERE_EMAIL = "SELECT * FROM Activation WHERE email = ? and acode=?";
	//responsetable
	public static final String SELECT_ALL_RESPONSE_TABLE = "SELECT * FROM ResponseTable";
	public static final String SELECT_USER_FROM_RESPONSE_TABLE = "SELECT * FROM ResponseTable WHERE email=?";
	public static final String SELECT_USER_FROM_USERTEXTS_TABLE = "SELECT * FROM usertexts WHERE email=? and ticketid=?";
	public static final String SELECT_USER_FROM_PAST_USERTEXTS_TABLE = "SELECT * FROM usertexts WHERE ticketid=? ORDER BY date asc;";
	//pjonecarriers
	public static final String SELECT_ALL_CARRIERS = "SELECT * FROM PhoneCarriers ORDER BY provider DESC";
	//passwordreset
	public static final String SELECT_USER_PASSWORDCHANGE = "SELECT * FROM passwordreset WHERE email=? and uid=? and date > CURRENT_TIMESTAMP";
	public static final String SELECT_UUID_PASSWORDCHANGE = "SELECT uid FROM passwordreset WHERE email=?";
	//Tickets
	public static final String SELECT_TICKET_IDS = "SELECT * FROM ticketids";
	
	//Insert queries
	public static final String INSERT_NEW_USER_ACTIVATION = "INSERT INTO Activation (email,password,firstname,lastname,phonenumber,carrier,acode)"
			+ "VALUES (?,?,?,?,?,?,?)";
	public static final String INSERT_FROM_ACTIVATION_TO_USERS = "INSERT INTO Users(firstname, lastname, phonenumber, carrier, email, confirmedphone, management, floor)"
			+ "SELECT firstname, lastname, phonenumber, carrier, email, false, false, 0 FROM activation WHERE email=?";
	public static final String INSERT_FROM_ACTIVATION_TO_LOGIN = "INSERT INTO login(email, password) SELECT email, password FROM activation WHERE email=?";
	public static final String INSERT_USER_TO_TICKET_TABLE = "INSERT INTO ResponseTable (email)"
			+ "VALUES (?)";
	public static final String INSERT_NEW_TICKET = "INSERT INTO ticketids (ticketid, date, text)"
			+ "VALUES (?,?,?)";
	public static final String INSERT_TO_PASSWORDCHANGE = "INSERT INTO passwordreset (email, date, uid) "
			+ "VALUES (?, CURRENT_TIMESTAMP, uuid_generate_v4())";
	public static final String INSERT_TO_USERTEXTS = "INSERT INTO usertexts (email, date, text, ticketid) "
			+ "VALUES (?,?,?,?)";
	
	//Delete queries
	public static final String DELETE_FROM_ACTIVATION_WHERE_EMAIL = "DELETE FROM Activation WHERE EMAIL = ?";
	public static final String DELETE_FROM_USERS_WHERE_PHONENUMBER = "DELETE FROM Users WHERE phonenumber=?";
	public static final String DELETE_FROM_PASSWORD_RESET_WHERE_EMAIL = "DELETE FROM passwordreset WHERE email=?";
	public static final String DELETE_FROM_RESPONSE_TABLE_WHERE_PHONENUMBER = "DELETE FROM responsetable WHERE phonenumber=?";
	public static final String DELETE_ALL_FROM_RESPONSE_TABLE = "DELETE FROM ResponseTable";
	public static final String DELETE_TICKETID = "DELETE FROM ticketids WHERE ticketid=?";
	public static final String DELETE_ALL_FROM_USERTEXTS_WHERE_TICKETID = "DELETE FROM usertexts WHERE ticketid=?";
	
	//URL Strings
	public static final String PRODURL = "http://ensm.mybluemix.net/";
	public static final String TESTURL = "http://localhost:9080/IBMMonroeENS/";
	
	//static functions used all over
	public static String convertTimeToMonroe(Date date){
		//get the current local time for Monroe
		String pattern = "MM/dd hh:mm a";
		SimpleDateFormat format = new SimpleDateFormat(pattern);
		Calendar cal = Calendar.getInstance();
		if(date != null){ cal.setTime(date); }
		TimeZone tz = TimeZone.getTimeZone("America/Chicago");
		format.setTimeZone(tz);
		
		return format.format(cal.getTime());
	}
}
