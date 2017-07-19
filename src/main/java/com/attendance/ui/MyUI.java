package com.attendance.ui;

import javax.servlet.annotation.WebServlet;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.attendance.backend.model.SharingType;
import com.attendance.backend.repository.SharingRepository;
import com.attendance.backend.repository.SharingUserRepository;
import com.attendance.ui.about.AboutView;
import com.attendance.ui.activity.ActivityView;
import com.attendance.ui.attendance.AttendanceView;
import com.attendance.ui.authentication.AccessControl;
import com.attendance.ui.authentication.CurrentUser;
import com.attendance.ui.authentication.DBAccessControl;
import com.attendance.ui.authentication.LoginScreen;
import com.attendance.ui.authentication.LoginScreen.LoginListener;
import com.attendance.ui.person.PersonView;
import com.attendance.ui.sharing.SharingView;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.annotations.Viewport;
import com.vaadin.server.Responsive;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.UI;
import com.vaadin.ui.themes.ValoTheme;

/**
 * Main UI class of the application that shows either the login screen or the
 * main view of the application depending on whether a user is signed in.
 *
 * The @Viewport annotation configures the viewport meta tags appropriately on
 * mobile devices. Instead of device based scaling (default), using responsive
 * layouts.
 */
@Viewport("user-scalable=no,initial-scale=1.0")
@Theme("mytheme")
@SpringUI
public class MyUI extends UI {

	private static final long serialVersionUID = 1L;

    /** Dependences */
    @Autowired private SharingUserRepository sharingUserRepository;
    @Autowired private SharingRepository sharingRepository;
    @Autowired private DBAccessControl accessControl;

	private final MainScreen mainScreen;
	
	@Autowired
	public MyUI(MainScreen mainScreen) {
		this.mainScreen = mainScreen;
	}
	
	@Override
    protected void init(VaadinRequest vaadinRequest) {
        Responsive.makeResponsive(this);
        setLocale(vaadinRequest.getLocale());
        getPage().setTitle("Attendance");
        if (!accessControl.isUserSignedIn()) {
            setContent(new LoginScreen(accessControl, new LoginListener() {
            	private static final long serialVersionUID = 1L;
                @Override
                public void loginSuccessful() {
                    showMainView();
                }
            }));
        } else {
            showMainView();
        }
    }

    protected void showMainView() {

    	this.makeUserScreen();
    	
    	System.out.println("showMainView() - " + getNavigator().getState());
    	
    	addStyleName(ValoTheme.UI_WITH_MENU);
        setContent(mainScreen);

        getNavigator().navigateTo(this.getViewToNavigate());
    }
    
    private String getViewToNavigate(){
        String viewName = StringUtils.isEmpty(getNavigator().getState()) ? AttendanceView.VIEW_NAME : getNavigator().getState();

        if(viewName.equals(AttendanceView.VIEW_NAME) && !CurrentUser.isUserInRole(SharingType.ATTENDANCE_READ)) return AboutView.VIEW_NAME;
        if(viewName.equals(PersonView.VIEW_NAME) && !CurrentUser.isUserInRole(SharingType.PERSON_READ)) return AboutView.VIEW_NAME;
        if(viewName.equals(ActivityView.VIEW_NAME) && !CurrentUser.isUserInRole(SharingType.ACTIVITY_READ)) return AboutView.VIEW_NAME;
        if(viewName.equals(SharingView.VIEW_NAME) && !CurrentUser.isUserInRolesOR(SharingType.SHARING_CENTER, SharingType.SHARING_ACTIVITY)) return AboutView.VIEW_NAME;
        
        return viewName;
    }
    
    public static MyUI get() {
        return (MyUI) UI.getCurrent();
    }

    public AccessControl getAccessControl() {
        return accessControl;
    }

    @WebServlet(urlPatterns = "/*", name = "MyUIServlet", asyncSupported = true)
    @VaadinServletConfiguration(ui = MyUI.class, productionMode = false)
    public static class MyUIServlet extends VaadinServlet {
    	private static final long serialVersionUID = 1L;
    }
    
    private void makeUserScreen(){

    	//Update User Sharings
    	CurrentUser.getUser().setCenters(sharingUserRepository.findCurrentUserCenters());
    	CurrentUser.getUser().setSharings(sharingRepository.findByUser(CurrentUser.getUser()));

		this.mainScreen.makeScreen(MyUI.this);
    }

}
