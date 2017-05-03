package com.attendance.backend.web;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.attendance.backend.model.ActivityAttendance;
import com.attendance.backend.model.Attendance;
import com.attendance.backend.model.Center;
import com.attendance.backend.model.Person;
import com.attendance.backend.repository.ActivityAttendanceRepository;
import com.attendance.backend.repository.ActivityRepository;
import com.attendance.backend.repository.AttendanceRepository;
import com.attendance.backend.repository.PersonRepository;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.Page;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.shared.ui.ValueChangeMode;
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

/**
 * A simple example to introduce building forms. As your real application is
 * probably much more complicated than this example, you could re-use this form in
 * multiple places. This example component is only used in VaadinUI.
 * <p>
 * In a real world application you'll most likely using a common super class for all your
 * forms - less code, better UX. See e.g. AbstractForm in Virin
 * (https://vaadin.com/addon/viritin).
 */
@SpringComponent
@UIScope
public class AttendanceEditorUI extends VerticalLayout {

	private static final long serialVersionUID = 1L;

	private final ActivityRepository activityRepository;
	private final AttendanceRepository attendanceRepository;
	private final ActivityAttendanceRepository activityAttendanceRepository;
	private final PersonRepository personRepository;

	private ActivityAttendance activityAttendance;
	private boolean editMode = false;
	
	// Fields
	private Label activityTitle = new Label("");
	private DateField date = new DateField();
	private Label totalPresent = new Label("");
	private VerticalLayout editFrame = new VerticalLayout(activityTitle, new HorizontalLayout(date, totalPresent));
	private Grid<Attendance> grid = new Grid<>(Attendance.class);
	
	/* Action buttons */
	private Button edit = new Button("Editar", VaadinIcons.EDIT);
	private Button back = new Button("Voltar", VaadinIcons.ARROW_BACKWARD);
	private Button save = new Button("Salvar", VaadinIcons.CHECK);
	private Button cancel = new Button("Cancelar", VaadinIcons.REPLY);
	CssLayout actions = new CssLayout(edit, back, save, cancel);
	
	//Add Person
	private TextField filter = new TextField();
	private Grid<Person> personGrid = new Grid<>(Person.class);
	private final Button newPerson = new Button("Adicionar Nome", VaadinIcons.PLUS);
	private TextField person = new TextField();
	VerticalLayout personFrame = new VerticalLayout(filter, personGrid, new HorizontalLayout(person, newPerson));

	@Autowired
	public AttendanceEditorUI(ActivityRepository activityRepository, AttendanceRepository attendanceRepository, ActivityAttendanceRepository activityAttendanceRepository, PersonRepository personRepository) {
		this.activityRepository = activityRepository;
		this.attendanceRepository = attendanceRepository;
		this.activityAttendanceRepository = activityAttendanceRepository;
		this.personRepository = personRepository;

		// Build layout
		setVisible(false);
		addComponents(editFrame, actions, grid, personFrame);

		// Configure and style components
		this.configureComponents();

		
		// Hook logic to components
		this.configureComponentsLogic();
	}

	public interface ChangeHandler {

		void onChange();
	}

	private void configureComponents() {
		totalPresent.setContentMode(ContentMode.HTML);

		actions.setStyleName(ValoTheme.LAYOUT_COMPONENT_GROUP);
		save.setStyleName(ValoTheme.BUTTON_PRIMARY);

		grid.setHeight(600, Unit.PIXELS);
		//grid.setWidth("100%");
		grid.setSelectionMode(SelectionMode.MULTI);
		grid.setColumns("name");
		grid.getColumn("name").setCaption("Nome").setResizable(false).setSortable(false);//.setWidth(300);

		personGrid.setHeight(300, Unit.PIXELS);
		personGrid.setColumns("name");
		personGrid.getColumn("name").setCaption("Nome").setResizable(false).setSortable(false);

		filter.setPlaceholder("Procurar por nome...");
		person.setPlaceholder("Pessoa nova...");
	}
	
	private void configureComponentsLogic() {
		//Attendance Selected
		grid.addSelectionListener(e -> {
			if(!editMode) return;
			
			totalPresent.setValue(grid.getSelectedItems().size() + "  " + VaadinIcons.GROUP.getHtml());
		});

		//Change Date
		date.addValueChangeListener(e -> {
			this.activityAttendance.setDate(Date.valueOf(date.getValue()));
			loadAttendanceGrid();
		});
		
		// wire action buttons
		back.addClickListener(e -> this.setVisible(false));
		edit.addClickListener(e ->{
			editMode = true;
			loadAttendanceGrid();
		});
		cancel.addClickListener(e ->{
			editMode = false;
			loadAttendanceGrid();
		});
		save.addClickListener(e ->{
			editMode = false;
			persistAttendance();
			loadAttendanceGrid();
		});
		
		// Replace listing with filtered content when user changes filter
		filter.setValueChangeMode(ValueChangeMode.LAZY);
		filter.addValueChangeListener(e -> loadPersonGrid(e.getValue()));
		
		//Person selected
		personGrid.asSingleSelect().addValueChangeListener(e -> {
			if(e.getValue() == null)
				return;
			
			//Persist Attendance before
			persistAttendance();
			loadAttendanceGrid();
			

			Attendance attendance = new Attendance(activityAttendance.getActivity(), e.getValue(), activityAttendance.getDate());

			boolean exists = false;
			for(Attendance saved : grid.getSelectedItems()){
				if(attendance.equals(saved)){
				    exists = true;
				    break;
				}
			}

			if(exists)
				new Notification(null,"<b>" + e.getValue().getName() + "</b> já está na lista de presenças.", Notification.Type.WARNING_MESSAGE, true).show(Page.getCurrent());
			else{
				attendanceRepository.save(attendance);
				new Notification(null,"<b>" + e.getValue().getName() + "</b> adicionado na lista de presenças.", Notification.Type.HUMANIZED_MESSAGE, true).show(Page.getCurrent());
				loadAttendanceGrid();
			}
			
		});
		
		newPerson.addClickListener(e -> this.saveNewPerson());
	}

	public final void edit(ActivityAttendance a) {
		
		this.activityAttendance = a;
		this.activityAttendance.setActivity(activityRepository.findOne(a.getActivity().getId()));
		if(this.activityAttendance.getDate() == null){
			this.activityAttendance.setDate(new Date(System.currentTimeMillis()));
			editMode = true;
		} else{
			editMode = false;
		}
		
		loadAttendanceGrid();
		setVisible(true);
		grid.focus();
	}

	public void setChangeHandler(ChangeHandler h) {
		back.addClickListener(e -> h.onChange());
	}

	private void loadAttendanceGrid() {
		
		//DB Persisted People
		List<Attendance> attendances = attendanceRepository.findByDateAndActivity(activityAttendance.getDate(), activityAttendance.getActivity());

		totalPresent.setValue(attendances.size() + "  " + VaadinIcons.GROUP.getHtml());

		//DB Seggestions People
		if(this.editMode){
			List<Person> people = activityAttendanceRepository.findByLastAttendances(activityAttendance.getActivity());
			for (Person person : people) {
				Attendance attendance = new Attendance(activityAttendance.getActivity(), person, activityAttendance.getDate());
				if(!attendances.contains(attendance))
					attendances.add(attendance);
			}
		}

		//Update grid and select Elements
		grid.setItems(attendances);;
		for (Attendance a : attendances)
			if(a.isPresent()) grid.select(a);
		grid.sort("name");
		
		refreshEditModeComponents();
	}
	
	private void refreshEditModeComponents(){
		activityTitle.setValue(this.activityAttendance.getActivityTitle());
		date.setValue(this.activityAttendance.getDate().toLocalDate());

		date.setEnabled(editMode);
		edit.setVisible(!editMode);
		back.setVisible(!editMode);
		save.setVisible(editMode);
		cancel.setVisible(editMode);

		grid.setEnabled(editMode);

		personFrame.setVisible(editMode);
	}
	
	private void persistAttendance() {

		//Remove all first
		List<Attendance> savedList = attendanceRepository.findByDateAndActivity(activityAttendance.getDate(), activityAttendance.getActivity());
		for (Attendance a : savedList) {
			attendanceRepository.delete(a);
		}
		
		//Insert all selected, if not exists
		for (Attendance a : grid.getSelectedItems()) {

			//Skip if exists
			boolean isSaved = attendanceRepository.findByDateAndActivityAndPerson(activityAttendance.getDate(), activityAttendance.getActivity(), a.getPerson()).size() > 0;
			if(isSaved) continue;

			a.setId(null);
			a.setDate(this.activityAttendance.getDate());
			attendanceRepository.save(a);
		}
	}
	
	private void loadPersonGrid(String filterText) {

		if (StringUtils.isEmpty(filterText))
			personGrid.setItems(new ArrayList<Person>());
		else
			personGrid.setItems(personRepository.findByNameStartsWithIgnoreCaseOrderByNameAsc("%"+filterText));
	}
	
	private void saveNewPerson() {
	
		String name = person.getValue() == null ? "": person.getValue().trim();

		//Blank name, ignore
		if(StringUtils.isEmpty(name)){
			Notification.show("Informe o nome da pessoa nova...");
			person.setValue("");
			person.selectAll();
		    return;
		}

		//Verify if exists
		Center center = this.activityAttendance.getActivity().getCenter();
		boolean isSaved = personRepository.findByNameIgnoreCaseAndCenter(name, center).size() > 0;
		if(isSaved){
			new Notification(null,"<b>" + name + "</b> já existe na base.", Notification.Type.WARNING_MESSAGE, true).show(Page.getCurrent());
			person.setValue("");
			filter.setValue(name);
			filter.focus();
			return;
		}

		//Save New Person
		Person newPerson = new Person(name, center);
		personRepository.save(newPerson);

		Attendance attendance = new Attendance(activityAttendance.getActivity(), newPerson, activityAttendance.getDate());
		attendanceRepository.save(attendance);

		new Notification(null,"<b>" + name + "</b> adicionado na lista de presenças.", Notification.Type.HUMANIZED_MESSAGE, true).show(Page.getCurrent());
		person.setValue("");
		filter.setValue("");
		loadAttendanceGrid();
	}
}
