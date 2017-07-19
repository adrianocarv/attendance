package com.attendance.ui.authentication;

import org.springframework.beans.factory.annotation.Autowired;

import com.attendance.backend.model.User;
import com.attendance.backend.repository.UserRepository;
import com.vaadin.spring.annotation.SpringComponent;

@SpringComponent
public class DBAccessControl implements AccessControl {

	private static final long serialVersionUID = 1L;
	
    /** Dependences */
	@Autowired private UserRepository userRepository;

	@Override
    public boolean signIn(String username, String password) {
        
		User user = userRepository.findOneByUsername(username);
		
		if(user == null)
			return false;
		
		if(!user.getPassword().equals(password))
			return false;
		
		CurrentUser.set(user);
        
        return true;
    }

    @Override
    public boolean isUserSignedIn() {
        return !CurrentUser.get().isEmpty();
    }

    @Override
    public boolean isUserInRole(String role) {
        return false;
    }

    @Override
    public String getPrincipalName() {
        return CurrentUser.get();
    }
}
