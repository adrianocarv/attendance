package com.attendance.ui.authentication;

import java.util.List;

import com.attendance.backend.model.Activity;
import com.attendance.backend.model.Center;
import com.attendance.backend.model.Sharing;
import com.attendance.backend.model.SharingType;
import com.attendance.backend.model.User;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinService;

/**
 * Class for retrieving and setting the name of the current user of the current
 * session (without using JAAS). All methods of this class require that a
 * {@link VaadinRequest} is bound to the current thread.
 * 
 * 
 * @see com.vaadin.server.VaadinService#getCurrentRequest()
 */
public final class CurrentUser {

    /**
     * The attribute key used to store the username in the session.
     */
    public static final String CURRENT_USER_SESSION_ATTRIBUTE_KEY = CurrentUser.class.getCanonicalName();

    private CurrentUser() {
    }

    /**
     * Returns the name of the current user stored in the current session, or an
     * empty string if no user name is stored.
     * 
     * @throws IllegalStateException
     *             if the current session cannot be accessed.
     */
    public static String get() {
    	User currentUser = (User) getCurrentRequest().getWrappedSession().getAttribute(CURRENT_USER_SESSION_ATTRIBUTE_KEY);
        if (currentUser == null) {
            return "";
        } else {
            return currentUser.getUsername();
        }
    }

    public static User getUser() {
    	User currentUser = (User) getCurrentRequest().getWrappedSession().getAttribute(CURRENT_USER_SESSION_ATTRIBUTE_KEY);
    	return currentUser != null ? currentUser : new User(-1L);
    }
    
    public static Center getCurrentCenter() {
    	return getUser().getId() != -1 ? getUser().getCurrentCenter() : new Center(-1L);
    }
    
    public static List<Center> getCenters() {
    	return getUser().getCenters();
    }
    
	public static boolean isUserInRole(SharingType type) {
		if(getUser() == null)
			return false;
		
		if(getCurrentCenter().isCurrentUserOwner())
			return true;
		
		for(Sharing sharing : getUser().getSharings()){
			if(sharing.matches(type, getCurrentCenter()))
				return true;
		}
		
		return false;
	}

	public static boolean isUserInActivity(Activity activity) {
		if(getUser() == null)
			return false;
		
		for(Sharing sharing : getUser().getSharings()){
			if(sharing.matches(SharingType.ATTENDANCE_READ, getCurrentCenter(), activity))
				return true;
		}
		
		return false;
	}

	public static boolean isUserInRoleAndActivity(SharingType type, Activity activity) {
		
		if(type == null || activity == null)
			return false;
		
		for(Sharing sharing : getUser().getSharings()){
			boolean inCurrentCenter = getCurrentCenter().getId() == sharing.getSharingCenter().getId();
			boolean inRole = type == sharing.getType();
			boolean inActivity = sharing.isSharingCenter() || activity.getId() == sharing.getActivity().getId();
			if(inCurrentCenter && inRole && inActivity)
				return true;
		}
		return false;
	}
	
	/**
     * Sets the name of the current user and stores it in the current session.
     * Using a {@code null} username will remove the username from the session.
     * 
     * @throws IllegalStateException
     *             if the current session cannot be accessed.
     */
    public static void set(User currentUser) {
        if (currentUser == null) {
            getCurrentRequest().getWrappedSession().removeAttribute(CURRENT_USER_SESSION_ATTRIBUTE_KEY);
        } else {
            getCurrentRequest().getWrappedSession().setAttribute(CURRENT_USER_SESSION_ATTRIBUTE_KEY, currentUser);
        }
    }

    private static VaadinRequest getCurrentRequest() {
        VaadinRequest request = VaadinService.getCurrentRequest();
        if (request == null) {
            throw new IllegalStateException(
                    "No request bound to current thread");
        }
        return request;
    }
}
