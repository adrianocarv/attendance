package com.attendance.ui.person;

import org.springframework.beans.factory.annotation.Autowired;

import com.attendance.backend.model.Person;
import com.attendance.backend.model.PersonStatus;
import com.attendance.backend.model.SharingType;
import com.attendance.backend.repository.AttendanceRepository;
import com.attendance.backend.repository.PersonRepository;
import com.attendance.ui.authentication.CurrentUser;
import com.attendance.ui.util.LocalDateToSqlDateConverter;
import com.attendance.ui.util.RealConverter;
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
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.DateField;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

@SpringComponent
@UIScope
public class PersonLayout extends CssLayout {

	private static final long serialVersionUID = 1L;

    /** Dependences */
	@Autowired private PersonRepository personRepository;
	@Autowired private AttendanceRepository attendanceRepository;

    /** Components */
	private Label labelCenter = new Label();
	private Label labelId = new Label();
	private Label labelTotalAttendance = new Label();
	private TextField name = new TextField("Nome");
	private TextField shortName = new TextField("Nome curto");
	private TextField email = new TextField("E-mail");
    private TextField phone = new TextField("Celular");
    private DateField birthday = new DateField("Nascimento");
    private ComboBox<PersonStatus> status = new ComboBox<PersonStatus>("Status");
    
    private CheckBox checkUniversitario = new CheckBox("Universitário");
    private CheckBox checkColegial = new CheckBox("Colegial");
    private CheckBox checkCooperador = new CheckBox("Cooperador");
    private DateField checkCooperadorDate = new DateField();
    private CheckBox checkContribui = new CheckBox("Contribução financeira");
    private TextField checkContribuiValue = new TextField();
    
    private CheckBox checkEstudanteWA = new CheckBox("WhatsApp");
    private DateField checkEstudanteWADate = new DateField();
    private CheckBox checkEstudanteMail = new CheckBox("E-mail");
    private DateField checkEstudanteMailDate = new DateField();
    
    private CheckBox checkProfissionalWA = new CheckBox("WhatsApp");
    private DateField checkProfissionalWADate = new DateField();
    private CheckBox checkProfissionalMail = new CheckBox("E-mail");
    private DateField checkProfissionalMailDate = new DateField();

    private TextField tag1 = new TextField("Tag 1");
    private TextField tag2 = new TextField("Tag 2");
    private TextField tag3 = new TextField("Tag 3");
    private TextField tag4 = new TextField("Tag 4");
	
	private Button buttonSave = new Button("Salvar");
    private Button buttonCancel = new Button("Cancelar");
    private SafeButton buttonDelete = new SafeButton("Excluir", "Confirma a exclusão dessa pessoa?");

    private Binder<Person> binder = new BeanValidationBinder<>(Person.class);

    private PersonView parentView;
	private Person current;
	
	public PersonLayout() {
		
		buildLayout();
		configureComponents();
		hookLogicToComponents();

	}

    private void buildLayout(){
    	setVisible(false);
    	
    	setStyleName("product-form-wrapper");
    	
    	VerticalLayout vertical = new VerticalLayout();

    	vertical.addComponent(labelCenter);
    	vertical.addComponent(labelTotalAttendance);
    	vertical.addComponent(labelId);
    	vertical.addComponent(name);
    	vertical.addComponent(shortName);
    	vertical.addComponent(email);
    	vertical.addComponent(phone);
    	vertical.addComponent(birthday);
    	vertical.addComponent(checkUniversitario);
    	vertical.addComponent(checkColegial);
    	
    	VerticalLayout estudanteWA = new VerticalLayout(checkEstudanteWA, checkEstudanteWADate);
    	estudanteWA.setMargin(false);
    	estudanteWA.setSpacing(false);
    	VerticalLayout estudanteMail = new VerticalLayout(checkEstudanteMail, checkEstudanteMailDate);
    	estudanteMail.setMargin(false);
    	estudanteMail.setSpacing(false);
    	VerticalLayout abaEstudante = new VerticalLayout(estudanteWA, estudanteMail);

    	VerticalLayout profissionalWA = new VerticalLayout(checkProfissionalWA, checkProfissionalWADate);
    	profissionalWA.setMargin(false);
    	profissionalWA.setSpacing(false);
    	VerticalLayout profissionalMail = new VerticalLayout(checkProfissionalMail, checkProfissionalMailDate);
    	profissionalMail.setMargin(false);
    	profissionalMail.setSpacing(false);
    	VerticalLayout abaProfissional = new VerticalLayout(profissionalWA, profissionalMail);

    	VerticalLayout cooperador = new VerticalLayout(checkCooperador, checkCooperadorDate);
    	cooperador.setMargin(false);
    	cooperador.setSpacing(false);
    	VerticalLayout contribui = new VerticalLayout(checkContribui, checkContribuiValue);
    	contribui.setMargin(false);
    	contribui.setSpacing(false);
    	VerticalLayout abaCooperador = new VerticalLayout(cooperador, contribui);

    	VerticalLayout abaTags = new VerticalLayout(status, tag1, tag2, tag3, tag4);
    	abaTags.setSpacing(false);

    	Accordion accordion = new Accordion();
    	accordion.addStyleName(ValoTheme.ACCORDION_BORDERLESS);
        accordion.addTab(abaEstudante, "Estudante", VaadinIcons.ACADEMY_CAP);
    	accordion.addTab(abaProfissional, "Profissional", VaadinIcons.DOCTOR);
    	accordion.addTab(abaCooperador, "Cooperador", VaadinIcons.COINS);
    	accordion.addTab(abaTags, "Status e Tags", VaadinIcons.TAGS);
        vertical.addComponent(accordion);
        
    	vertical.addComponents(buttonSave, buttonCancel, buttonDelete);

    	vertical.setMargin(false);
    	vertical.setStyleName("form-layout");
    	
    	addComponent(vertical);
	}
	private void configureComponents() {
        
		// bind using naming convention
        binder.forField(birthday).withConverter(new LocalDateToSqlDateConverter()).bind("birthday");
        binder.forField(checkCooperadorDate).withConverter(new LocalDateToSqlDateConverter()).bind("checkCooperadorDate");
        binder.forField(checkEstudanteWADate).withConverter(new LocalDateToSqlDateConverter()).bind("checkEstudanteWADate");
        binder.forField(checkEstudanteMailDate).withConverter(new LocalDateToSqlDateConverter()).bind("checkEstudanteMailDate");
        binder.forField(checkProfissionalWADate).withConverter(new LocalDateToSqlDateConverter()).bind("checkProfissionalWADate");
        binder.forField(checkProfissionalMailDate).withConverter(new LocalDateToSqlDateConverter()).bind("checkProfissionalMailDate");
        binder.forField(checkContribuiValue).withConverter(new RealConverter()).bind("checkContribuiValue");
        binder.bindInstanceFields(this);

        birthday.setDateFormat("dd/MM/yyyy");
        checkCooperadorDate.setDateFormat("dd/MM/yyyy");
        checkEstudanteWADate.setDateFormat("dd/MM/yyyy");
        checkEstudanteMailDate.setDateFormat("dd/MM/yyyy");
        checkProfissionalWADate.setDateFormat("dd/MM/yyyy");
        checkProfissionalMailDate.setDateFormat("dd/MM/yyyy");
		
		name.setSizeFull();
    	shortName.setSizeFull();
    	email.setSizeFull();
    	phone.setSizeFull();
    	birthday.setSizeFull();
    	status.setSizeFull();
        
    	checkUniversitario.setSizeFull();
    	checkColegial.setSizeFull();
    	checkCooperador.setSizeFull();
    	checkCooperadorDate.setSizeFull();
    	checkContribui.setSizeFull();
    	checkContribuiValue.setSizeFull();
        
    	checkEstudanteWA.setSizeFull();
    	checkEstudanteWADate.setSizeFull();
    	checkEstudanteMail.setSizeFull();
    	checkEstudanteMailDate.setSizeFull();
        
    	checkProfissionalWA.setSizeFull();
    	checkProfissionalWADate.setSizeFull();
    	checkProfissionalMail.setSizeFull();
    	checkProfissionalMailDate.setSizeFull();

    	tag1.setSizeFull();
    	tag2.setSizeFull();
    	tag3.setSizeFull();
    	tag4.setSizeFull();
		
        status.setItems(PersonStatus.values());
        
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
    	BinderValidationStatus<Person> status = binder.validate();
    	if(!status.hasErrors()){
			personRepository.save(current);
			new Notification(null,"Pessoa salva.", Notification.Type.HUMANIZED_MESSAGE, true).show(Page.getCurrent());
			return true;
		} else{
			new Notification(null, status.getValidationErrors().get(0).getErrorMessage(), Notification.Type.ERROR_MESSAGE, true).show(Page.getCurrent());
			return false;
		}
	}

	private boolean delete() {

		if (current.getTotalAttendance() == 0) {
			personRepository.delete(current);
			new Notification(null,"Pessoa excluída.", Notification.Type.HUMANIZED_MESSAGE, true).show(Page.getCurrent());
			return true;
		} else {
			new Notification(null, "Exclusão não permitidas, pois há presenças atreladas a esta pessoa.", Notification.Type.WARNING_MESSAGE, true).show(Page.getCurrent());
			return false;
		}
	}

	public void enter(PersonView parentView, Person person) {

		//security
    	buttonSave.setVisible(CurrentUser.isUserInRole(SharingType.PERSON_WRITE));
    	buttonDelete.setVisible(CurrentUser.isUserInRole(SharingType.PERSON_WRITE));
    	
    	this.parentView = parentView;
    	this.current = person;
    	
		final boolean persisted = person.getId() != null;
		if(persisted){
			current = personRepository.findOne(person.getId());
			current.setTotalAttendance(attendanceRepository.findByPerson(current).size());
		}else{
			current = person;
			current.setCenter(CurrentUser.getCurrentCenter());
			current.setStatus(PersonStatus.NEW);
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
