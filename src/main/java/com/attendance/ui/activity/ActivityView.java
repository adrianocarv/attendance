package com.attendance.ui.activity;

import org.springframework.beans.factory.annotation.Autowired;

import com.attendance.backend.model.Activity;
import com.attendance.backend.model.SharingType;
import com.attendance.backend.repository.ActivityRepository;
import com.attendance.ui.authentication.CurrentUser;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.SelectionMode;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

@SpringComponent
@UIScope
public class ActivityView extends CssLayout implements View {

	private static final long serialVersionUID = 1L;

    public static final String VIEW_NAME = "Atividades";

	/** Dependences */
    @Autowired private ActivityRepository activityRepository;
    private final ActivityLayout activityLayout;

    /** Components */
	private Button buttonNew= new Button("Nova atividade", VaadinIcons.PLUS_CIRCLE);
	private Grid<Activity> grid = new Grid<>(Activity.class);
	
	@Autowired
	public ActivityView(ActivityLayout activityLayout) {
		this.activityLayout = activityLayout;
		
		buildLayout();
		configureComponents();
		hookLogicToComponents();
	}

    private void buildLayout(){
        setSizeFull();
        addStyleName("crud-view");

        HorizontalLayout topLayout = new HorizontalLayout(buttonNew);
        topLayout.setWidth("100%");
        topLayout.setStyleName("top-bar");
        topLayout.setComponentAlignment(buttonNew, Alignment.MIDDLE_RIGHT);

    	VerticalLayout vertical = new VerticalLayout(topLayout, grid);
    	vertical.setSizeFull();
    	vertical.setMargin(true);
    	vertical.setStyleName("form-layout");
    	vertical.setExpandRatio(grid,  1);
    	
        addComponents(activityLayout, vertical);
	}
	
	private void configureComponents() {
        buttonNew.addStyleName(ValoTheme.BUTTON_PRIMARY);
		
		grid.setSizeFull();
		grid.setColumns("name");
		grid.getColumn("name").setCaption("Atividades").setResizable(false).setSortable(true);
		grid.sort("name");
		grid.setSelectionMode(SelectionMode.SINGLE);
	}

	private void hookLogicToComponents() {

		buttonNew.addClickListener(e -> activityLayout.enter(this, new Activity(null)));

		grid.asSingleSelect().addValueChangeListener(e -> {
			if(e.getValue() == null) return;
			
			activityLayout.enter(this, e.getValue());
		});
	}

    @Override
    public void enter(ViewChangeEvent event) {

    	//security
    	buttonNew.setVisible(CurrentUser.isUserInRole(SharingType.ACTIVITY_WRITE));
    	if(!CurrentUser.isUserInRole(SharingType.ACTIVITY_READ)) return;

    	grid.setVisible(true);
    	activityLayout.setVisible(false);

    	grid.setItems(activityRepository.findByCenter(CurrentUser.getCurrentCenter()));
    }
}
