package com.attendance.ui.authentication;

import java.io.Serializable;

import com.attendance.backend.model.User;
import com.attendance.backend.util.AttendanceEmailService;

/**
 * Simple interface for authentication and authorization checks.
 */
public interface AccessControl extends Serializable {

    public boolean signIn(String username, String password);

    public boolean isUserSignedIn();

    public boolean isUserInRole(String role);

    public String getPrincipalName();
    
    public boolean register(User user);
    
    public AttendanceEmailService getAttendanceEmailService();
}
