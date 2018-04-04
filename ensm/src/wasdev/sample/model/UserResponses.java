package wasdev.sample.model;

import java.io.Serializable;

public class UserResponses implements Serializable{
	private static final long serialVersionUID = -6855305808585287186L;
	
	private String email;
	private String fullName;
	private String phoneNumber;
	private String responseType;
	private String responseText;
	
	public UserResponses(){
		email = "";
		fullName = "";
		phoneNumber = "";
		responseType = "null";
		responseText = "";
	}
	

	public String getEmail() {return email;}
	public String getFullName() {return fullName;}
	public String getPhoneNumber() {return phoneNumber;}
	public String getResponseType() {return responseType;}
	public String getResponseText() {return responseText;}
	

	public void setEmail(String email) {this.email = email;}
	public void setFullName(String fullName) {this.fullName = fullName;}
	public void setPhoneNumber(String phoneNumber) {this.phoneNumber = phoneNumber;}
	public void setResponseType(String responseType) {this.responseType = responseType;}
	public void setResponseText(String responseText) {this.responseText = responseText;}
}
