package com.john.notifications.model;

public enum PhoneProvider {
	VERIZON("Verizon Wireless", "@vtext.com", (short) 155);
	
	private String name;
	private String emailExtension;
	private short maxLength;
	
	PhoneProvider(String name, String emailExtension, short maxLength) {
		this.name = name;
		this.emailExtension = emailExtension;
		this.maxLength = maxLength;
	}
	
	public String getEmailExtension() {
		return emailExtension;
	}
	
	public short getMaxLength() {
		return maxLength;
	}
	
	@Override
	public String toString() {
		return name;
	}
}
