package com.attendance.backend.web;

import org.springframework.beans.factory.annotation.Autowired;

import com.attendance.backend.model.Activity;
import com.attendance.backend.repository.ActivityRepository;
import com.vaadin.data.Binder;
import com.vaadin.event.ShortcutAction;
import com.vaadin.server.FontAwesome;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.Button;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Label;
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
public class ActivityEditorUI extends VerticalLayout {

	private static final long serialVersionUID = 1L;

	private final ActivityRepository repository;

	/**
	 * The currently edited customer
	 */
	private Activity activity;

	/* Fields to edit properties in Activity entity */
	TextField name = new TextField("Nome");
	TextField nameComplement = new TextField("Complemento");
	TextField description = new TextField("Descrição");
	Label centro = new Label("");
	
	/* Action buttons */
	Button save = new Button("Save", FontAwesome.SAVE);
	Button cancel = new Button("Cancel");
	Button delete = new Button("Delete", FontAwesome.TRASH_O);
	CssLayout actions = new CssLayout(save, cancel, delete);

	Binder<Activity> binder = new Binder<>(Activity.class);

	@Autowired
	public ActivityEditorUI(ActivityRepository repository) {
		this.repository = repository;

		// Build layout
		setVisible(false);
		addComponents(name, nameComplement, description, centro, actions);

		// bind using naming convention
		binder.bindInstanceFields(this);

		// Configure and style components
		setSpacing(true);
		actions.setStyleName(ValoTheme.LAYOUT_COMPONENT_GROUP);
		save.setStyleName(ValoTheme.BUTTON_PRIMARY);
		save.setClickShortcut(ShortcutAction.KeyCode.ENTER);

		// wire action buttons to save, delete and reset
		save.addClickListener(e -> repository.save(activity));
		delete.addClickListener(e -> repository.delete(activity));
		cancel.addClickListener(e -> this.setVisible(false));
	}

	public interface ChangeHandler {

		void onChange();
	}

	public final void edit(Activity a) {
		final boolean persisted = a.getId() != null;
		if (persisted) {
			// Find fresh entity for editing
			activity = repository.findOne(a.getId());
		}
		else {
			activity = a;
		}
		cancel.setVisible(persisted);

		// Bind customer properties to similarly named fields
		// Could also use annotation or "manual binding" or programmatically
		// moving values from fields to entities before saving
		binder.setBean(activity);

		setVisible(true);

		// A hack to ensure the whole form is visible
		save.focus();
		// Select all text in firstName field automatically
		name.selectAll();
	}

	public void setChangeHandler(ChangeHandler h) {
		// ChangeHandler is notified when either save or delete
		// is clicked
		save.addClickListener(e -> h.onChange());
		delete.addClickListener(e -> h.onChange());
	}

}
