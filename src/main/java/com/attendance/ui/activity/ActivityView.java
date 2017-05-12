package com.attendance.ui.activity;

import org.springframework.beans.factory.annotation.Autowired;

import com.attendance.backend.model.Activity;
import com.attendance.backend.repository.ActivityRepository;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.Sizeable.Unit;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Grid;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

@SpringComponent
@UIScope
public class ActivityView extends CssLayout implements View {

	private static final long serialVersionUID = 1L;

    public static final String VIEW_NAME = "Atividades";

	private final ActivityRepository activityRepository;
	
	private final ActivityEditorUI activityEditorUI;

	//Main components
	private Label title = new Label("Atividades 2");
	private Button addNewBtn = new Button("Nova atividade", VaadinIcons.PLUS);
	private Grid<Activity> grid = new Grid<>(Activity.class);
	private HorizontalLayout topComponents = new HorizontalLayout(title, addNewBtn);

//	@Autowired
//	public ActivityView(ActivityRepository activityRepository, ActivityEditorUI editor) {
//		this.activityRepository = activityRepository;
//		this.activityEditorUI = editor;
//
//        setSizeFull();
//        addStyleName("crud-view");
//
//		grid.setColumns("id", "name");
//		grid.setSizeFull();
//
//        
//        VerticalLayout barAndGridLayout = new VerticalLayout();
//        barAndGridLayout.addComponent(grid);
//        barAndGridLayout.setSizeFull();
//        barAndGridLayout.setExpandRatio(grid, 1);
//        barAndGridLayout.setStyleName("crud-main-layout");
//
//        addComponent(barAndGridLayout);
//        
//        
//
//		// Hook logic to components
//		
//		// Connect selected Customer to editor or hide if none is selected
//		grid.asSingleSelect().addValueChangeListener(e -> {
//			if(e.getValue() == null) return;
//
//			activityEditorUI.edit(e.getValue());
//		});
//
//		// Instantiate and edit new Element the new button is clicked
//		addNewBtn.addClickListener(e -> activityEditorUI.edit(new Activity(null)));
//
//		// Listen changes made by the editor, refresh data from backend
//		activityEditorUI.setChangeHandler(() -> {
//			activityEditorUI.setVisible(false);
//			listActivities();
//		});
//
//		// Initialize listing
//		listActivities();
//	}
	
	@Autowired
	public ActivityView(ActivityRepository activityRepository, ActivityEditorUI editor) {
		this.activityRepository = activityRepository;
		this.activityEditorUI = editor;

        setSizeFull();
        addStyleName("crud-view");
        
        VerticalLayout barAndGridLayout = new VerticalLayout();
        barAndGridLayout.addComponent(grid);
        barAndGridLayout.setSizeFull();
        barAndGridLayout.setExpandRatio(grid, 1);
        barAndGridLayout.setStyleName("crud-main-layout");
        
        barAndGridLayout.addComponent(activityEditorUI);

        // Build layout
		//setVisible(false);
		addComponents(topComponents, barAndGridLayout);
		
		// Configure layouts and components
//		topComponents.setSpacing(true);
		grid.setSizeFull();
		grid.setHeight(300, Unit.PIXELS);
		grid.setColumns("id", "name");
		
//		topComponents.setMargin(true);
//		topComponents.setSpacing(true);

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

    @Override
    public void enter(ViewChangeEvent event) {
    }
}
