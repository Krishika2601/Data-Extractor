package com.Data.demo.auth.entity;



import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import java.util.Date;
import java.util.List;

@Entity
public class Customer {

@Id
 private String customerName;
 private int status;
 private String addressLine1;
 private String addressLine2;
 private String city;
 private String role = "CUSTOMER"; 
 private String emailId; 
 private String password;
 private String username; 
 
 public String getUsername() {
     return username;
 }

 public void setUsername(String username) {
     this.username = username;
 }

 public String getEmailId() {
     return emailId;
 }

 public void setEmailId(String emailId) {
     this.emailId = emailId;
 }

 public String getPassword() {
     return password;
 }

 public void setPassword(String password) {
     this.password = password;
 }
 public String getCustomerName() {
	return customerName;
}

public void setCustomerName(String customerName) {
	this.customerName = customerName;
}

public int getStatus() {
	return status;
}

public void setStatus(int status) {
	this.status = status;
}

public String getAddressLine1() {
	return addressLine1;
}

public void setAddressLine1(String addressLine1) {
	this.addressLine1 = addressLine1;
}

public String getAddressLine2() {
	return addressLine2;
}

public void setAddressLine2(String addressLine2) {
	this.addressLine2 = addressLine2;
}

public String getCity() {
	return city;
}

public void setCity(String city) {
	this.city = city;
}

public String getState() {
	return state;
}

public void setState(String state) {
	this.state = state;
}

public List<String> getDeviceIds() {
    return deviceIds;
}

public void setDeviceIds(List<String> deviceIds) {
    this.deviceIds = deviceIds;
}

public Date getDateOfRegistration() {
	return dateOfRegistration;
}

public void setDateOfRegistration(Date dateOfRegistration) {
	this.dateOfRegistration = dateOfRegistration;
}

public String getEmailIds() {
	return emailIds;
}

public void setEmailIds(String emailIds) {
	this.emailIds = emailIds;
}

private String state;
private List<String> deviceIds; 
 private Date dateOfRegistration;
 private String emailIds;
 public String getRole() {
     return role;
 }

 public void setRole(String role) {
     this.role = role;
 }

public Customer() {
	super();
	
}

public Customer(String customerName, int status, String addressLine1, String addressLine2, String city, String role,
        String emailId, String password, String username, String state, List<String> deviceIds,
        Date dateOfRegistration, String emailIds) {
this.customerName = customerName;
this.status = status;
this.addressLine1 = addressLine1;
this.addressLine2 = addressLine2;
this.city = city;
this.role = role;
this.emailId = emailId;
this.password = password;
this.username = username;
this.state = state;
this.deviceIds = deviceIds;
this.dateOfRegistration = dateOfRegistration;
this.emailIds = emailIds;
}

 
}
