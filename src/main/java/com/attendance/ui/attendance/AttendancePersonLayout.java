package com.attendance.ui.attendance;

import java.util.ArrayList;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.attendance.backend.model.Center;
import com.attendance.backend.model.Person;
import com.attendance.backend.repository.PersonRepository;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.Page;
import com.vaadin.shared.ui.ValueChangeMode;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Grid;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

@SpringComponent
@UIScope
public class AttendancePersonLayout extends CssLayout {

	private static final long serialVersionUID = 1L;

    /** Dependences */
	@Autowired private PersonRepository personRepository;

    /** Components */
	private TextField fieldFilter = new TextField();
	private Button buttonNewPerson = new Button(VaadinIcons.PLUS);
	private Button buttonBack = new Button("Voltar");
	private Grid<Person> grid = new Grid<>(Person.class);

	private AttendanceLayout parentView;
	
	public AttendancePersonLayout() {
		
		buildLayout();
		configureComponents();
		hookLogicToComponents();

	}

    private void buildLayout(){
    	setVisible(false);
    	
    	setStyleName("product-form-wrapper");
    	
        HorizontalLayout topLayout = new HorizontalLayout(fieldFilter, buttonNewPerson);
        topLayout.setWidth("100%");
        topLayout.setComponentAlignment(fieldFilter, Alignment.MIDDLE_LEFT);
        topLayout.setExpandRatio(fieldFilter, 5);
        topLayout.setExpandRatio(buttonNewPerson, 1);
        topLayout.setStyleName("top-bar");

    	VerticalLayout vertical = new VerticalLayout(topLayout, grid, buttonBack);
    	vertical.setSizeFull();
    	//vertical.setSpacing(false);
    	vertical.setMargin(false);
    	vertical.setStyleName("form-layout");
    	vertical.setExpandRatio(grid,  1);
    	
    	addComponent(vertical);
	}
	private void configureComponents() {
        fieldFilter.setStyleName("filter-textfield");
        fieldFilter.setPlaceholder("Buscar ou adicionar Nome");
        
        buttonNewPerson.addStyleName(ValoTheme.BUTTON_PRIMARY);

		grid.setSizeFull();
		grid.setColumns("name");
		grid.getColumn("name").setCaption("Nome").setResizable(false).setSortable(false);
        
        buttonBack.setStyleName("cancel");
	}

	private void hookLogicToComponents() {

		fieldFilter.setValueChangeMode(ValueChangeMode.LAZY);
		fieldFilter.addValueChangeListener(e -> loadGrid(e.getValue()));
		
		buttonNewPerson.addClickListener(e -> this.saveNewPerson());

		buttonBack.addClickListener(e -> {
			fieldFilter.setValue("");
			setVisible(false);
		});
		
		grid.asSingleSelect().addValueChangeListener(e -> {
			if(e.getValue() == null)
				return;
			
			parentView.addPerson(e.getValue());
			fieldFilter.focus();
			fieldFilter.selectAll();
		});
		
	}

    public void enter(AttendanceLayout parentView) {
    	this.parentView = parentView;
    	
		setVisible(true);
		fieldFilter.focus();
    }

	private void loadGrid(String filterText) {

		if (StringUtils.isEmpty(filterText))
			grid.setItems(new ArrayList<Person>());
		else
			grid.setItems(personRepository.findByNameStartsWithIgnoreCaseOrderByNameAsc("%"+filterText.trim()));
	}
	
	private void saveNewPerson() {
		
		String name = fieldFilter.getValue() == null ? "": fieldFilter.getValue().trim();

		//Blank name, ignore
		if(StringUtils.isEmpty(name)){
			Notification.show("Informe o nome da pessoa nova...");
			fieldFilter.setValue("");
			fieldFilter.selectAll();
		    return;
		}

		//Verify if exists
		Center center = parentView.getCurrentActivity().getCenter();
		boolean isSaved = personRepository.findByNameIgnoreCaseAndCenter(name, center).size() > 0;
		if(isSaved){
			new Notification(null,"<b>" + name + "</b> já existe na base.", Notification.Type.WARNING_MESSAGE, true).show(Page.getCurrent());
			fieldFilter.focus();
			return;
		}

		//Save New Person
		Person newPerson = new Person(name, center);
		personRepository.save(newPerson);

		new Notification(null,"<b>" + name + "</b> adicionado na base.", Notification.Type.HUMANIZED_MESSAGE, true).show(Page.getCurrent());
		fieldFilter.setValue("");
		fieldFilter.setValue(name);
		fieldFilter.focus();
	}
}
