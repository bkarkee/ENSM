package wasdev.sample.methods;

import java.io.UnsupportedEncodingException;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class SendEmail {
	
	private SendEmail() { throw new IllegalStateException("SendEmail class"); }
	
	public static void sendEmail(String recipient, String subject, String body) {
		// Recipient's email ID needs to be mentioned.
		String sender = " IBM ENSM";
		// Sender's email ID needs to be mentioned
		Session session = ConnectToGmail.createGmailSession();

		try {
			// Create a default MimeMessage object.
			Message message = new MimeMessage(session);
			// Set From
			try {
				message.setFrom(new InternetAddress("IBMMonroeENS@gmail.com", sender));
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			// Set To
			message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipient));
			// Set Subject
			message.setSubject(subject);
			// Set message
			message.setText(body);
			// Send message
			Transport.send(message);

			System.out.println("Sent message successfully: " + subject);

		} catch (MessagingException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Main e-mail method - sends out an email
	 * (will only send one email at a time, so if you need to send more, loop)
	 * @param session
	 * @param from
	 * @param to
	 * @param subject
	 * @param bodyText
	 * @param bodyHtml
	 * @return
	 */
	public static boolean sendEmailHTML(String to, String subject, String bodyHtml) {
		String from = " IBM ENSM";

		//Need to check to ensure that all the parameters have been filled in
		if (to==null || to.trim().equals("") || from.trim().equals("") || subject == null || subject.trim().equals("") || bodyHtml == null || bodyHtml.trim().equals("")){
			return false;
		}

		//Connect over SMTP to send emails.

		Session mailSession = ConnectToGmail.createGmailSession();
		MimeMessage mimeMsg = new MimeMessage(mailSession);

		//Need to set the fields for the email
		try {
			mimeMsg.setFrom(new InternetAddress("IBMMonroeENS@gmail.com", from));
			if (to.contains(",")){
				mimeMsg.addRecipients(MimeMessage.RecipientType.TO,(InternetAddress.parse(to)));
			} else {
				mimeMsg.setRecipients(MimeMessage.RecipientType.TO, new InternetAddress[] { new InternetAddress(to) });
			} 
			mimeMsg.setSubject(subject);
			mimeMsg.setContent(bodyHtml, "text/html");
			Transport.send(mimeMsg);
		} catch (AddressException e) {
			e.printStackTrace();
			return false;
		} catch (MessagingException e) {
			e.printStackTrace();
			return false;
		}catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
}
