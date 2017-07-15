package com.attendance.ui.attendance;

import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.attendance.backend.model.Activity;
import com.attendance.backend.model.ActivityAttendance;
import com.attendance.backend.model.Attendance;
import com.attendance.backend.model.Person;
import com.attendance.backend.model.SharingType;
import com.attendance.backend.repository.ActivityAttendanceRepository;
import com.attendance.backend.repository.ActivityRepository;
import com.attendance.backend.repository.AttendanceRepository;
import com.attendance.backend.repository.PersonRepository;
import com.attendance.ui.authentication.CurrentUser;
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
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

@SpringComponent
@UIScope
public class AttendanceLayout extends CssLayout {

	private static final long serialVersionUID = 1L;

    /** Dependences */
	@Autowired private AttendanceRepository attendanceRepository;
	@Autowired private ActivityAttendanceRepository activityAttendanceRepository;
	@Autowired private PersonRepository personRepository;
	@Autowired private ActivityRepository activityRepository;
	private final AttendancePersonLayout attendancePersonLayout;
	private final AttendanceActivityLayout attendanceActivityLayout;

    /** Components */
	private Label labelActivity = new Label(); 
	private DateField fieldDate = new DateField();
	private TextField fieldTitle = new TextField();
	private Button buttonEvents = new Button(VaadinIcons.USER_CHECK);
	private Button buttonSave = new Button(VaadinIcons.CHECK);
	private Button buttonEdit = new Button(VaadinIcons.EDIT);
	private Button buttonNewPerson = new Button(VaadinIcons.PLUS_CIRCLE);
	private Button buttonBack = new Button(VaadinIcons.ARROW_BACKWARD);
	private Button buttonTotal = new Button();
	private Grid<Attendance> grid = new Grid<>(Attendance.class);
	
	HorizontalLayout titleLayout = new HorizontalLayout(fieldTitle);

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
    	VerticalLayout layoutTop = new VerticalLayout(titleAndDate, titleLayout, actions);
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
        fieldTitle.setPlaceholder("Título requerido");
		
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
			if(persistAttendance()){
				editMode = false;
				refreshEditModeComponents();
			}
		});
		
		buttonEdit.addClickListener(e -> {
			editMode = true;
			refreshEditModeComponents();	
		});
		
		buttonNewPerson.addClickListener(e -> attendancePersonLayout.enter(this));
		
		buttonBack.addClickListener(e -> parentView.enter(null));
		
		fieldDate.addValueChangeListener(e -> {
			if(fieldDate.getValue() == null)
				fieldDate.setValue(new Date(System.currentTimeMillis()).toLocalDate());

			refreshEditModeComponents();	
		});
	}

	public void enter(AttendanceView parentView, Activity a) {
    	this.parentView = parentView;
    	this.currentActivity = a;

    	this.parentView.getVertical().setVisible(false);
    	setVisible(true);
    	
		labelActivity.setValue(currentActivity.getName());
		fieldDate.setValue(new Date(System.currentTimeMillis()).toLocalDate());
		fieldTitle.setValue("");

		editMode = true;
    	this.refreshEditModeComponents();
    }
    
	private void refreshEditModeComponents(){
    	
    	//security
    	if(!CurrentUser.isUserInRoleAndActivity(SharingType.ATTENDANCE_WRITE, currentActivity)) editMode = false;
		
		fieldDate.setEnabled(!editMode);
		fieldTitle.setEnabled(editMode);
		titleLayout.setVisible(currentActivity.isCheckTitleRequired());
		buttonSave.setVisible(editMode);
   		buttonEdit.setVisible(!editMode);
   		buttonNewPerson.setVisible(editMode);
		
   		if(editMode){
   			grid.setSelectionMode(SelectionMode.MULTI);
   			grid.asMultiSelect().addValueChangeListener(e -> {
   		   		String today = DateUtils.isSameDay(Date.valueOf(fieldDate.getValue()), new Date(System.currentTimeMillis())) ? " HJ" : "";
   				buttonTotal.setCaption(grid.getSelectedItems().size()+today);
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
				if(!attendances.contains(attendance)){
					Person personDB = personRepository.findOne(person.getId());
					attendance.setPerson(personDB);
					attendances.add(attendance);
				}
			}
			
			gridList = attendances;

			//Set Title
			String title = attendances.isEmpty() ? "" : attendances.get(0).getTitle(); 
			fieldTitle.setValue(title == null ? "" : title);
		} else{
			//DB Persisted People
			List<Attendance> attendances = attendanceRepository.findByDateAndActivity(Date.valueOf(fieldDate.getValue()), currentActivity);
			gridList = attendances;
			
			//Set Title
			String title = attendances.isEmpty() ? "" : attendances.get(0).getTitle(); 
			fieldTitle.setValue(title == null ? "" : title);
		}
		
		this.updateGrid();
	}
	
	void addPerson(Person person) {

		//First, mark presence from selected itens
		for (Attendance a : gridList){
			boolean selected = grid.getSelectedItems().contains(a);
			a.setPresent(selected);
		}

		//Update or Add new Person on gridList
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
   		String today = DateUtils.isSameDay(Date.valueOf(fieldDate.getValue()), new Date(System.currentTimeMillis())) ? " HJ" : "";
		buttonTotal.setCaption(total+today);
		grid.sort("name");
	}
	
	private boolean persistAttendance() {

		//Required title check
		fieldTitle.setValue(StringUtils.trim(fieldTitle.getValue()));
		if(currentActivity.isCheckTitleRequired() && fieldTitle.getValue().length() < 2){
			new Notification(null, "Título é requerido para esta atividade.", Notification.Type.ERROR_MESSAGE, true).show(Page.getCurrent());
			fieldTitle.focus();
			return false;
		}
			
		//Remove all first
		List<Attendance> savedList = attendanceRepository.findByDateAndActivity(Date.valueOf(fieldDate.getValue()), currentActivity);
		for (Attendance a : savedList) {
			attendanceRepository.delete(a);
		}
		
		//Insert all selected, if not exists
		for (Attendance a : grid.getSelectedItems()) {

			a.setId(null);
			a.setDate(Date.valueOf(fieldDate.getValue()));
			a.setTitle(StringUtils.isBlank(fieldTitle.getValue()) ? null : fieldTitle.getValue());
			a.setLastEditTime(new Timestamp(System.currentTimeMillis()));
			a.setLastEditUser(CurrentUser.getUser());
			
			attendanceRepository.save(a);
		}
		
		//Update last attendance info on respective Activity
		ActivityAttendance last = activityAttendanceRepository.findOneLastByActivity(currentActivity);
		if(last != null){
			Activity activityDB = activityRepository.findOne(currentActivity.getId());
			activityDB.setLastAttendanceDate(last.getDate());
			activityDB.setLastAttendanceTotal(last.getTotalPresent());
			activityRepository.save(activityDB);
		}
		
		return true;
	}

	Activity getCurrentActivity() {
		return currentActivity;
	}
	
    void selectAttendanceActivity(LocalDate selectedDate, String title) {
		fieldDate.setValue(selectedDate);
		fieldTitle.setValue(title == null ? "" : title);
		editMode = false;
    	this.refreshEditModeComponents();
    }
}
