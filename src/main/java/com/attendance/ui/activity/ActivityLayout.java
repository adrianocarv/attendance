package com.attendance.ui.activity;

import org.springframework.beans.factory.annotation.Autowired;

import com.attendance.backend.model.Activity;
import com.attendance.backend.repository.ActivityRepository;
import com.attendance.backend.repository.AttendanceRepository;
import com.attendance.backend.repository.CenterRepository;
import com.attendance.ui.util.IntegerConverter;
import com.attendance.ui.util.SafeButton;
import com.vaadin.data.BeanValidationBinder;
import com.vaadin.data.Binder;
import com.vaadin.data.BinderValidationStatus;
import com.vaadin.event.ShortcutAction;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.Page;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.Accordion;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

@SpringComponent
@UIScope
public class ActivityLayout extends CssLayout {

	private static final long serialVersionUID = 1L;

    /** Dependences */
	@Autowired private ActivityRepository activityRepository;
	@Autowired private CenterRepository centerRepository;
	@Autowired private AttendanceRepository attendanceRepository;

    /** Components */
	private Label labelCenter = new Label();
	private Label labelId = new Label();
	private Label labelTotalAttendance = new Label();
	private TextField name = new TextField("Nome");
	private TextField nameComplement = new TextField("Complemento");
	private TextField description = new TextField("Descrição");
    private CheckBox checkTitleRequired = new CheckBox("Requer título na marcação");
	private TextField personSuggestionByEvents = new TextField("Últimos eventos");
	private TextField personSuggestionByDays = new TextField("Últimos dias");
	private TextField resumoMensalId = new TextField("Id");
    private Button buttonSave = new Button("Salvar");
    private Button buttonCancel = new Button("Cancelar");
    private SafeButton buttonDelete = new SafeButton("Excluir", "Confirma a exclusão da atividade?");

    private Binder<Activity> binder = new BeanValidationBinder<>(Activity.class);

    private ActivityView parentView;
	private Activity current;
	
	public ActivityLayout() {
		
		buildLayout();
		configureComponents();
		hookLogicToComponents();

	}

    private void buildLayout(){
    	setVisible(false);
    	
    	setStyleName("product-form-wrapper");
    	
    	VerticalLayout vertical = new VerticalLayout(labelCenter, labelTotalAttendance, labelId, name, nameComplement, description, checkTitleRequired);
    	vertical.setMargin(false);
    	vertical.setStyleName("form-layout");
    	
    	VerticalLayout abaSuggestion = new VerticalLayout(personSuggestionByEvents, personSuggestionByDays);
    	VerticalLayout abaResumoMensal = new VerticalLayout(resumoMensalId);
    	Accordion accordion = new Accordion();
        accordion.addTab(abaSuggestion, "Sugerir Pessoas", VaadinIcons.CHECK_SQUARE_O);
        accordion.addTab(abaResumoMensal, "Resumo Mensal", VaadinIcons.FILE_TEXT_O);
        vertical.addComponents(accordion, buttonSave, buttonCancel, buttonDelete, buttonDelete);
    	
    	addComponent(vertical);
	}
	private void configureComponents() {
        
		// bind using naming convention
        binder.forField(personSuggestionByEvents).withConverter(new IntegerConverter()).bind("personSuggestionByEvents");
        binder.forField(personSuggestionByDays).withConverter(new IntegerConverter()).bind("personSuggestionByDays");
        binder.forField(resumoMensalId).withConverter(new IntegerConverter()).bind("resumoMensalId");
        binder.bindInstanceFields(this);

		name.setSizeFull();
		nameComplement.setSizeFull();
		description.setSizeFull();
    	checkTitleRequired.setSizeFull();
		
		buttonSave.addStyleName(ValoTheme.BUTTON_PRIMARY);
        buttonCancel.addStyleName("cancel");
        buttonDelete.addStyleName(ValoTheme.BUTTON_DANGER);
        
        buttonSave.setClickShortcut(ShortcutAction.KeyCode.ENTER);
        buttonCancel.setClickShortcut(ShortcutAction.KeyCode.ESCAPE);

	}

	private void hookLogicToComponents() {

		buttonSave.addClickListener(e -> {
			if (save()) parentView.enter(null);
		});

		buttonCancel.addClickListener(e -> {
			current = null;
			parentView.enter(null);
		});

	    ClickListener buttonDeleteListener = new ClickListener() {
	        private static final long serialVersionUID = 1L;
	        @Override
	        public void buttonClick(ClickEvent event) {
				if (delete()) parentView.enter(null);
	        }
	      };
	      buttonDelete.setYesListener(buttonDeleteListener);
	}

	private boolean save() {
    	BinderValidationStatus<Activity> status = binder.validate();
    	if(!status.hasErrors()){
			activityRepository.save(current);
			new Notification(null,"Atividade salva.", Notification.Type.HUMANIZED_MESSAGE, true).show(Page.getCurrent());
			return true;
		} else{
			new Notification(null, status.getValidationErrors().get(0).getErrorMessage(), Notification.Type.ERROR_MESSAGE, true).show(Page.getCurrent());
			return false;
		}
	}

	private boolean delete() {

		if (current.getTotalAttendance() == 0) {
			activityRepository.delete(current);
			new Notification(null,"Atividade excluída.", Notification.Type.HUMANIZED_MESSAGE, true).show(Page.getCurrent());
			return true;
		} else {
			new Notification(null, "Exclusão não permitidas, pois há presenças atreladas a atividade.", Notification.Type.WARNING_MESSAGE, true).show(Page.getCurrent());
			return false;
		}
	}

	public void enter(ActivityView parentView, Activity activity) {
    	this.parentView = parentView;
    	this.current = activity;
    	
		final boolean persisted = activity.getId() != null;
		if(persisted){
			current = activityRepository.findOne(activity.getId());
			current.setCenter(centerRepository.findOne(1L));
			current.setTotalAttendance(attendanceRepository.findByActivity(current).size());
		}else{
			current = activity;

			//TODO Tornar dinâmico
			 current.setCenter(centerRepository.findOne(1L));
		}
		
		binder.setBean(current);

		labelCenter.setValue("Centro: " + current.getCenter().getName());
		labelTotalAttendance.setValue("Presenças: " + current.getTotalAttendance());
	    labelId.setValue("Id: " + current.getId());
		
		setVisible(true);

		// A hack to ensure the whole form is visible
		buttonSave.focus();
		name.selectAll();
    }
}
