package com.john.notifications.model;

import org.json.JSONObject;

public class Recipient {
	private String firstName;
	private String lastName;
	private String email;
	private String phoneNumber;
	private PhoneProvider provider;
	private boolean emailNotifications;
	private boolean smsNotifications;
	
	// most basic recipient with bare minimum info
	public Recipient(String firstName, String email, boolean emailNotifications) {
		this.firstName = firstName;
		this.email = email;
		this.emailNotifications = emailNotifications;
	}
	
	// email-only recipient
	public Recipient(String firstName, String lastName, String email, boolean emailNotifications) {
		this(firstName, email, emailNotifications);
		this.lastName = lastName;
	}
	
	// recipient with all info
	public Recipient(String firstName, String lastName, String email, String phoneNumber, PhoneProvider provider, boolean emailNotifications, boolean smsNotifications) {
		this(firstName, lastName, email, emailNotifications);
		this.phoneNumber = phoneNumber;
		this.provider = provider;
		this.smsNotifications = smsNotifications;
	}
	
	// copy constructor for deep cloning
	public Recipient(Recipient r) {
		this(r.getFirstName(), r.getLastName(), r.getEmail(), r.getPhoneNumber(), r.getPhoneProvider(), r.getEmailEnabled(), r.getSmsEnabled());
	}
	
	// create Recipient from JSON object with matching structure
	public Recipient(JSONObject jsonRecipient) {
		this(jsonRecipient.getString("firstName"), jsonRecipient.getString("lastName"),
				jsonRecipient.getString("email"), jsonRecipient.getString("phoneNumber"),
				PhoneProvider.valueOf(jsonRecipient.getString("provider")),
				jsonRecipient.getBoolean("emailNotifications"), jsonRecipient.getBoolean("smsNotifications"));
	}
	
	public String getFirstName() {
		return firstName;
	}
	
	public String getLastName() {
		return lastName;
	}
	
	public String getEmail() {
		return email;
	}
	
	public boolean getEmailEnabled() {
		return emailNotifications;
	}
	
	public String getPhoneNumber() {
		return phoneNumber;
	}
	
	public PhoneProvider getPhoneProvider() {
		return provider;
	}
	
	public boolean getSmsEnabled() {
		return smsNotifications;
	}
}
