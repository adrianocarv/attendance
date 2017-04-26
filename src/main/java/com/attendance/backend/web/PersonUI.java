package com.attendance.backend.web;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

@SpringComponent
@UIScope
public class PersonUI  extends VerticalLayout {

	private static final long serialVersionUID = 1L;

	/* Fields to edit properties in Customer entity */
	TextField name = new TextField(this.getClass().getSimpleName());

	@Autowired
	public PersonUI() {
		addComponents(name);
		setVisible(false);
	}	

	public final void show() {
		setVisible(true);
	}
	
}
