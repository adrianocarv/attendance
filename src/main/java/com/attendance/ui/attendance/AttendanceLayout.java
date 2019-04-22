package com.attendance.ui.attendance;

import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.attendance.backend.model.Activity;
import com.attendance.backend.model.ActivityAttendance;
import com.attendance.backend.model.Attendance;
import com.attendance.backend.model.Person;
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
import com.vaadin.ui.renderers.HtmlRenderer;
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
	private DateField fieldNewDate = new DateField();
	private TextField fieldTitle = new TextField();
	private Button buttonTotal = new Button();
	private Button buttonSave = new Button(VaadinIcons.CHECK);
	private Button buttonEdit = new Button(VaadinIcons.EDIT);
	private Button buttonNewPerson = new Button(VaadinIcons.PLUS_CIRCLE);
	private Button buttonChangeDate = new Button(VaadinIcons.CALENDAR);
	private Button buttonNewAttendance = new Button(VaadinIcons.CHECK_SQUARE_O);
	private Button buttonBack = new Button(VaadinIcons.ARROW_BACKWARD);
	private Button buttonEvents = new Button(VaadinIcons.USER_CHECK);
	private Grid<Attendance> grid = new Grid<>(Attendance.class);
	
	HorizontalLayout titleLayout = new HorizontalLayout(fieldTitle);
	HorizontalLayout newDateLayout = new HorizontalLayout(fieldNewDate);

	private AttendanceView parentView;
    private Activity currentActivity;
    private LocalDate currentDate;
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

    	HorizontalLayout titleAndDate = new HorizontalLayout(labelActivity);
    	HorizontalLayout actions = new HorizontalLayout(buttonTotal, buttonSave, buttonEdit, buttonNewPerson, buttonChangeDate, buttonNewAttendance, buttonBack, buttonEvents);
    	VerticalLayout layoutTop = new VerticalLayout(titleAndDate, newDateLayout, titleLayout, actions);
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
		grid.removeAllColumns();
		grid.addColumn("name", new HtmlRenderer()).setCaption("Nome").setResizable(false).setSortable(false);
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
		
		buttonChangeDate.addClickListener(e -> {
			newDateLayout.setVisible(true);
			buttonChangeDate.setVisible(false);
			fieldNewDate.setValue(currentDate);
			fieldNewDate.focus();
		});

		buttonBack.addClickListener(e -> parentView.enter(null));

		buttonNewAttendance.addClickListener(e -> {
			this.enter(this.parentView, this.currentActivity);
			newDateLayout.setVisible(true);
			buttonChangeDate.setVisible(false);
			fieldNewDate.setValue(currentDate);
			fieldNewDate.focus();
		});
	}

	public void enter(AttendanceView parentView, Activity a) {
    	this.parentView = parentView;
    	this.currentActivity = a;

    	this.parentView.getVertical().setVisible(false);
    	setVisible(true);
    	
		labelActivity.setValue(currentActivity.getName() + ": " + this.getCurrentDateAsString());
		currentDate = new Date(System.currentTimeMillis()).toLocalDate();
		labelActivity.setValue(currentActivity.getName() + ": " + this.getCurrentDateAsString());
		fieldTitle.setValue("");

		editMode = true;
    	this.refreshEditModeComponents();
    }
    
	private void refreshEditModeComponents(){
    	
    	//security
    	if(!CurrentUser.isUserInActivityWrite(currentActivity)) editMode = false;
		
		fieldTitle.setEnabled(editMode);
		titleLayout.setVisible(currentActivity.isCheckTitleRequired());
		buttonSave.setVisible(editMode);
   		buttonEdit.setVisible(!editMode);
   		buttonNewPerson.setVisible(editMode);
   		buttonChangeDate.setVisible(editMode);
   		buttonNewAttendance.setVisible(!editMode);
   		buttonBack.setVisible(!editMode);
		
		//reset change date elements
   		newDateLayout.setVisible(false);
		fieldNewDate.clear();
		
		if(editMode){
   			grid.setSelectionMode(SelectionMode.MULTI);
   			grid.asMultiSelect().addValueChangeListener(e -> {
   		   		String today = DateUtils.isSameDay(Date.valueOf(currentDate), new Date(System.currentTimeMillis())) ? " HJ" : "";
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
			List<Attendance> attendances = attendanceRepository.findByDateAndActivity(Date.valueOf(currentDate), currentActivity);

			//DB Suggestions People
			List<Person> people = activityAttendanceRepository.findByLastAttendances(currentActivity);
			for (Person person : people) {
				Attendance attendance = new Attendance(currentActivity, person, Date.valueOf(currentDate));
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
			List<Attendance> attendances = attendanceRepository.findByDateAndActivity(Date.valueOf(currentDate), currentActivity);
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
		Attendance newAttendance = new Attendance(currentActivity, person, Date.valueOf(currentDate));
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
   		String today = DateUtils.isSameDay(Date.valueOf(currentDate), new Date(System.currentTimeMillis())) ? " HJ" : "";
		buttonTotal.setCaption(total+today);
		grid.sort("name");
	}
	
	private boolean persistAttendance() {

		//Required title check
		fieldTitle.setValue(StringUtils.trim(fieldTitle.getValue()));
		if(!grid.getSelectedItems().isEmpty() && currentActivity.isCheckTitleRequired() && fieldTitle.getValue().length() < 2){
			new Notification(null, "TÍTULO é requerido para esta atividade.", Notification.Type.WARNING_MESSAGE, true).show(Page.getCurrent());
			fieldTitle.focus();
			return false;
		}
			
		//Required new date check
		if(newDateLayout.isVisible()){
			if(fieldNewDate.isEmpty()){
				new Notification(null, "Informe a NOVA DATA para esta atividade.", Notification.Type.WARNING_MESSAGE, true).show(Page.getCurrent());
				fieldNewDate.focus();
				return false;
			}
			if(!fieldNewDate.getValue().equals(currentDate) ){
				List<Attendance> newDateList = attendanceRepository.findByDateAndActivity(Date.valueOf(fieldNewDate.getValue()), currentActivity);
				if(!newDateList.isEmpty()){
					new Notification(null, "Alteração não permitida, pois a atividade já tem " + newDateList.size() + " presenças na nova data." , Notification.Type.ERROR_MESSAGE, true).show(Page.getCurrent());
					fieldNewDate.focus();
					return false;
				}
			}
		}
			
		//Remove all first
		List<Attendance> savedList = attendanceRepository.findByDateAndActivity(Date.valueOf(currentDate), currentActivity);
		for (Attendance a : savedList) {
			attendanceRepository.delete(a);
		}
	
		//Change date logic
		if(newDateLayout.isVisible()){
			currentDate = fieldNewDate.getValue();
			labelActivity.setValue(currentActivity.getName() + ": " + this.getCurrentDateAsString());
		}
			
		//Insert all selected, if not exists
		for (Attendance a : grid.getSelectedItems()) {

			a.setId(null);
			a.setDate(Date.valueOf(currentDate));
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
    	currentDate = selectedDate;
		labelActivity.setValue(currentActivity.getName() + ": " + this.getCurrentDateAsString());
		fieldTitle.setValue(title == null ? "" : title);
		editMode = false;
    	this.refreshEditModeComponents();
    }
    
    private String getCurrentDateAsString(){
    	if (this.currentDate == null)
   			return "";

    	DateTimeFormatter formatters = DateTimeFormatter.ofPattern("dd/MM/yyyy (EEE)");
    	String str = currentDate.format(formatters);
    	return str;
    }
}
