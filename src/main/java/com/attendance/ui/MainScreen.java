package com.attendance.ui;

import org.springframework.beans.factory.annotation.Autowired;

import com.attendance.backend.model.SharingType;
import com.attendance.ui.about.AboutView;
import com.attendance.ui.activity.ActivityView;
import com.attendance.ui.attendance.AttendanceView;
import com.attendance.ui.authentication.CurrentUser;
import com.attendance.ui.center.CenterView;
import com.attendance.ui.person.PersonView;
import com.attendance.ui.sharing.SharingView;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.navigator.Navigator;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.HorizontalLayout;

/**
 * Content of the UI when the user is logged in.
 * 
 * 
 */
@SpringComponent
@UIScope
public class MainScreen extends HorizontalLayout {
    
	private static final long serialVersionUID = 1L;

	/** Dependences */
	@Autowired private AttendanceView attendanceView;
	@Autowired private PersonView personView;
	@Autowired private ActivityView activityView;
	@Autowired private CenterView centerView;
	@Autowired private SharingView sharingView;
	@Autowired private AboutView aboutView;

    /** Components */
	private Menu menu;
	private CssLayout viewContainer = new CssLayout();
	private Navigator navigator;	
	
		
	public MainScreen() {
        setSpacing(false);
        setStyleName("main-screen");
    }

	public void makeScreen(MyUI ui){
        viewContainer = new CssLayout();
        viewContainer.addStyleName("valo-content");
        viewContainer.setSizeFull();

        navigator = new Navigator(ui, viewContainer);
        navigator.setErrorView(ErrorView.class);
        menu = new Menu(navigator);
        
        if(CurrentUser.isUserInRole(SharingType.ATTENDANCE_READ)) menu.addView(attendanceView, AttendanceView.VIEW_NAME, AttendanceView.VIEW_NAME, VaadinIcons.CHECK_SQUARE_O);
        if(CurrentUser.isUserInRole(SharingType.PERSON_READ)) menu.addView(personView, PersonView.VIEW_NAME, PersonView.VIEW_NAME, VaadinIcons.GROUP);
        if(CurrentUser.isUserInRole(SharingType.ACTIVITY_READ))menu.addView(activityView, ActivityView.VIEW_NAME, ActivityView.VIEW_NAME, VaadinIcons.CALENDAR_USER);
        menu.addView(centerView, CenterView.VIEW_NAME, CenterView.VIEW_NAME, VaadinIcons.INSTITUTION);
        if(CurrentUser.isUserInRolesOR(SharingType.SHARING_CENTER, SharingType.SHARING_ACTIVITY)) menu.addView(sharingView, SharingView.VIEW_NAME, SharingView.VIEW_NAME, VaadinIcons.CONNECT_O);
        menu.addView(aboutView, AboutView.VIEW_NAME, AboutView.VIEW_NAME, VaadinIcons.INFO_CIRCLE);

        this.menu.loadUserSection();
        
        addComponent(menu);
        addComponent(viewContainer);
        setExpandRatio(viewContainer, 1);
        setSizeFull();
	}
	
    // notify the view menu about view changes so that it can display which view
    // is currently active
    ViewChangeListener viewChangeListener = new ViewChangeListener() {
		private static final long serialVersionUID = 1L;

		@Override
        public boolean beforeViewChange(ViewChangeEvent event) {
            return true;
        }

        @Override
        public void afterViewChange(ViewChangeEvent event) {
            menu.setActiveView(event.getViewName());
        }

    };
}
