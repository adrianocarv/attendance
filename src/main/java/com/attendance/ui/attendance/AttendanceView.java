package com.attendance.ui.attendance;

import org.springframework.beans.factory.annotation.Autowired;

import com.attendance.backend.model.Activity;
import com.attendance.backend.repository.ActivityRepository;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.SelectionMode;

@SpringComponent
@UIScope
public class AttendanceView extends CssLayout implements View {

	private static final long serialVersionUID = 1L;

    public static final String VIEW_NAME = "Presenças";

	/** Dependences */
    @Autowired private ActivityRepository activityRepository;
    private final AttendanceLayout attendanceLayout;

    /** Components */
	private Grid<Activity> grid = new Grid<>(Activity.class);
	
	@Autowired
	public AttendanceView(AttendanceLayout attendanceLayout) {
		this.attendanceLayout = attendanceLayout;
		
		buildLayout();
		configureComponents();
		hookLogicToComponents();
	}

    private void buildLayout(){
        setSizeFull();
        addStyleName("crud-view");

        addComponents(grid, attendanceLayout);
	}
	
	private void configureComponents() {
		grid.setSizeFull();
		grid.setColumns("name");
		grid.getColumn("name").setCaption("Atividade").setResizable(false).setSortable(false);
		grid.setSelectionMode(SelectionMode.SINGLE);
	}

	private void hookLogicToComponents() {
		grid.asSingleSelect().addValueChangeListener(e -> {
			if(e.getValue() == null) return;
			
			attendanceLayout.enter(this, e.getValue());
		});
	}

    @Override
    public void enter(ViewChangeEvent event) {
    	grid.setVisible(true);
    	attendanceLayout.setVisible(false);

    	grid.setItems(activityRepository.findAll());
    }

	public Grid<Activity> getGrid() {
		return grid;
	}
}
