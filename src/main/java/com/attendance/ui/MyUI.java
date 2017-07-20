package com.attendance.ui;

import javax.servlet.annotation.WebServlet;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import com.attendance.backend.repository.SharingRepository;
import com.attendance.backend.repository.SharingUserRepository;
import com.attendance.ui.about.AboutView;
import com.attendance.ui.attendance.AttendanceView;
import com.attendance.ui.authentication.AccessControl;
import com.attendance.ui.authentication.CurrentUser;
import com.attendance.ui.authentication.DBAccessControl;
import com.attendance.ui.authentication.LoginScreen;
import com.attendance.ui.authentication.LoginScreen.LoginListener;
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
	@Value("${build.version}") private String buildVersion;
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

        this.mainScreen.makeScreen(MyUI.this);

        if (!accessControl.isUserSignedIn()) {
            setContent(new LoginScreen(buildVersion, accessControl, new LoginListener() {
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

    	addStyleName(ValoTheme.UI_WITH_MENU);
        setContent(mainScreen);

        //Update User Sharings
    	CurrentUser.getUser().setCenters(sharingUserRepository.findCurrentUserCenters());
    	CurrentUser.getUser().setSharings(sharingRepository.findByUser(CurrentUser.getUser()));

		this.mainScreen.getMenu().refresh();
    	
        getNavigator().navigateTo(this.getViewToNavigate());
    }
    
    private String getViewToNavigate(){

    	String viewName = StringUtils.isEmpty(getNavigator().getState()) ? AttendanceView.VIEW_NAME : getNavigator().getState();

        viewName = mainScreen.getMenu().isVisibleView(viewName) ? viewName : AboutView.VIEW_NAME; 
        
        mainScreen.getMenu().setActiveView(viewName);

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
}
