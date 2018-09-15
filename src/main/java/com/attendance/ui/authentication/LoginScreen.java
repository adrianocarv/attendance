package com.attendance.ui.authentication;

import java.io.Serializable;

import org.apache.commons.lang3.StringUtils;

import com.attendance.backend.model.User;
import com.attendance.ui.util.NotificationUtil;
import com.vaadin.data.BeanValidationBinder;
import com.vaadin.data.Binder;
import com.vaadin.data.BinderValidationStatus;
import com.vaadin.data.validator.EmailValidator;
import com.vaadin.data.validator.RegexpValidator;
import com.vaadin.data.validator.StringLengthValidator;
import com.vaadin.event.ShortcutAction;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

/**
 * UI content when the user is not logged in yet.
 */
public class LoginScreen extends CssLayout {

	private static final long serialVersionUID = 1L;

	private final String ABA_LOGIN = "Acessar conta";
	private final String ABA_REGISTER = "Registrar";

	private String buildVersion;
	private TextField username;
    private PasswordField password;
    private Button login;
    private Button forgotPassword;
    private LoginListener loginListener;

	private TextField registerName;
	private TextField registerUsername;
	private TextField registerEmail;
    private PasswordField registerPassword;
    private Button registerRegister;
    
    private AccessControl accessControl;
    
    public LoginScreen(String buildVersion, AccessControl accessControl, LoginListener loginListener) {
        this.buildVersion = buildVersion;
    	this.loginListener = loginListener;
        this.accessControl = accessControl;
        buildUI();

		// A hack to ensure the whole form is visible
        username.focus();
        username.selectAll();
    }

    private void buildUI() {
        addStyleName("login-screen");

        // login form, centered in the available part of the screen
        Component authForm = buildAuthForm();

        // layout to center login form when there is sufficient screen space
        // - see the theme for how this is made responsive for various screen
        // sizes
        VerticalLayout centeringLayout = new VerticalLayout();
        centeringLayout.setMargin(false);
        centeringLayout.setSpacing(false);
        centeringLayout.setStyleName("centering-layout");
        centeringLayout.addComponent(authForm);
        centeringLayout.setComponentAlignment(authForm, Alignment.MIDDLE_CENTER);

        // information text about logging in
        CssLayout loginInformation = buildLoginInformation();

        addComponent(centeringLayout);
        addComponent(loginInformation);
    }

    private Component buildAuthForm() {
    	TabSheet authForm = new TabSheet();
    	authForm.addStyleName("login-form");
    	authForm.setSizeUndefined();
    	authForm.addTab(this.buildLoginForm(), ABA_LOGIN);
    	authForm.addTab(this.buildRegisterForm(), ABA_REGISTER);
    	
    	authForm.addSelectedTabChangeListener(e ->{
    		Component tab = (Component) e.getTabSheet().getSelectedTab();

    		if(ABA_LOGIN.equals(e.getTabSheet().getTab(tab).getCaption())){
    			registerRegister.removeClickShortcut();
    	        login.setClickShortcut(ShortcutAction.KeyCode.ENTER);
    	        username.focus();
    		} else {
    			login.removeClickShortcut();
    			registerRegister.setClickShortcut(ShortcutAction.KeyCode.ENTER);
        		registerName.focus();
        		registerEmail.setValue(" ");
        		registerPassword.setValue("");
    		}
    	});
    	
        return authForm;
    }
    
    private Component buildLoginForm() {

    	FormLayout loginForm = new FormLayout();

        loginForm.addStyleName("login-form");
        loginForm.setSizeUndefined();
        loginForm.setMargin(false);

        loginForm.addComponent(username = new TextField("Usuário"));
        username.setWidth(15, Unit.EM);
        loginForm.addComponent(password = new PasswordField("Senha"));
        password.setWidth(15, Unit.EM);
        password.setDescription("Write anything");
        CssLayout buttons = new CssLayout();
        buttons.setStyleName("buttons");
        loginForm.addComponent(buttons);

        buttons.addComponent(login = new Button("Login"));
        login.setDisableOnClick(true);
        login.addClickListener(new Button.ClickListener() {
        	private static final long serialVersionUID = 1L;
			@Override
            public void buttonClick(Button.ClickEvent event) {
                try {
                    login();
                } finally {
                    login.setEnabled(true);
                }
            }
        });
        login.setClickShortcut(ShortcutAction.KeyCode.ENTER);
        login.addStyleName(ValoTheme.BUTTON_FRIENDLY);

        buttons.addComponent(forgotPassword = new Button("esqueceu a senha?"));
        forgotPassword.addClickListener(new Button.ClickListener() {
        	private static final long serialVersionUID = 1L;
            @Override
            public void buttonClick(Button.ClickEvent event) {
            	NotificationUtil.show(new Notification("Hint: Try anything"));
            }
        });
        forgotPassword.addStyleName(ValoTheme.BUTTON_LINK);

        return loginForm;
    }

    private Component buildRegisterForm() {

    	FormLayout registerForm = new FormLayout();

        registerForm.addStyleName("login-form");
        registerForm.setSizeUndefined();
        registerForm.setMargin(false);
        
        registerForm.addComponent(registerName = new TextField("Nome completo"));
        registerName.setWidth(15, Unit.EM);
        registerForm.addComponent(registerUsername = new TextField("Usuário"));
        registerUsername.setWidth(15, Unit.EM);
        registerForm.addComponent(registerEmail = new TextField("E-mail"));
        registerEmail.setWidth(15, Unit.EM);
        registerForm.addComponent(registerPassword = new PasswordField("Senha"));
        registerPassword.setWidth(15, Unit.EM);
        registerPassword.setDescription("Pelo menos 6 caracteres");
        CssLayout buttons = new CssLayout();
        buttons.setStyleName("buttons");
        registerForm.addComponent(buttons);

        buttons.addComponent(registerRegister = new Button("Criar conta"));
        registerRegister.setDisableOnClick(true);
        registerRegister.addClickListener(new Button.ClickListener() {
        	private static final long serialVersionUID = 1L;
			@Override
            public void buttonClick(Button.ClickEvent event) {
                try {
                    register();
                } finally {
                	registerRegister.setEnabled(true);
                }
            }
        });
        registerRegister.addStyleName(ValoTheme.BUTTON_FRIENDLY);

        return registerForm;
    }
    
    private CssLayout buildLoginInformation() {
        CssLayout loginInformation = new CssLayout();
        loginInformation.setStyleName("login-information");
        
        String infoText = "<h1>Attendance</h1>";
        infoText += "<h2>Seja Bem-vindo!</h2>"; 
        infoText += "Crie suas atividades</br>"; 
        infoText += "Marque as presenças</br>"; 
        infoText += "Analise as frequências</br>"; 
        infoText += "E compartilhe...</br>"; 
        infoText += "<p align='right'><font size='1'><i>v. " + buildVersion + "</i></font size></p>"; 
        Label loginInfoText = new Label(infoText,ContentMode.HTML);
        loginInfoText.setSizeFull();
        loginInformation.addComponent(loginInfoText);
        return loginInformation;
    }

    private void login() {
        if (accessControl.signIn(username.getValue(), password.getValue())) {
        	loginListener.loginSuccessful();
        } else {
        	NotificationUtil.show(new Notification("Falha no login", "Por favor, verifique seu username e senha e tente novamente.", Notification.Type.HUMANIZED_MESSAGE));
            username.focus();
            username.selectAll();
        }
    }

    private void register() {

    	//Fields validations
    	registerEmail.setValue(StringUtils.trim(registerEmail.getValue()));
    	
    	Binder<User> binder = new BeanValidationBinder<>(User.class);
    	binder.forField(registerName).withValidator(new StringLengthValidator("O nome deve ter pelo menos 5 caracteres", 5, null)).bind("name");
    	binder.forField(registerUsername).withValidator(new StringLengthValidator("O nome do usuário deve ter entre 2 e 20 caracteres", 2, 20)).bind("username");
    	binder.forField(registerEmail).withValidator(new EmailValidator("E-mail inválido")).bind("email");
    	binder.forField(registerPassword).withValidator(new RegexpValidator("A senha precisa ter pelo menos 6 caracteres formados por letras e números", User.getPasswordRegexp())).bind("password");
    	BinderValidationStatus<User> status = binder.validate();
    	if(status.hasErrors()){
			return;
		}
    	
        User newUser = new User(registerUsername.getValue(), registerName.getValue(), registerEmail.getValue(), registerPassword.getValue());
    	if (accessControl.register(newUser)) {
        	username.setValue(registerUsername.getValue());
        	password.setValue(registerPassword.getValue());
    		this.login();
        } else {
            registerUsername.focus();
            registerUsername.selectAll();
        }
    }

    public interface LoginListener extends Serializable {
        void loginSuccessful();
    }
}
