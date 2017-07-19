package com.attendance.ui.center;

import org.springframework.beans.factory.annotation.Autowired;

import com.attendance.backend.model.Center;
import com.attendance.backend.model.User;
import com.attendance.backend.repository.CenterRepository;
import com.attendance.backend.repository.UserRepository;
import com.attendance.ui.authentication.CurrentUser;
import com.attendance.ui.util.SafeButton;
import com.vaadin.data.BeanValidationBinder;
import com.vaadin.data.Binder;
import com.vaadin.data.BinderValidationStatus;
import com.vaadin.event.ShortcutAction;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.Page;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

@SpringComponent
@UIScope
public class CenterLayout extends CssLayout {

	private static final long serialVersionUID = 1L;

    /** Dependences */
	@Autowired private CenterRepository centerRepository;
	@Autowired private UserRepository userRepository;

    /** Components */
	private Label labelId = new Label();
	private TextField name = new TextField("Nome");
	private TextField description = new TextField("Descrição");
	private CheckBox checkDefault = new CheckBox();
    private Button buttonSave = new Button("Salvar");
    private Button buttonCancel = new Button("Cancelar");
    private SafeButton buttonDelete = new SafeButton("Excluir", "Confirma a exclusão do Centro?");

	private Binder<Center> binder = new BeanValidationBinder<>(Center.class);

	private CenterView parentView;
	private Center current;
	
	public CenterLayout() {
		
		buildLayout();
		configureComponents();
		hookLogicToComponents();

	}

    private void buildLayout(){
    	setVisible(false);
    	
    	setStyleName("product-form-wrapper");
    	
    	VerticalLayout vertical = new VerticalLayout(labelId, name, description, checkDefault, buttonSave, buttonCancel, buttonDelete, buttonDelete);
    	vertical.setMargin(false);
    	vertical.setStyleName("form-layout");
    	
    	addComponent(vertical);
	}
	private void configureComponents() {
        
		// bind using naming convention
        binder.bindInstanceFields(this);

		name.setSizeFull();
		description.setSizeFull();
		checkDefault.setSizeFull();
		checkDefault.setCaptionAsHtml(true);
		
		buttonSave.addStyleName(ValoTheme.BUTTON_PRIMARY);
        buttonCancel.addStyleName("cancel");
        buttonDelete.addStyleName(ValoTheme.BUTTON_DANGER);
        
        buttonSave.setClickShortcut(ShortcutAction.KeyCode.ENTER);
        buttonCancel.setClickShortcut(ShortcutAction.KeyCode.ESCAPE);
	}

	private void hookLogicToComponents() {

		checkDefault.addValueChangeListener(e -> {
		    String star =e.getValue() ? VaadinIcons.STAR.getHtml() : VaadinIcons.STAR_O.getHtml();
		    checkDefault.setCaption(star + " Centro padrão");
		});

		buttonSave.addClickListener(e -> {
			if (save()) parentView.enter(null);
		});

		buttonCancel.addClickListener(e -> {
			current = null;
			parentView.enter(null);
		});

	    ClickListener buttonDeleteListener = new ClickListener() {
	        private static final long serialVersionUID = 1L;
	        @Override
	        public void buttonClick(ClickEvent event) {
				if (delete()) parentView.enter(null);
	        }
	      };
	      buttonDelete.setYesListener(buttonDeleteListener);
	}

	private boolean save() {
    	
		this.updateCheckDefault(checkDefault.getValue());
		
    	//Center Owner Security
		if(!current.isCurrentUserOwner())
			return true;
		
		BinderValidationStatus<Center> status = binder.validate();
    	if(!status.hasErrors()){

    		centerRepository.save(current);
			new Notification(null,"Atividade salva.", Notification.Type.HUMANIZED_MESSAGE, true).show(Page.getCurrent());

	    	//Update User Sharings
	    	if(CurrentUser.getCenters().size() == 1) CurrentUser.getUser().setCurrentCenter(current);

	    	return true;

    	} else{
			new Notification(null, status.getValidationErrors().get(0).getErrorMessage(), Notification.Type.ERROR_MESSAGE, true).show(Page.getCurrent());
			return false;
		}
	}

	//TODO Remover @SuppressWarnings
	@SuppressWarnings("unused")
	private boolean delete() {

		//TODO Verificar se há pessoas ou atividades cadastradas
		if (true) {

			centerRepository.delete(current);
			new Notification(null,"Atividade excluída.", Notification.Type.HUMANIZED_MESSAGE, true).show(Page.getCurrent());

	    	//Update User Sharings
	    	if(current.getId() == CurrentUser.getCurrentCenter().getId()) CurrentUser.getUser().setCurrentCenter(CurrentUser.getUser().getDefaultCenter());

	    	return true;

		} else {
			new Notification(null, "Exclusão não permitidas, pois há pessoas e/ou atividades atreladas ao centro.", Notification.Type.WARNING_MESSAGE, true).show(Page.getCurrent());
			return false;
		}
	}

	public void enter(CenterView parentView, Center center) {
    	this.parentView = parentView;
    	this.current = center;
    	
		final boolean persisted = center.getId() != null;
		if(persisted){
			current = centerRepository.findOne(center.getId());
		}else{
			current = center;
		}
		
		binder.setBean(current);

	    labelId.setValue("Id: " + current.getId());

	    String star = current.isCurrentUserDefault() ? VaadinIcons.STAR.getHtml() : VaadinIcons.STAR_O.getHtml();
	    checkDefault.setCaption(star + " Centro padrão");
	    checkDefault.setValue(current.isCurrentUserDefault());
	    
		setVisible(true);

    	//Center Owner Security
	    name.setEnabled(current.isCurrentUserOwner());
	    description.setEnabled(current.isCurrentUserOwner());
	    buttonDelete.setVisible(current.isCurrentUserOwner());

    	// A hack to ensure the whole form is visible
		buttonSave.focus();
		name.selectAll();
    }
	
	private void updateCheckDefault(boolean checked){
	    User currentUserDB = userRepository.findOne(CurrentUser.getUser().getId());
	    if(checked){
	    	currentUserDB.setDefaultCenter(current);
		    userRepository.save(currentUserDB);
		    CurrentUser.getUser().setDefaultCenter(current);
	    }else{
	    	currentUserDB.setDefaultCenter(null);
		    userRepository.save(currentUserDB);
		    CurrentUser.getUser().setDefaultCenter(null);
	    }
	    
    	parentView.loadGrid();
	}

}
