package com.attendance.ui.about;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.shared.Version;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.CustomLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

@SpringComponent
@UIScope
public class AboutView extends VerticalLayout implements View {

	private static final long serialVersionUID = 1L;

	public static final String VIEW_NAME = "Sobre";

	/** Dependences */
	@Value("${application.name}")
	private String applicationName;

	@Value("${build.version}")
	private String buildVersion;

	@Value("${build.timestamp}")
	private String buildTimestamp;

	@Autowired
	Environment env;
	
	@Value("${profile.name}")
	private String profileName;
	
	/** Components */
	private Label labelAttendanceInfo = new Label();
	
	
	public AboutView() {
		buildLayout();
		configureComponents();
		hookLogicToComponents();
    }

    private void buildLayout() {
		CustomLayout aboutContent = new CustomLayout("aboutview");
        aboutContent.setStyleName("about-content");

        // you can add Vaadin components in predefined slots in the custom layout
        aboutContent.addComponent(labelAttendanceInfo, "attendanceInfo");
        aboutContent.addComponent(new Label(VaadinIcons.INFO_CIRCLE.getHtml() + " This application is using Vaadin " + Version.getFullVersion(), ContentMode.HTML), "vaadinInfo");

        setSizeFull();
        setMargin(false);
        setStyleName("about-view");
        addComponent(aboutContent);
        setComponentAlignment(aboutContent, Alignment.MIDDLE_CENTER);
	}

	private void configureComponents() {
	}

	private void hookLogicToComponents() {
	}

	@Override
    public void enter(ViewChangeEvent event) {
        String attendanceInfo = VaadinIcons.INFO_CIRCLE.getHtml() + " Versão: " + buildVersion + " - " + buildTimestamp + " (" + env.getDefaultProfiles() + ")";
        labelAttendanceInfo.setValue(attendanceInfo);
        labelAttendanceInfo.setContentMode(ContentMode.HTML);
    }

}
