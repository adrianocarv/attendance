package com.attendance.ui.attendance;

import com.attendance.backend.model.Activity;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.VerticalLayout;

@SpringComponent
@UIScope
public class AttendanceActivityLayout extends VerticalLayout {

	private static final long serialVersionUID = 1L;

    /** Dependences */

    /** Components */

    private AttendanceLayout parentView;
	private Activity currentActivity;

	public AttendanceActivityLayout() {

		buildLayout();
		configureComponents();
		hookLogicToComponents();

	}

    private void buildLayout(){
    	setVisible(false);
	}
	
	private void configureComponents() {
	}

	private void hookLogicToComponents() {
	}

    public void enter(AttendanceLayout parentView, Activity a) {
    	this.parentView = parentView;
    	this.currentActivity = a;
    }

}
