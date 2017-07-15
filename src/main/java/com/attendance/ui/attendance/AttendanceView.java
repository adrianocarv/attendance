package com.attendance.ui.attendance;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.attendance.backend.model.Activity;
import com.attendance.backend.model.SharingType;
import com.attendance.backend.repository.ActivityRepository;
import com.attendance.ui.authentication.CurrentUser;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.SelectionMode;
import com.vaadin.ui.VerticalLayout;

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
	private VerticalLayout vertical;
	
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

    	vertical = new VerticalLayout(grid);
    	vertical.setSizeFull();
    	vertical.setMargin(true);
    	vertical.setStyleName("form-layout");
    	vertical.setExpandRatio(grid,  1);
    	
        addComponents(vertical, attendanceLayout);
	}
	
	private void configureComponents() {
		grid.setSizeFull();
		grid.setColumns("displayName");
		grid.getColumn("displayName").setCaption("Atividades").setResizable(false).setSortable(false);
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
    	
    	//security
    	if(!CurrentUser.isUserInRole(SharingType.ATTENDANCE_READ)) return;
    	
    	vertical.setVisible(true);
    	attendanceLayout.setVisible(false);

    	grid.setItems(this.findSharingActivities());
    }

	VerticalLayout getVertical() {
		return vertical;
	}
	
	private List<Activity> findSharingActivities(){
		List<Activity> all = activityRepository.findByCenter(CurrentUser.getCurrentCenter());		
		List<Activity> sharing = new ArrayList<Activity>();
		
		for(Activity a : all){
			if(CurrentUser.isUserInActivityRead(a)){
				sharing.add(a);
			}
		}
		return sharing;
	}
}
