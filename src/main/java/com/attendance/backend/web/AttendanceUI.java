package com.attendance.backend.web;

import org.springframework.beans.factory.annotation.Autowired;

import com.attendance.backend.model.Activity;
import com.attendance.backend.model.ActivityAttendance;
import com.attendance.backend.model.Person2;
import com.attendance.backend.repository.ActivityAttendanceRepository;
import com.attendance.backend.repository.ActivityRepository;
import com.vaadin.server.FontAwesome;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.Button;
import com.vaadin.ui.Grid;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Grid.SelectionMode;

@SpringComponent
@UIScope
public class AttendanceUI  extends VerticalLayout {

	private static final long serialVersionUID = 1L;

	private final ActivityRepository activityRepository;
	private final ActivityAttendanceRepository activityAttendanceRepository;
	
	private final AttendanceEditorUI attendanceEditorUI;
	
	private Activity selectedActivity;

	//Activities components
	private Label titleActivity = new Label("Selecione a atividade");
	private Grid<Activity> gridActivity = new Grid<>(Activity.class);
	private VerticalLayout activityFrame = new VerticalLayout(titleActivity, gridActivity);
	
	//ActivityAttendance components
	private Label attendanceTitle = new Label("");
	private Button addNewBtn = new Button("Marcar presença", FontAwesome.PLUS);
	private HorizontalLayout topAttendance = new HorizontalLayout(attendanceTitle, addNewBtn);
	private Grid<ActivityAttendance> gridActivityAttendance = new Grid<>(ActivityAttendance.class);
	private VerticalLayout attendanceFrame = new VerticalLayout(topAttendance, gridActivityAttendance);

	@Autowired
	public AttendanceUI(ActivityRepository activityRepository, ActivityAttendanceRepository activityAttendanceRepository, AttendanceEditorUI editor) {
		this.activityRepository = activityRepository;
		this.activityAttendanceRepository = activityAttendanceRepository;
		this.attendanceEditorUI = editor;

		// Build layout
		setVisible(false);
		attendanceFrame.setVisible(false);
		addComponents(activityFrame, attendanceFrame, attendanceEditorUI);
		
		// Configure layouts and components
		gridActivity.setHeight(300, Unit.PIXELS);
		gridActivity.setColumns("id", "name");
		gridActivity.setSelectionMode(SelectionMode.SINGLE);
		
		gridActivityAttendance.setHeight(300, Unit.PIXELS);
		gridActivityAttendance.setColumns("attendanceTitle");

		// Hook logic to components
		
		//Selected Activity
		gridActivity.asSingleSelect().addValueChangeListener(e -> {
			if(e.getValue() == null) return;
			
			activityFrame.setVisible(false);
			attendanceFrame.setVisible(true);
			
			this.selectedActivity = e.getValue();
			
			attendanceTitle.setValue(e.getValue().getId() + " - " + e.getValue().getName());
			listActivityAttendance(e.getValue());
		});

		//Selected Attendance
		gridActivityAttendance.asSingleSelect().addValueChangeListener(e -> {
			if(e.getValue() == null) return;

			activityFrame.setVisible(false);
			attendanceFrame.setVisible(false);

			attendanceEditorUI.edit(e.getValue());
		});

		// Instantiate and edit new Element the new button is clicked
		addNewBtn.addClickListener(e -> {
			activityFrame.setVisible(false);
			attendanceFrame.setVisible(false);
			attendanceEditorUI.edit(new ActivityAttendance(this.selectedActivity));
		});

		// Listen changes made by the editor, refresh data from backend
		attendanceEditorUI.setChangeHandler(() -> {
			attendanceEditorUI.setVisible(false);
			activityFrame.setVisible(false);
			attendanceFrame.setVisible(true);
			listActivityAttendance(this.selectedActivity);
		});

		// Initialize listing
		listActivities();
	}
	
	public final void show() {
		setVisible(true);
		attendanceEditorUI.setVisible(false);
		activityFrame.setVisible(true);
		attendanceFrame.setVisible(false);
		listActivities();
	}
	
	private void listActivities() {
		gridActivity.setItems(activityRepository.findAll());
	}

	private void listActivityAttendance(Activity activity) {
		gridActivityAttendance.setItems(activityAttendanceRepository.findByActivity(activity));
	}

}
