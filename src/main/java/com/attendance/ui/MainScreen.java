package com.attendance.ui;

import org.springframework.beans.factory.annotation.Autowired;

import com.attendance.ui.about.AboutView;
import com.attendance.ui.activity.ActivityView;
import com.attendance.ui.attendance.AttendanceView;
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

	private Menu menu;

	@Autowired private AttendanceView attendanceView;
	@Autowired private ActivityView activityView;
	@Autowired private AboutView aboutView;

	public MainScreen() {
        setSpacing(false);
        setStyleName("main-screen");
    }

	public void makeScreen(MyUI ui){
        CssLayout viewContainer = new CssLayout();
        viewContainer.addStyleName("valo-content");
        viewContainer.setSizeFull();

        final Navigator navigator = new Navigator(ui, viewContainer);
        navigator.setErrorView(ErrorView.class);
        menu = new Menu(navigator);
        menu.addView(attendanceView, AttendanceView.VIEW_NAME, AttendanceView.VIEW_NAME, VaadinIcons.CHECK_SQUARE_O);
        menu.addView(activityView, ActivityView.VIEW_NAME, ActivityView.VIEW_NAME, VaadinIcons.CALENDAR_USER);
        menu.addView(aboutView, AboutView.VIEW_NAME, AboutView.VIEW_NAME, VaadinIcons.INFO_CIRCLE);

        navigator.addViewChangeListener(viewChangeListener);

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
