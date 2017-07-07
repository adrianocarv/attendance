package com.attendance.ui.attendance;


import org.springframework.beans.factory.annotation.Autowired;

import com.attendance.backend.model.ActivityAttendance;
import com.attendance.backend.repository.ActivityAttendanceRepository;
import com.vaadin.event.ShortcutAction;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.Button;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Grid;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

@SpringComponent
@UIScope
public class AttendanceActivityLayout extends CssLayout {

	private static final long serialVersionUID = 1L;

    /** Dependences */
	@Autowired private ActivityAttendanceRepository activityAttendanceRepository;

    /** Components */
	private Label labelActivity = new Label();
	private Grid<ActivityAttendance> grid = new Grid<>(ActivityAttendance.class);
	private Button buttonBack = new Button("Voltar");

	private AttendanceLayout parentView;
	
	public AttendanceActivityLayout() {
		
		buildLayout();
		configureComponents();
		hookLogicToComponents();

	}

    private void buildLayout(){
    	setVisible(false);
    	
    	setStyleName("product-form-wrapper");
    	
        HorizontalLayout topLayout = new HorizontalLayout(labelActivity);
        topLayout.setWidth("100%");
        topLayout.setStyleName("top-bar");

    	VerticalLayout vertical = new VerticalLayout(topLayout, grid, buttonBack);
    	vertical.setSizeFull();
    	vertical.setMargin(false);
    	vertical.setStyleName("form-layout");
    	vertical.setExpandRatio(grid,  1);
    	
    	addComponent(vertical);
	}
	private void configureComponents() {

		grid.setSizeFull();
		grid.setColumns("attendanceTitle");
		grid.getColumn("attendanceTitle").setCaption("Ocorrências").setResizable(false).setSortable(false);
        
		buttonBack.setStyleName("cancel");
		buttonBack.setClickShortcut(ShortcutAction.KeyCode.ESCAPE);
	}

	private void hookLogicToComponents() {

		buttonBack.addClickListener(e -> {
			setVisible(false);
		});
		
		grid.asSingleSelect().addValueChangeListener(e -> {
			if(e.getValue() == null)
				return;
			
			setVisible(false);
			parentView.selectAttendanceActivity(e.getValue().getDate().toLocalDate(), e.getValue().getTitle());
		});
		
	}

    public void enter(AttendanceLayout parentView) {
    	this.parentView = parentView;
    	
		setVisible(true);
		loadGrid();
    }

	private void loadGrid() {
		grid.setItems(activityAttendanceRepository.findByActivity(parentView.getCurrentActivity()));
	}
}
