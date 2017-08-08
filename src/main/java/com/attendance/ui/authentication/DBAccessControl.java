package com.attendance.ui.authentication;

import org.springframework.beans.factory.annotation.Autowired;

import com.attendance.backend.model.User;
import com.attendance.backend.repository.UserRepository;
import com.attendance.backend.util.EmailService;
import com.attendance.ui.util.NotificationUtil;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.ui.Notification;

@SpringComponent
public class DBAccessControl implements AccessControl {

	private static final long serialVersionUID = 1L;
	
    /** Dependences */
	@Autowired private UserRepository userRepository;
    @Autowired private EmailService emailService;


	@Override
    public boolean signIn(String username, String password) {
        
		//Find by username OR email
		User user = userRepository.findOneByUsername(username);
		if(user == null)
			user = userRepository.findOneByEmail(username);
		
		if(user == null)
			return false;
		
		if(!user.getPassword().equals(User.getEncryptedPassword(password)))
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
    
	@Override
    public boolean register(User newUser) {
        
		User existentUser = userRepository.findOneByUsername(newUser.getUsername());
		if(existentUser != null){
			NotificationUtil.show(new Notification("Atenção: ", "Já existe um usuário com o login <b>" + newUser.getUsername() + "</b>.", Notification.Type.TRAY_NOTIFICATION, true));
            return false;
		}

		existentUser = userRepository.findOneByEmail(newUser.getEmail());
		if(existentUser != null){
			NotificationUtil.show(new Notification("Atenção: ", "Já existe um usuário com o e-mail <b>" + newUser.getEmail() + "</b>.", Notification.Type.TRAY_NOTIFICATION, true));
			return false;
		}

		//Save newUser
		userRepository.save(newUser);
		
		//Send mail
		String accessToken = emailService.sendEmailVerification(newUser);
		if(accessToken == null) return false;
		newUser.setAccessToken(accessToken);
		userRepository.save(newUser);
        
        return true;
    }
}
