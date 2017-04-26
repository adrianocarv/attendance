package com.attendance.backend.web;

import java.sql.Date;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;

import com.attendance.backend.model.ActivityAttendance;
import com.attendance.backend.model.Attendance;
import com.attendance.backend.model.Person;
import com.attendance.backend.repository.ActivityAttendanceRepository;
import com.attendance.backend.repository.ActivityRepository;
import com.attendance.backend.repository.AttendanceRepository;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.shared.ui.ContentMode;
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

	private ActivityAttendance activityAttendance;
	private boolean editMode = false;
	
	private Label activityTitle = new Label("");
	private DateField date = new DateField("Data");
	private Label totalPresent = new Label("");
	private VerticalLayout editFrame = new VerticalLayout(activityTitle, new HorizontalLayout(date, totalPresent));
	private Grid<Attendance> grid = new Grid<>(Attendance.class);
	
	/* Action buttons */
	private Button edit = new Button("Editar", VaadinIcons.EDIT);
	private Button back = new Button("Voltar", VaadinIcons.ARROW_BACKWARD);
	private Button save = new Button("Salvar", VaadinIcons.CHECK);
	private Button cancel = new Button("Cancelar", VaadinIcons.REPLY);
	CssLayout actions = new CssLayout(edit, back, save, cancel);

	@Autowired
	public AttendanceEditorUI(ActivityRepository activityRepository, AttendanceRepository attendanceRepository, ActivityAttendanceRepository activityAttendanceRepository) {
		this.activityRepository = activityRepository;
		this.attendanceRepository = attendanceRepository;
		this.activityAttendanceRepository = activityAttendanceRepository;

		// Build layout
		setVisible(false);
		addComponents(editFrame, actions, grid);

		// Configure and style components
		totalPresent.setContentMode(ContentMode.HTML);

		actions.setStyleName(ValoTheme.LAYOUT_COMPONENT_GROUP);
		save.setStyleName(ValoTheme.BUTTON_PRIMARY);

		grid.setHeight(600, Unit.PIXELS);
		//grid.setWidth("100%");
		grid.setSelectionMode(SelectionMode.MULTI);
		grid.setColumns("name");
		grid.getColumn("name").setCaption("Nome").setResizable(false).setSortable(false);//.setWidth(300);

		// Hook logic to components
		
		//Selected Activity
		grid.addSelectionListener(e -> {
			if(!editMode) return;
			
			totalPresent.setValue(grid.getSelectedItems().size() + "  " + VaadinIcons.GROUP.getHtml());

			Set<Attendance> selected = e.getAllSelectedItems();
		    Notification.show(selected.size() + " items selected");
		});

		//Change Date
		date.addValueChangeListener(e -> {
			this.activityAttendance.setDate(Date.valueOf(date.getValue()));
			editMode = false;
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
	}

	public interface ChangeHandler {

		void onChange();
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
		List<Attendance> attendances = attendanceRepository.findByDateAndActivity(activityAttendance.getDate(), activityAttendance.getActivity());

		if(this.editMode){
			List<Person> people = activityAttendanceRepository.findByLastAttendances(activityAttendance.getActivity());
			for (Person person : people) {
				Attendance attendance = new Attendance(activityAttendance.getActivity(), person, activityAttendance.getDate());
				if(!attendances.contains(attendance))
					attendances.add(attendance);
			}
		}

		grid.setItems(attendances);;
		for (Attendance a : attendances)
			if(a.isPresent()) grid.select(a);
		grid.sort("name");
		
		refreshEditModeComponents();
	}
	
	private void refreshEditModeComponents(){
		editFrame.setEnabled(editMode);
		edit.setVisible(!editMode);
		back.setVisible(!editMode);
		save.setVisible(editMode);
		cancel.setVisible(editMode);
		grid.setEnabled(editMode);
		
		activityTitle.setValue(this.activityAttendance.getActivityTitle());
		date.setValue(this.activityAttendance.getDate().toLocalDate());
		totalPresent.setValue(grid.getSelectedItems().size() + "  " + VaadinIcons.GROUP.getHtml());
	}
	
	private void persistAttendance() {

		//Remove all first
		List<Attendance> savedList = attendanceRepository.findByDateAndActivity(activityAttendance.getDate(), activityAttendance.getActivity());
		for (Attendance a : savedList) {
			attendanceRepository.delete(a);
		}
		
		//Insert all selected
		for (Attendance a : grid.getSelectedItems()) {
			a.setId(null);
			a.setDate(this.activityAttendance.getDate());
			attendanceRepository.save(a);
		}
	}
}
