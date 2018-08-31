package com.attendance.ui.sharing;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.attendance.backend.model.Activity;
import com.attendance.backend.model.Sharing;
import com.attendance.backend.model.SharingType;
import com.attendance.backend.model.User;
import com.attendance.backend.model.UserStatus;
import com.attendance.backend.repository.ActivityRepository;
import com.attendance.backend.repository.SharingRepository;
import com.attendance.backend.repository.UserRepository;
import com.attendance.ui.authentication.CurrentUser;
import com.vaadin.data.BeanValidationBinder;
import com.vaadin.data.Binder;
import com.vaadin.data.BinderValidationStatus;
import com.vaadin.event.ShortcutAction;
import com.vaadin.server.Page;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

@SpringComponent
@UIScope
public class SharingLayout extends CssLayout {

	private static final long serialVersionUID = 1L;

    /** Dependences */
	@Autowired private SharingRepository sharingRepository;
	@Autowired private ActivityRepository activityRepository;
	@Autowired private UserRepository userRepository;

    /** Components */
	private Label labelCenter = new Label();
	private ComboBox<User> selectUsers = new ComboBox<User>("Usuário");
	private TextField email = new TextField("Novo usuário");
	private ComboBox<Activity> selectActivities = new ComboBox<Activity>("Atividade");
	private CheckBox check0 = new CheckBox(SharingType.ATTENDANCE_READ.toString());
	private CheckBox check1 = new CheckBox(SharingType.ATTENDANCE_WRITE.toString());
	private CheckBox check2 = new CheckBox(SharingType.PERSON_READ.toString());
	private CheckBox check3 = new CheckBox(SharingType.PERSON_WRITE.toString());
	private CheckBox check4 = new CheckBox(SharingType.ACTIVITY_READ.toString());
	private CheckBox check5 = new CheckBox(SharingType.ACTIVITY_WRITE.toString());
	private CheckBox check6 = new CheckBox(SharingType.SHARING_CENTER.toString());
	private CheckBox check7 = new CheckBox(SharingType.SHARING_ACTIVITY.toString());
	private CheckBox checkActivity0 = new CheckBox(SharingType.ATTENDANCE_READ.toString());
	private CheckBox checkActivity1 = new CheckBox(SharingType.ATTENDANCE_WRITE.toString());
	
	private Button buttonSave = new Button("Salvar");
    private Button buttonCancel = new Button("Cancelar");

    private VerticalLayout layoutCheckCenter = new VerticalLayout();
    private VerticalLayout layoutCheckActivity = new VerticalLayout();
    
    private SharingView parentView;
	
	public SharingLayout() {
		
		buildLayout();
		configureComponents();
		hookLogicToComponents();

	}

    private void buildLayout(){
    	setVisible(false);
    	
    	setStyleName("product-form-wrapper");
    	
        layoutCheckCenter.setMargin(false);
        layoutCheckCenter.setSpacing(false);

        layoutCheckCenter = new VerticalLayout(check0, check1, check2, check3, check4, check5, check6, check7);
        layoutCheckCenter.setMargin(true);
        layoutCheckCenter.setSpacing(false);

        layoutCheckActivity = new VerticalLayout(checkActivity0, checkActivity1);
        layoutCheckActivity.setMargin(true);
        layoutCheckActivity.setSpacing(false);

        VerticalLayout buttonsLayout = new VerticalLayout(buttonSave, buttonCancel);
        buttonsLayout.setMargin(false);
        buttonsLayout.setSpacing(true);

        VerticalLayout vertical = new VerticalLayout();
    	vertical.addComponents(labelCenter, selectUsers, email, selectActivities, layoutCheckCenter, layoutCheckActivity, buttonsLayout);

    	vertical.setMargin(false);
    	vertical.setSpacing(true);
    	vertical.setStyleName("form-layout");
    	
    	addComponent(vertical);
	}
    
	private void configureComponents() {
        
		email.setSizeFull();
		email.setPlaceholder("e-mail");

		selectUsers.setSizeFull();
		selectUsers.setItemCaptionGenerator(User::getEmail);

		selectActivities.setSizeFull();
		selectActivities.setItemCaptionGenerator(Activity::getName);

		buttonSave.addStyleName(ValoTheme.BUTTON_PRIMARY);
        buttonCancel.addStyleName("cancel");
        
        buttonCancel.setClickShortcut(ShortcutAction.KeyCode.ESCAPE);
	}

	private void hookLogicToComponents() {

		selectUsers.addValueChangeListener(e -> {
			updateChecks();
		});

		selectActivities.addValueChangeListener(e -> {
			updateChecks();
		});
		
		buttonSave.addClickListener(e -> {
			if (save()) parentView.enter(null);
			setVisible(true);
		});

		buttonCancel.addClickListener(e -> {
			parentView.enter(null);
		});
	}
    
    private boolean save() {

    	if(!CurrentUser.isUserInRole(SharingType.SHARING_CENTER) && selectActivities.getValue() == null){
			new Notification(null, "É necessário selecionar a atividade para a qual deseja conceder as permissões.", Notification.Type.WARNING_MESSAGE, true).show(Page.getCurrent());
			return false;
    	}

    	User user = this.getUserByCreating();
		if(user == null) return false;
    	
    	//Remove all correspondents Sharings of User on Current Center
    	List<Sharing> userSharings = sharingRepository.findByCenterAndUser(CurrentUser.getCurrentCenter(), user);
    	for(Sharing s : userSharings){
    		if(selectActivities.getValue() != null && !s.isSharingCenter() && s.getActivity().getId() == selectActivities.getValue().getId())
    			sharingRepository.delete(s);
    		else if (selectActivities.getValue() == null && s.isSharingCenter())
    			sharingRepository.delete(s);
    	}
    	
    	//Persists Marked Sharings
    	if(selectActivities.getValue() != null){
    		if(checkActivity0.getValue()) sharingRepository.save(new Sharing(user, SharingType.ATTENDANCE_READ, selectActivities.getValue()));
    		if(checkActivity1.getValue()) sharingRepository.save(new Sharing(user, SharingType.ATTENDANCE_WRITE, selectActivities.getValue()));
    	} else{
    		if(check0.getValue()) sharingRepository.save(new Sharing(user, SharingType.ATTENDANCE_READ, CurrentUser.getCurrentCenter()));
    		if(check1.getValue()) sharingRepository.save(new Sharing(user, SharingType.ATTENDANCE_WRITE, CurrentUser.getCurrentCenter()));
    		if(check2.getValue()) sharingRepository.save(new Sharing(user, SharingType.PERSON_READ, CurrentUser.getCurrentCenter()));
    		if(check3.getValue()) sharingRepository.save(new Sharing(user, SharingType.PERSON_WRITE, CurrentUser.getCurrentCenter()));
    		if(check4.getValue()) sharingRepository.save(new Sharing(user, SharingType.ACTIVITY_READ, CurrentUser.getCurrentCenter()));
    		if(check5.getValue()) sharingRepository.save(new Sharing(user, SharingType.ACTIVITY_WRITE, CurrentUser.getCurrentCenter()));
    		if(check6.getValue()) sharingRepository.save(new Sharing(user, SharingType.SHARING_CENTER, CurrentUser.getCurrentCenter()));
    		if(check7.getValue()) sharingRepository.save(new Sharing(user, SharingType.SHARING_ACTIVITY, CurrentUser.getCurrentCenter()));
    	}
    	
    	new Notification(null,"Compartilhamentos salvos.", Notification.Type.HUMANIZED_MESSAGE, true).show(Page.getCurrent());
		return true;
	}

    private User getUserByCreating(){
    	if(selectUsers.getValue() == null && StringUtils.isEmpty(email.getValue())){
			new Notification(null, "É necessário informar o usuário.", Notification.Type.WARNING_MESSAGE, true).show(Page.getCurrent());
			return null;
    	}
    	
    	if(selectUsers.getValue() != null && !StringUtils.isEmpty(email.getValue())){
			new Notification(null, "O usuário selecionado e novo ao mesmo tempo. Escolha uma ou outra opção.", Notification.Type.WARNING_MESSAGE, true).show(Page.getCurrent());
			return null;
    	}

    	//Selected User
    	if(selectUsers.getValue() != null)
    		return selectUsers.getValue();
    	
    	//New user
    	User user = new User(email.getValue());
        
    	//Validate e-mail
    	Binder<User> binder = new BeanValidationBinder<>(User.class);
		binder.setBean(user);
        binder.bindInstanceFields(this);
        BinderValidationStatus<User> status = binder.validate();
    	if(status.hasErrors()){
			new Notification(null, status.getValidationErrors().get(0).getErrorMessage(), Notification.Type.ERROR_MESSAGE, true).show(Page.getCurrent());
			return null;
		}

    	//New User exists alread
    	User userDB = userRepository.findOneByEmail(user.getEmail()); 
    	if(userDB != null)
    		return userDB;
    	
    	user.setUsername(email.getValue().substring(0,email.getValue().indexOf('@')));
    	user.setName(user.getUsername());
    	user.setPassword("@@@");
    	user.setStatus(UserStatus.NEW_BY_SHARING);
    	
    	//Resolve existent username
    	userDB = userRepository.findOneByUsername(user.getUsername());
    	while(userDB != null){
    		user.setUsername(user.getUsername() + "1");
        	userDB = userRepository.findOneByUsername(user.getUsername());
    	}
    	
    	userRepository.save(user);
    	return user;
    }
    
    public void enter(SharingView parentView, User user, Activity activity) {

    	this.parentView = parentView;
    	
		final boolean persisted = user != null;
		if(persisted){
			selectUsers.setEnabled(false);
			email.setVisible(false);
		}else{
			selectUsers.setEnabled(true);
			email.setVisible(true);
		}

		labelCenter.setValue("Centro: " + CurrentUser.getCurrentCenter().getName());
		selectUsers.clear();
		selectUsers.setItems(parentView.findSharingsUsers());
		selectUsers.setSelectedItem(user);
		selectActivities.clear();
    	selectActivities.setItems(activityRepository.findByCenterOrderByName(CurrentUser.getCurrentCenter()));
    	selectActivities.setSelectedItem(activity);
		email.setValue("");
		
    	setVisible(true);

		// A hack to ensure the whole form is visible
		buttonSave.focus();
		
		updateChecks();
    }
	
	private void updateChecks(){
		selectActivities.setVisible(CurrentUser.isUserInRole(SharingType.SHARING_ACTIVITY));
		layoutCheckCenter.setVisible(CurrentUser.isUserInRole(SharingType.SHARING_CENTER) && selectActivities.getValue() == null);
		layoutCheckActivity.setVisible(CurrentUser.isUserInRole(SharingType.SHARING_ACTIVITY) && selectActivities.getValue() != null);

		User user = selectUsers.getValue();
		if(user == null) user = new User(-1L);
		
		user.setSharings(sharingRepository.findByCenterAndUser(CurrentUser.getCurrentCenter(), user));
		
		check0.setValue(user.hasSharing(CurrentUser.getCurrentCenter(), SharingType.ATTENDANCE_READ));
		check1.setValue(user.hasSharing(CurrentUser.getCurrentCenter(), SharingType.ATTENDANCE_WRITE));
		check2.setValue(user.hasSharing(CurrentUser.getCurrentCenter(), SharingType.PERSON_READ));
		check3.setValue(user.hasSharing(CurrentUser.getCurrentCenter(), SharingType.PERSON_WRITE));
		check4.setValue(user.hasSharing(CurrentUser.getCurrentCenter(), SharingType.ACTIVITY_READ));
		check5.setValue(user.hasSharing(CurrentUser.getCurrentCenter(), SharingType.ACTIVITY_WRITE));
		check6.setValue(user.hasSharing(CurrentUser.getCurrentCenter(), SharingType.SHARING_CENTER));
		check7.setValue(user.hasSharing(CurrentUser.getCurrentCenter(), SharingType.SHARING_ACTIVITY));
		checkActivity0.setValue(user.hasSharing(CurrentUser.getCurrentCenter(), SharingType.ATTENDANCE_READ, selectActivities.getValue()));
		checkActivity1.setValue(user.hasSharing(CurrentUser.getCurrentCenter(), SharingType.ATTENDANCE_WRITE, selectActivities.getValue()));
	}
}
