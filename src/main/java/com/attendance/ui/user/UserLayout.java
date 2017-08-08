package com.attendance.ui.user;

import org.springframework.beans.factory.annotation.Autowired;

import com.attendance.backend.model.User;
import com.attendance.backend.model.UserStatus;
import com.attendance.backend.repository.UserRepository;
import com.attendance.backend.util.EmailService;
import com.attendance.ui.authentication.CurrentUser;
import com.attendance.ui.util.NotificationUtil;
import com.vaadin.data.BeanValidationBinder;
import com.vaadin.data.Binder;
import com.vaadin.data.BinderValidationStatus;
import com.vaadin.data.validator.EmailValidator;
import com.vaadin.data.validator.StringLengthValidator;
import com.vaadin.event.ShortcutAction;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.Button;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

@SpringComponent
@UIScope
public class UserLayout extends CssLayout {

	private static final long serialVersionUID = 1L;

    /** Dependences */
	@Autowired private UserRepository userRepository;
    @Autowired private EmailService emailService;

    /** Components */
    private Label id = new Label("id");
    private Label defaultCenter = new Label("Centro padrão");
    private TextField username = new TextField("Usuário");
    private TextField name = new TextField("Nome completo");
    private TextField email = new TextField("E-mail");
	private PasswordField passwordOld = new PasswordField("Senha antiga");
	private PasswordField passwordNew = new PasswordField("Nova senha");
	
	private Button buttonSave = new Button("Salvar");
    private Button buttonCancel = new Button("Cancelar");

    private Binder<User> binder = new BeanValidationBinder<>(User.class);

    private UserView parentView;
	private User current;
	
	public UserLayout() {
		
		buildLayout();
		configureComponents();
		hookLogicToComponents();

	}

    private void buildLayout(){
    	setVisible(false);
    	
    	setStyleName("product-form-wrapper");
    	
    	VerticalLayout vertical = new VerticalLayout();

    	vertical.addComponent(id);
    	vertical.addComponent(defaultCenter);
    	vertical.addComponent(username);
    	vertical.addComponent(name);
    	vertical.addComponent(email);
    	vertical.addComponent(passwordOld);
    	vertical.addComponent(passwordNew);
    	
    	vertical.addComponents(buttonSave, buttonCancel);

    	vertical.setMargin(false);
    	vertical.setStyleName("form-layout");
    	
    	addComponent(vertical);
	}
	private void configureComponents() {
        
		// bind using naming convention
        binder.bindInstanceFields(this);

        username.setSizeFull();
        name.setSizeFull();
        email.setSizeFull();
    	passwordOld.setSizeFull();
    	passwordNew.setSizeFull();
        
        buttonSave.addStyleName(ValoTheme.BUTTON_PRIMARY);
        buttonCancel.addStyleName("cancel");
        
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
	}
    
    private boolean save() {

    	//Fields validations
    	binder.forField(name).withValidator(new StringLengthValidator("O nome deve ter pelo menos 5 caracteres", 5, null)).bind("name");
    	binder.forField(username).withValidator(new StringLengthValidator("O nome do usuário deve ter entre 2 e 20 caracteres", 2, 20)).bind("username");
    	binder.forField(email).withValidator(new EmailValidator("E-mail inválido")).bind("email");
    	BinderValidationStatus<User> status = binder.validate();

    	if(status.hasErrors()){
			return false;
		}

    	//Validate password fields
    	if(passwordOld.isEmpty() ^ passwordNew.isEmpty()){
			NotificationUtil.show(new Notification("Atenção: ", "Para alterar a senha é necessário preencher os dois campos (senha antiga e nova).", Notification.Type.TRAY_NOTIFICATION, true));
			return false;
    	}

    	//Validate old password
    	if(!passwordOld.isEmpty() && !User.getEncryptedPassword(passwordOld.getValue()).equals(CurrentUser.getUser().getPassword()) ){
			NotificationUtil.show(new Notification("Atenção: ", "A senha antiga está incorreta.", Notification.Type.TRAY_NOTIFICATION, true));
			passwordOld.selectAll();
			return false;
    	}

    	//Validate new password
    	if(!passwordNew.isEmpty() && !passwordNew.getValue().matches(User.getPasswordRegexp())){
			NotificationUtil.show(new Notification("Atenção: ", "A nova senha precisa ter pelo menos 6 caracteres formados por letras e números.", Notification.Type.TRAY_NOTIFICATION, true));
			passwordNew.selectAll();
			return false;
    	}
    	
    	//Change security
    	if(!passwordNew.isEmpty() && !email.getValue().equals(CurrentUser.getUser().getEmail())){
			NotificationUtil.show(new Notification("Atenção: ", "Não é possível alterar a senha e o e-mail ao mesmo tempo.", Notification.Type.TRAY_NOTIFICATION, true));
			return false;
    	}
    	
    	//Validade username and e-mail
    	if(!this.validateUnique()){
    		return false;
    	}
    	
    	//Update password
    	if(!passwordNew.getValue().isEmpty()){
    		current.setPassword(User.getEncryptedPassword(passwordNew.getValue()));
    	}

    	String msg = "Perfil salvo";
    	//Update e-mail
		if(!email.getValue().equals(CurrentUser.getUser().getEmail())){

    		current.setOldMail(CurrentUser.getUser().getEmail());
    		current.setStatus(UserStatus.CHANGED_MAIL);

    		//Send mail
    		String accessToken = emailService.sendEmailVerification(current);
    		if(accessToken == null) return false;

    		current.setAccessToken(accessToken);

    		msg = "Como você alterou o e-mail, sua conta precisa de uma ativação.";
    	}

    	userRepository.save(current);
    	CurrentUser.set(current);
		NotificationUtil.show(new Notification("Perfil salvo", msg, Notification.Type.HUMANIZED_MESSAGE, true));
		return true;
	}

	private boolean validateUnique() {
		//by username
		if(!username.getValue().equals(CurrentUser.getUser().getUsername())){
			User existentUser = userRepository.findOneByUsername(username.getValue());
			if(existentUser != null){
				NotificationUtil.show(new Notification("Atenção: ", "Já existe um usuário com o login <b>" + username.getValue() + "</b>.", Notification.Type.TRAY_NOTIFICATION, true));
				username.setValue(current.getUsername());
	            return false;
			}
		}

		//by email
		if(!email.getValue().equals(CurrentUser.getUser().getEmail())){
			User existentUser = userRepository.findOneByEmail(email.getValue());
			if(existentUser != null){
				NotificationUtil.show(new Notification("Atenção: ", "Já existe um usuário com o e-mail <b>" + email.getValue() + "</b>.", Notification.Type.TRAY_NOTIFICATION, true));
				email.setValue(current.getEmail());
	            return false;
			}
		}
		
		return true;
	}

	public void enter(UserView parentView) {

		//security
    	buttonSave.setVisible(!CurrentUser.getUser().isInterceptedLogin());

    	this.parentView = parentView;
    	this.current = userRepository.findOne(CurrentUser.getUser().getId());
    	binder.setBean(current);

		id.setValue("Id: " + current.getId());
		defaultCenter.setValue("Centro padrão: " + current.getDefaultCenter().getName());
		passwordOld.clear();
		passwordNew.clear();
		
		setVisible(true);

		// A hack to ensure the whole form is visible
		buttonSave.focus();
		username.selectAll();
    }
}
