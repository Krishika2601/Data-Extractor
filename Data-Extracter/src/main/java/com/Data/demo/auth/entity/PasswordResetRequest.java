package com.Data.demo.auth.entity;
public class PasswordResetRequest {
    private String emailId;
    private String currentPassword;
    private String newPassword;
    private String confirmNewPassword;
	public String getEmail() {
		return emailId;
	}
	public void setEmail(String emailId) {
		this.emailId = emailId;
	}
	public String getCurrentPassword() {
		return currentPassword;
	}
	public void setCurrentPassword(String currentPassword) {
		this.currentPassword = currentPassword;
	}
	public String getNewPassword() {
		return newPassword;
	}
	public void setNewPassword(String newPassword) {
		this.newPassword = newPassword;
	}
	public String getConfirmNewPassword() {
		return confirmNewPassword;
	}
	public void setConfirmNewPassword(String confirmNewPassword) {
		this.confirmNewPassword = confirmNewPassword;
	}

  
}