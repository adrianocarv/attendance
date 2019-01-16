package com.attendance.ui.about;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import com.attendance.backend.model.User;
import com.attendance.backend.repository.UserRepository;
import com.attendance.backend.util.AttendanceEmailService;
import com.attendance.ui.authentication.CurrentUser;
import com.attendance.ui.util.NotificationUtil;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.Page;
import com.vaadin.shared.Version;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.CustomLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

@SpringComponent
@UIScope
public class AboutView extends VerticalLayout implements View {

	private static final long serialVersionUID = 1L;

	public static final String VIEW_NAME = "Sobre";

	/** Dependences */
	@Value("${build.version}") private String buildVersion;
	@Value("${build.timestamp}") private String buildTimestamp;
    @Autowired private AttendanceEmailService attendanceEmailService;
    @Autowired private UserRepository userRepository;

    /** Components */
	private CustomLayout aboutContent = new CustomLayout("aboutview");
	private Label labelAttendanceInfo = new Label();
	private Label interceptLabel = new Label("", ContentMode.HTML);
	private Button interceptLButtonResendMail = new Button("reenviar e-mail de verificação");
	
	
	public AboutView() {
		buildLayout();
		configureComponents();
		hookLogicToComponents();
    }

    private void buildLayout() {
        aboutContent.setStyleName("about-content");

        // you can add Vaadin components in predefined slots in the custom layout
		aboutContent.addComponent(interceptLabel, "interceptMsg");
    	aboutContent.addComponent(interceptLButtonResendMail, "interceptResendButton");
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
		interceptLButtonResendMail.addClickListener(e -> this.sendEmailVerification());
	}

	@Override
    public void enter(ViewChangeEvent event) {

		this.emailVerification();
		this.buildInterceptedLoginNotification();
		
		String attendanceInfo = "</br></br>" + VaadinIcons.INFO_CIRCLE.getHtml() + " Versão: " + buildVersion + " - " + buildTimestamp;
        this.buildInterceptedLoginNotification();
        labelAttendanceInfo.setValue(attendanceInfo);
        labelAttendanceInfo.setContentMode(ContentMode.HTML);
    }

	private void buildInterceptedLoginNotification(){
        if(!CurrentUser.getUser().isInterceptedLogin()){
    		aboutContent.removeComponent(interceptLabel);
        	aboutContent.removeComponent(interceptLButtonResendMail);
        	return;
        }
        
		String msg = "Olá, </br></br> <u>Seu endereço de e-mail precisa ser verificado</u>.</br></br>";
		msg += " Nós enviamos um e-mail para você no endereço <b>" + CurrentUser.getUser().getEmail() + "</b></br></br>";
		msg += " Por favor acesse a caixa postal, encontre a mensagem enviada e clique no link de ativação. Caso não encontre o e-mail:</br>";
		interceptLabel.setValue(msg);
		interceptLabel.setWidth("100%");

		interceptLButtonResendMail.setStyleName(ValoTheme.BUTTON_LINK);
		
		aboutContent.addComponent(interceptLabel, "interceptMsg");
    	aboutContent.addComponent(interceptLButtonResendMail, "interceptResendButton");
	}

	private void sendEmailVerification(){

		//Send mail
		String accessToken = attendanceEmailService.sendEmailVerification(CurrentUser.getUser());
		if(accessToken == null) return;
		CurrentUser.getUser().setAccessToken(accessToken);
		userRepository.save(CurrentUser.getUser());
	}
	
	private void emailVerification(){

		String markup = "/Activation/";
		String uriFragment = Page.getCurrent().getUriFragment();
		if(!uriFragment.contains(markup))
			return;
		
		String token = uriFragment.substring(uriFragment.indexOf(markup));
		token = token.replace(markup, "");
		Long id = new Long(token.substring(token.indexOf('_')+1));
		User user = userRepository.findOne(id); 
		
		if(user == null || !token.equals(user.getAccessToken())){
			NotificationUtil.show(new Notification("Problema no verificação do e-mail", "Token de acesso inválido." , Notification.Type.TRAY_NOTIFICATION));
			return;
		}
		
		user.setAccessToken(null);
		user.setStatus(null);
		user.setOldMail(null);
		userRepository.save(user);

		CurrentUser.set(user);
	
		NotificationUtil.show(new Notification("E-mail verificado", "Seja Bem-vindo!" , Notification.Type.TRAY_NOTIFICATION));
	}
}
