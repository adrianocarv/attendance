package com.attendance.backend.web;

import org.springframework.beans.factory.annotation.Autowired;

import com.attendance.backend.model.Activity;
import com.attendance.backend.repository.ActivityRepository;
import com.vaadin.server.FontAwesome;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.Button;
import com.vaadin.ui.Grid;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

@SpringComponent
@UIScope
public class ActivityUI  extends VerticalLayout {

	private static final long serialVersionUID = 1L;

	private final ActivityRepository activityRepository;
	
	private final ActivityEditorUI activityEditorUI;

	//Main components
	private Label title = new Label("Atividades");
	private Button addNewBtn = new Button("Nova atividade", FontAwesome.PLUS);
	private Grid<Activity> grid = new Grid<>(Activity.class);
	private HorizontalLayout topComponents = new HorizontalLayout(title, addNewBtn);

	@Autowired
	public ActivityUI(ActivityRepository activityRepository, ActivityEditorUI editor) {
		this.activityRepository = activityRepository;
		this.activityEditorUI = editor;

		// Build layout
		setVisible(false);
		addComponents(topComponents, grid, activityEditorUI);
		
		// Configure layouts and components
		topComponents.setSpacing(true);
		grid.setHeight(300, Unit.PIXELS);
		grid.setColumns("id", "name");
		
		topComponents.setMargin(true);
		topComponents.setSpacing(true);

		// Hook logic to components
		
		// Connect selected Customer to editor or hide if none is selected
		grid.asSingleSelect().addValueChangeListener(e -> {
			if(e.getValue() == null) return;

			activityEditorUI.edit(e.getValue());
		});

		// Instantiate and edit new Element the new button is clicked
		addNewBtn.addClickListener(e -> activityEditorUI.edit(new Activity(null)));

		// Listen changes made by the editor, refresh data from backend
		activityEditorUI.setChangeHandler(() -> {
			activityEditorUI.setVisible(false);
			listActivities();
		});

		// Initialize listing
		listActivities();
	}
	
	public final void show() {
		setVisible(true);
	}
	
	void listActivities() {
		grid.setItems(activityRepository.findAll());
	}
}
