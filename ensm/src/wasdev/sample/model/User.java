package wasdev.sample.model;

import java.io.Serializable;

public class User implements Serializable{
	private static final long serialVersionUID = -7186476300300652133L;
	private String email;
	private String firstname;
	private String lastname;
	private String fullname;
	private String phonenumber;
	private String carrier;
	private int floor;
	private boolean dbconfirmedPhone;
	private boolean management;
	

	// user login
	public User() {
		email = "";
		firstname = "";
		lastname = "";
		phonenumber = "";
		carrier = "";
		floor = 0;
		management = false;
		dbconfirmedPhone = false;
	}

	// getters
	public String getEmail() {return email;}
	public String getFirstname() {return firstname;}
	public String getLastname() {return lastname;}
	public String getFullname() {return fullname;}
	public String getPhonenumber() {return phonenumber;}
	public String getCarrier() {return carrier;}
	public int getFloor() {return floor;}
	public boolean getConfirmedPhone() {return dbconfirmedPhone;}
	public boolean isManagement() {return management;}

	// setters
	public void setEmail(String email) {this.email = email;}
	public void setFirstname(String firstname) {this.firstname = firstname;}
	public void setLastname(String lastname) {this.lastname = lastname;}
	public void setFullname(String fullname) {this.fullname = fullname;}
	public void setPhonenumber(String phonenumber) {this.phonenumber = phonenumber;}
	public void setCarrier(String carrier) {this.carrier = carrier;}
	public void setFloor(int floor) {this.floor = floor;}
	public void setConfirmedPhone(boolean dbconfirmedPhone) {this.dbconfirmedPhone = dbconfirmedPhone;}
	public void setManagement(boolean group_management) {this.management = group_management;}
}

	
	
