package com.contactmanager.model;

import java.io.Serializable;

/**
 * The class Contact makes up the data model of a single contact, 
 * which includes First name, last name, phone number and email.
 */
public class Contact implements Serializable{
	
	String firstName, lastName, phoneNo, email;
	
	/**
	 * Constructor used to create a contact object
	 * @param fname first name
	 * @param lname last name
	 * @param phone phone number
	 * @param eMail email
	 */
	public Contact(String fname,String lname,String phone,String eMail) {
		firstName=fname;
		lastName=lname;
		phoneNo=phone;
		email=eMail;
	}
	
	/**
	 * returns the first name
	 * @return first name
	 */
	public String getFirstName() {
		return firstName;
	}
	
	/**
	 * returns the last name
	 * @return last name
	 */
	public String getLastName() {
		return lastName;
	}
	
	/**
	 * returns the phone number
	 * @return phone number
	 */
	public String getPhoneNo() {
		return phoneNo;
	}
	
	/**
	 * returns the email address
	 * @return email address
	 */
	public String getEmail() {
		return email;
	}
	
	/**
	 * Checks if this contact object is equal to the specified contact object
	 * @param c the Contact to which this contact should be compared to
	 * @return true/false if contacts are equal/not equal respectively
	 */
	public boolean isEqualTo(Contact c){
		if((c.getFirstName().equals(firstName))&&(c.getLastName().equals(lastName))&&(c.getPhoneNo().equals(phoneNo))&&(c.getEmail().equals(email)))
			return true;
		else
			return false;
	}
}
