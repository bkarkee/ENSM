package wasdev.sample.methods;

import java.util.Properties;

import javax.mail.PasswordAuthentication;
import javax.mail.Session;

public class ConnectToGmail {
	
	private ConnectToGmail() { throw new IllegalStateException("ConnectToGmail class"); }
	
	public static Session createGmailSession() {
		// Sender's email ID needs to be mentioned
		final String username = "IBMMonroeENS@gmail.com";// change accordingly
		final String password = "5afeWord";// change accordingly

		String host = "smtp.gmail.com";

		Properties props = new Properties();
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.host", host);
		props.put("mail.smtp.port", "587");

		// Get the Session object.
		Session session = null;
		session = Session.getInstance(props, new javax.mail.Authenticator() {
			@Override
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(username, password);
			}
		});

		return session;
	}
}
