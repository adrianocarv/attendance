package com.attendance.ui;

import javax.servlet.annotation.WebServlet;

import org.springframework.beans.factory.annotation.Autowired;

import com.attendance.ui.attendance.AttendanceView;
import com.attendance.ui.authentication.AccessControl;
import com.attendance.ui.authentication.BasicAccessControl;
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

	private AccessControl accessControl = new BasicAccessControl();

	private final MainScreen mainScreen;
	
	@Autowired
	public MyUI(MainScreen mainScreen) {
		this.mainScreen = mainScreen;
		this.mainScreen.makeScreen(MyUI.this);
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
        addStyleName(ValoTheme.UI_WITH_MENU);
        setContent(mainScreen);
        getNavigator().navigateTo(AttendanceView.VIEW_NAME);
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
