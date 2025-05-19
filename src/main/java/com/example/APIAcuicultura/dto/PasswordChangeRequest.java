
package com.example.APIAcuicultura.dto;

public class PasswordChangeRequest {
    
    private String oldPassword;
    private String newPassword;
    private String confirmPassword;

    // Constructor sin argumentos (necesario para la deserialización)
    public PasswordChangeRequest() {
    }

    public String getOldPassword() {
        return oldPassword;
    }

    public void setOldPassword(String oldPassword) {
        this.oldPassword = oldPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmNewPassword(String confirmNewPassword) {
        this.confirmPassword = confirmNewPassword;
    }
    
}
