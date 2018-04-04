package wasdev.sample.model;

import java.io.Serializable;

public class Carrier implements Serializable{
	
	private static final long serialVersionUID = -1432893235552209027L;
	private String provider;
	private String phoneAddress;
	
	public Carrier(String provider, String phoneAddress){
		this.provider = provider;
		this.phoneAddress = phoneAddress;
	}
	public String getProvider(){
		return provider;
	}
	public String getPhoneAddress(){
		return phoneAddress;
	}
	public void setProvider(String provider){
		this.provider = provider;
	}
	public void setPhoneAddress(String phoneAddress){
		this.phoneAddress = phoneAddress;
	}

}


