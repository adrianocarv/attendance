package com.attendance.ui.attendance;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.attendance.backend.model.Activity;
import com.attendance.backend.model.Attendance;
import com.attendance.backend.model.Person;
import com.attendance.backend.repository.ActivityAttendanceRepository;
import com.attendance.backend.repository.AttendanceRepository;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.Page;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.Button;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.DateField;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.SelectionMode;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

@SpringComponent
@UIScope
public class AttendanceLayout extends CssLayout {

	private static final long serialVersionUID = 1L;

    /** Dependences */
	@Autowired private AttendanceRepository attendanceRepository;
	@Autowired private ActivityAttendanceRepository activityAttendanceRepository;
	private final AttendancePersonLayout attendancePersonLayout;
	private final AttendanceActivityLayout attendanceActivityLayout;

    /** Components */
	private Label labelActivity = new Label(); 
	private DateField fieldDate = new DateField();
	private Button buttonEvents = new Button(VaadinIcons.USER_CHECK);
	private Button buttonSave = new Button(VaadinIcons.CHECK);
	private Button buttonEdit = new Button(VaadinIcons.EDIT);
	private Button buttonNewPerson = new Button(VaadinIcons.PLUS_CIRCLE);
	private Button buttonBack = new Button(VaadinIcons.ARROW_BACKWARD);
	private Button buttonTotal = new Button();
	private Grid<Attendance> grid = new Grid<>(Attendance.class);
	
    private AttendanceView parentView;
    private Activity currentActivity;
	private boolean editMode = true;
	private List<Attendance> gridList = null;
	
	@Autowired
	public AttendanceLayout(AttendancePersonLayout attendancePersonLayout, AttendanceActivityLayout attendanceActivityLayout) {
		this.attendancePersonLayout = attendancePersonLayout;
		this.attendanceActivityLayout = attendanceActivityLayout;
		
		buildLayout();
		configureComponents();
		hookLogicToComponents();
	}

    private void buildLayout(){
    	setVisible(false);

        setSizeFull();
        addStyleName("crud-view");

    	HorizontalLayout titleAndDate = new HorizontalLayout(labelActivity, fieldDate);
    	HorizontalLayout actions = new HorizontalLayout(buttonEvents, buttonSave, buttonEdit, buttonNewPerson, buttonBack, buttonTotal);
    	VerticalLayout layoutTop = new VerticalLayout(titleAndDate, actions);
    	VerticalLayout layoutTopAndGrig = new VerticalLayout(layoutTop, grid);

    	actions.setSpacing(true);
    	
    	layoutTop.setWidth("100%");
    	layoutTop.setSpacing(true);
    	layoutTop.setMargin(false);
    	layoutTop.setStyleName("top-bar");
    	
    	layoutTopAndGrig.setMargin(false);
    	layoutTopAndGrig.setSizeFull();
        layoutTopAndGrig.setExpandRatio(grid, 1);
        layoutTopAndGrig.setStyleName("crud-main-layout");
        
        addComponents(attendancePersonLayout, attendanceActivityLayout, layoutTopAndGrig);
	}
	
	private void configureComponents() {
        
		labelActivity.setStyleName(ValoTheme.LABEL_BOLD);
		
		buttonEvents.setStyleName(ValoTheme.BUTTON_BORDERLESS_COLORED);
		buttonSave.setStyleName(ValoTheme.BUTTON_FRIENDLY);
		buttonEdit.setStyleName(ValoTheme.BUTTON_PRIMARY);
		buttonNewPerson.setStyleName(ValoTheme.BUTTON_PRIMARY);
		buttonBack.setStyleName("cancel");
		buttonTotal.setStyleName(ValoTheme.BUTTON_BORDERLESS_COLORED);

		grid.setSizeFull();
		grid.setColumns("name");
		grid.getColumn("name").setCaption("Nome").setResizable(false).setSortable(false);
	}

	private void hookLogicToComponents() {

		buttonEvents.addClickListener(e -> attendanceActivityLayout.enter(this));
		
		buttonSave.addClickListener(e -> {
			editMode = false;
			persistAttendance();
			refreshEditModeComponents();	
		});
		
		buttonEdit.addClickListener(e -> {
			editMode = true;
			refreshEditModeComponents();	
		});
		
		buttonNewPerson.addClickListener(e -> attendancePersonLayout.enter(this));
		
		buttonBack.addClickListener(e -> parentView.enter(null));
		
		fieldDate.addFocusListener(e -> {
			buttonSave.setVisible(false);
		});
		
		fieldDate.addBlurListener(e -> {
			buttonSave.setVisible(true);
		});

		fieldDate.addValueChangeListener(e -> {
			refreshEditModeComponents();	
		});
	}

	public void enter(AttendanceView parentView, Activity a) {
    	this.parentView = parentView;
    	this.currentActivity = a;

    	this.parentView.getVertical().setVisible(false);
    	setVisible(true);
    	
		labelActivity.setValue(currentActivity.getName());
		if(fieldDate.isEmpty())
			fieldDate.setValue(new Date(System.currentTimeMillis()).toLocalDate());

		editMode = true;
    	this.refreshEditModeComponents();
    }
    
	private void refreshEditModeComponents(){

		fieldDate.setEnabled(editMode);
		buttonSave.setVisible(editMode);
   		buttonEdit.setVisible(!editMode);
   		buttonNewPerson.setVisible(editMode);
		
   		if(editMode){
   			grid.setSelectionMode(SelectionMode.MULTI);
   			grid.asMultiSelect().addValueChangeListener(e -> {
   		   		buttonTotal.setCaption(grid.getSelectedItems().size()+"");
   			});
   		}else{
   			grid.setSelectionMode(SelectionMode.SINGLE);
   		}

   		this.loadGridList();
	}
	
	private void loadGridList() {
		
		if(editMode){
			//DB Persisted People
			List<Attendance> attendances = attendanceRepository.findByDateAndActivity(Date.valueOf(fieldDate.getValue()), currentActivity);

			//DB Suggestions People
			List<Person> people = activityAttendanceRepository.findByLastAttendances(currentActivity);
			for (Person person : people) {
				Attendance attendance = new Attendance(currentActivity, person, Date.valueOf(fieldDate.getValue()));
				if(!attendances.contains(attendance))
					attendances.add(attendance);
			}
			
			gridList = attendances;
		} else{
			//DB Persisted People
			List<Attendance> attendances = attendanceRepository.findByDateAndActivity(Date.valueOf(fieldDate.getValue()), currentActivity);
			gridList = attendances;
		}
		
		this.updateGrid();
	}
	
	void addPerson(Person person) {

		//First, mark presence from selected itens
		for (Attendance a : gridList){
			boolean selected = grid.getSelectedItems().contains(a);
			a.setPresent(selected);
		}

		//Update ou Add new Person on gridList
		Attendance newAttendance = new Attendance(currentActivity, person, Date.valueOf(fieldDate.getValue()));
		newAttendance.setPresent(true);

		if(gridList.contains(newAttendance)){
			for (Attendance a : gridList){
				if(a.equals(newAttendance)){
					a.setPresent(true);
					break;
				}
			}
		}else{
			gridList.add(newAttendance);
		}

		this.updateGrid();

		new Notification(null,"<b>" + person.getName() + "</b> adicionado na lista de presenças.", Notification.Type.HUMANIZED_MESSAGE, true).show(Page.getCurrent());
	}
	
	private void updateGrid(){
		int total = 0;
		grid.setItems(gridList);
		for (Attendance a : gridList){
			if(a.isPresent()){
				grid.select(a);
				total++;
			}
		}
		buttonTotal.setCaption(total+"");
		grid.sort("name");
	}
	
	private void persistAttendance() {

		//Remove all first
		List<Attendance> savedList = attendanceRepository.findByDateAndActivity(Date.valueOf(fieldDate.getValue()), currentActivity);
		for (Attendance a : savedList) {
			attendanceRepository.delete(a);
		}
		
		//Insert all selected, if not exists
		for (Attendance a : grid.getSelectedItems()) {

			//Skip if exists
			boolean isSaved = attendanceRepository.findByDateAndActivityAndPerson(Date.valueOf(fieldDate.getValue()), currentActivity, a.getPerson()).size() > 0;
			if(isSaved) continue;

			a.setId(null);
			a.setDate(Date.valueOf(fieldDate.getValue()));
			attendanceRepository.save(a);
		}
	}

	Activity getCurrentActivity() {
		return currentActivity;
	}
	
    void selectAttendanceActivity(LocalDate selectedDate) {
		fieldDate.setValue(selectedDate);
		editMode = false;
    	this.refreshEditModeComponents();
    }
}
