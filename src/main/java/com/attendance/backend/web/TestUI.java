package com.attendance.backend.web;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;

import com.attendance.backend.model.Person2;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.SelectionMode;
import com.vaadin.ui.Notification;
import com.vaadin.ui.VerticalLayout;

@SpringComponent
@UIScope
public class TestUI  extends VerticalLayout {

	private static final long serialVersionUID = 1L;

	//Main components
	Grid<Person2> grid = new Grid<>(Person2.class);

	@Autowired
	public TestUI() {

		// Build layout
		setVisible(false);
		addComponents(grid);
		
		// Configure layouts and components
		grid.setHeight(300, Unit.PIXELS);
		grid.setColumns("name", "birthYear");


		//Grid data
		// Have some data
		List<Person2> people = Arrays.asList(
		    new Person2("Nicolaus Copernicus", 1543),
		    new Person2("Galileo Galilei", 1564),
		    new Person2("Johannes Kepler", 1571));

		// Create a grid bound to the list
		grid.setItems(people);
		grid.addColumn(Person2::getName).setCaption("Name");
		grid.addColumn(Person2::getBirthYear).setCaption("Year of birth");
		
		
		// Hook logic to components
		// switch to multiselect mode
		grid.setSelectionMode(SelectionMode.MULTI);

		grid.addSelectionListener(event -> {
		    Set<Person2> selected = event.getAllSelectedItems();
		    Notification.show(selected.size() + " items selected");
		});
	}
	
	public final void show() {
		setVisible(true);
	}
}
