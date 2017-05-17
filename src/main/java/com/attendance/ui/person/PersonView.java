package com.attendance.ui.person;

import java.util.ArrayList;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.attendance.backend.model.Person;
import com.attendance.backend.repository.PersonRepository;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.shared.ui.ValueChangeMode;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.SelectionMode;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

@SpringComponent
@UIScope
public class PersonView extends CssLayout implements View {

	private static final long serialVersionUID = 1L;

    public static final String VIEW_NAME = "Pessoas";

	/** Dependences */
    @Autowired private PersonRepository personRepository;
    private final PersonLayout personLayout;

    /** Components */
	private TextField fieldFilter = new TextField();
	private Button buttonNew= new Button("", VaadinIcons.PLUS_CIRCLE);
	private Grid<Person> grid = new Grid<>(Person.class);
	
	@Autowired
	public PersonView(PersonLayout personLayout) {
		this.personLayout = personLayout;
		
		buildLayout();
		configureComponents();
		hookLogicToComponents();
	}

    private void buildLayout(){
        setSizeFull();
        addStyleName("crud-view");

        HorizontalLayout topLayout = new HorizontalLayout(fieldFilter, buttonNew);
        topLayout.setWidth("100%");
        topLayout.setComponentAlignment(fieldFilter, Alignment.MIDDLE_LEFT);
        topLayout.setExpandRatio(fieldFilter, 3);
        topLayout.setExpandRatio(buttonNew, 1);
        topLayout.setStyleName("top-bar");

    	VerticalLayout vertical = new VerticalLayout(topLayout, grid);
    	vertical.setSizeFull();
    	vertical.setMargin(true);
    	vertical.setStyleName("form-layout");
    	vertical.setExpandRatio(grid,  1);
    	
        addComponents(personLayout, vertical);
	}
	
	private void configureComponents() {
        fieldFilter.setStyleName("filter-textfield");
        fieldFilter.setPlaceholder("Buscar por Nome");

        buttonNew.addStyleName(ValoTheme.BUTTON_PRIMARY);
		
		grid.setSizeFull();
		grid.setColumns("name");
		grid.getColumn("name").setCaption("Pessoas").setResizable(false).setSortable(true);
		grid.sort("name");
		grid.setSelectionMode(SelectionMode.SINGLE);
	}

	private void hookLogicToComponents() {

		fieldFilter.setValueChangeMode(ValueChangeMode.LAZY);
		fieldFilter.addValueChangeListener(e -> loadGrid(e.getValue()));

		buttonNew.addClickListener(e -> personLayout.enter(this, new Person(null)));

		grid.asSingleSelect().addValueChangeListener(e -> {
			if(e.getValue() == null) return;
			
			personLayout.enter(this, e.getValue());
		});
	}

    @Override
    public void enter(ViewChangeEvent event) {
    	grid.setVisible(true);
    	personLayout.setVisible(false);

		loadGrid(fieldFilter.getValue());
		fieldFilter.focus();
		fieldFilter.selectAll();
    }

	private void loadGrid(String filterText) {

		if (StringUtils.isEmpty(filterText))
			grid.setItems(new ArrayList<Person>());
		else
			grid.setItems(personRepository.findByNameStartsWithIgnoreCaseOrderByNameAsc("%"+filterText.trim()));
	}
}
