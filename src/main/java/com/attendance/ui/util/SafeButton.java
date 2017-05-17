package com.attendance.ui.util;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;

public class SafeButton extends CustomComponent {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final Label infoLabel = new Label("", ContentMode.HTML);
	private final Button yesButton = new Button("Sim", VaadinIcons.CHECK);
	private final Button noButton = new Button("Não", VaadinIcons.CLOSE);
	private final Window window = new Window();

	public SafeButton(String caption, String popupText) {
	    this(caption, null, popupText, "300px", null);
	}

	public SafeButton(String caption, VaadinIcons icon, String popupText, String popupWidth, ClickListener yesListener) {
		VerticalLayout root = new VerticalLayout();
		setCompositionRoot(root);

		root.setSpacing(false);
		root.setMargin(false);

		infoLabel.setSizeFull();
		if (popupText != null) {
			infoLabel.setValue(popupText);
		}

		yesButton.addStyleName(ValoTheme.BUTTON_DANGER);
		noButton.addStyleName(ValoTheme.BUTTON_PRIMARY);

		final VerticalLayout popupVLayout = new VerticalLayout();
		popupVLayout.setSpacing(true);
		popupVLayout.setMargin(true);

		final Button button = new Button(caption, icon);
		button.setSizeFull();
		button.addStyleName(ValoTheme.BUTTON_DANGER);
		button.addClickListener(e -> {
			if (window.getParent() == null) {
				UI.getCurrent().addWindow(window);
			}
		});
		HorizontalLayout buttonsHLayout = new HorizontalLayout();
		buttonsHLayout.setSpacing(true);

		buttonsHLayout.addComponent(yesButton);
		buttonsHLayout.addComponent(noButton);

		window.setWidth(popupWidth);
		window.setHeightUndefined();
		window.setModal(true);
		window.center();
		window.setResizable(false);
		window.setContent(popupVLayout);
		window.setCaption(" Confirmação");
		window.setIcon(VaadinIcons.WARNING);

		yesButton.addClickListener(e -> {
			window.close();
		});

		if (yesListener != null) {
			yesButton.addClickListener(yesListener);
		}

		noButton.focus();
		noButton.addClickListener(e -> {
			window.close();
		});

		// ui
		popupVLayout.addComponent(infoLabel);
		popupVLayout.addComponent(buttonsHLayout);
		popupVLayout.setComponentAlignment(buttonsHLayout, Alignment.TOP_CENTER);
		root.addComponent(button);
	}

	public void setInfo(String info) {
		infoLabel.setValue(info);
	}

	public void setWindowWidth(String width) {
		window.setWidth(width);
	}

	public void setYesListener(ClickListener yesListener) {
		if (yesListener != null) {
			yesButton.addClickListener(yesListener);
		}
	}
}