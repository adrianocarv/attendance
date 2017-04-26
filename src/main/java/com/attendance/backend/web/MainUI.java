package com.attendance.backend.web;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.annotations.Theme;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.VaadinRequest;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

@SpringUI
@Theme("valo")
public class MainUI extends UI {

	private static final long serialVersionUID = 1L;

	//Views
	private final AttendanceUI attendanceUI;
	private final ActivityUI activityUI;
	private final PersonUI personUI;
	private final TestUI testUI;
	
	//Main menu
	private final Button attendanceMenu = new Button("Presenças", VaadinIcons.CHECK_SQUARE_O);
	private final Button personMenu = new Button("Pessoas", VaadinIcons.GROUP);
	private final Button activityMenu = new Button("Atividades", VaadinIcons.CALENDAR_USER);
	private final Button testMenu = new Button("Test", VaadinIcons.HAMMER);

	@Autowired
	public MainUI(AttendanceUI attendanceUI, PersonUI personUI, ActivityUI activityUI, TestUI testUI) {
		this.attendanceUI = attendanceUI;
		this.personUI = personUI;
		this.activityUI = activityUI;
		this.testUI = testUI;
	}

	@Override
	protected void init(VaadinRequest request) {
		// build layout
		HorizontalLayout menu = new HorizontalLayout(attendanceMenu, personMenu, activityMenu, testMenu);
		VerticalLayout views = new VerticalLayout(attendanceUI, personUI, activityUI, testUI);
		VerticalLayout mainLayout = new VerticalLayout(menu, views);
		setContent(mainLayout);

		// Configure layouts and components
//		menu.setMargin(true);
//		menu.setSpacing(true);
//		mainLayout.setMargin(true);
//		mainLayout.setSpacing(true);
		
		// Hook logic to components
		attendanceMenu.addClickListener(e -> showView(attendanceUI));
		personMenu.addClickListener(e -> showView(personUI));
		activityMenu.addClickListener(e -> showView(activityUI));
		testMenu.addClickListener(e -> showView(testUI));
	}
	
	private void showView(VerticalLayout view){
		this.attendanceUI.setVisible(false);
		this.activityUI.setVisible(false);
		this.personUI.setVisible(false);
		this.testUI.setVisible(false);
		
		if(view instanceof AttendanceUI) attendanceUI.show();
		if(view instanceof PersonUI) personUI.show();
		if(view instanceof ActivityUI) activityUI.show();
		if(view instanceof TestUI) testUI.show();
	}
}
