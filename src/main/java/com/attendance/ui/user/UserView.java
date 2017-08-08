package com.attendance.ui.user;

import org.springframework.beans.factory.annotation.Autowired;

import com.attendance.backend.model.User;
import com.attendance.backend.repository.UserRepository;
import com.attendance.ui.authentication.CurrentUser;
import com.vaadin.event.ShortcutAction;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.Button;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

@SpringComponent
@UIScope
public class UserView extends CssLayout implements View {

	private static final long serialVersionUID = 1L;

    public static final String VIEW_NAME = "Perfil";

	/** Dependences */
	@Autowired private UserRepository userRepository;
    private final UserLayout userLayout;

    /** Components */
    private Label id = new Label("id");
    private Label username = new Label("username");
    private Label name = new Label("name");
    private Label email = new Label("email");
    private Label defaultCenter = new Label("Centro padrão");
	private Button buttonEdit = new Button("Editar");
	
	@Autowired
	public UserView(UserLayout userLayout) {
		this.userLayout = userLayout;
		
		buildLayout();
		configureComponents();
		hookLogicToComponents();
	}

    private void buildLayout(){
        setSizeFull();
        addStyleName("crud-view");

    	VerticalLayout vertical = new VerticalLayout(id, defaultCenter, username, name, email, buttonEdit);
    	vertical.setMargin(true);
    	
    	vertical.setStyleName("form-layout");
    	
        addComponents(userLayout, vertical);
	}
	
	private void configureComponents() {
        buttonEdit.addStyleName(ValoTheme.BUTTON_PRIMARY);
        buttonEdit.setClickShortcut(ShortcutAction.KeyCode.ENTER);
	}

	private void hookLogicToComponents() {

		buttonEdit.addClickListener(e -> userLayout.enter(this));

	}

    @Override
    public void enter(ViewChangeEvent event) {

    	userLayout.setVisible(false);

    	User user = userRepository.findOne(CurrentUser.getUser().getId());
    	
    	if(user == null)
    		return;
    	
        id.setValue("Id: " + user.getId()+"");
        username.setValue("Username: " + user.getUsername());
        name.setValue("Nome: " + user.getName());
        email.setValue("E-mail: " + user.getEmail());
        defaultCenter.setValue("Centro padrão: " + user.getDefaultCenter().getName());
        
        buttonEdit.focus();
    }

}
