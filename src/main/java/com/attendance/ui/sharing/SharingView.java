package com.attendance.ui.sharing;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.attendance.backend.model.Activity;
import com.attendance.backend.model.Sharing;
import com.attendance.backend.model.SharingType;
import com.attendance.backend.model.User;
import com.attendance.backend.repository.ActivityRepository;
import com.attendance.backend.repository.SharingRepository;
import com.attendance.ui.authentication.CurrentUser;
import com.vaadin.data.provider.GridSortOrder;
import com.vaadin.data.provider.GridSortOrderBuilder;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.SelectionMode;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

@SpringComponent
@UIScope
public class SharingView extends CssLayout implements View {

	private static final long serialVersionUID = 1L;

    public static final String VIEW_NAME = "Compartilhamentos";

	/** Dependences */
    @Autowired private SharingRepository sharingRepository;
    @Autowired private ActivityRepository activityRepository;
    private final SharingLayout sharingLayout;

    /** Components */
	private ComboBox<User> selectUsers = new ComboBox<User>("Usuário");
	private ComboBox<Activity> selectActivities = new ComboBox<Activity>("Atividade");
	private Button buttonNew = new Button("", VaadinIcons.PLUS_CIRCLE);
	private Grid<Sharing> grid = new Grid<>(Sharing.class);
	
	@Autowired
	public SharingView(SharingLayout sharingLayout) {
		this.sharingLayout = sharingLayout;
		
		buildLayout();
		configureComponents();
		hookLogicToComponents();
	}

    private void buildLayout(){
        setSizeFull();
        addStyleName("crud-view");

        HorizontalLayout topLayout = new HorizontalLayout(selectUsers, buttonNew);
        topLayout.setWidth("100%");
        topLayout.setComponentAlignment(selectUsers, Alignment.MIDDLE_LEFT);
        topLayout.setExpandRatio(selectUsers, 3);
        topLayout.setExpandRatio(buttonNew, 1);
        topLayout.setStyleName("top-bar");

    	VerticalLayout vertical = new VerticalLayout(topLayout, selectActivities, grid);
    	vertical.setSizeFull();
    	vertical.setMargin(true);
    	vertical.setStyleName("form-layout");
    	vertical.setExpandRatio(grid,  1);
    	
        addComponents(sharingLayout, vertical);
	}
	
	private void configureComponents() {

    	selectUsers.setItemCaptionGenerator(User::getEmail);
    	selectUsers.setWidth("100%");
    	selectActivities.setItemCaptionGenerator(Activity::getName);
    	selectActivities.setWidth("100%");
    	
    	buttonNew.addStyleName(ValoTheme.BUTTON_PRIMARY);
		
    	grid.setSelectionMode(SelectionMode.SINGLE);
		grid.setSizeFull();
		grid.setColumns("displayUser", "type", "displayActivityName", "status", "displayStatusTime");
		grid.getColumn("displayUser").setCaption("Usuário").setResizable(false).setSortable(true);
		grid.getColumn("type").setCaption("Compartilhamento").setResizable(false).setSortable(true);
		grid.getColumn("displayActivityName").setCaption("Atividade").setResizable(false).setSortable(true);
		grid.getColumn("displayStatusTime").setCaption("Data").setResizable(false).setSortable(true);
	}

	private void hookLogicToComponents() {

		selectUsers.addValueChangeListener(e -> {
			loadGrid(e.getValue(), selectActivities.getValue());
		});

		selectActivities.addValueChangeListener(e -> {
			loadGrid(selectUsers.getValue(), e.getValue());
		});

		buttonNew.addClickListener(e -> sharingLayout.enter(this, null, null));

		grid.asSingleSelect().addValueChangeListener(e -> {
			if(e.getValue() == null) return;
			sharingLayout.enter(this, e.getValue().getUser(), e.getValue().getActivity());
		});
	}

    @Override
    public void enter(ViewChangeEvent event) {

    	//security
    	if(!CurrentUser.isUserInRole(SharingType.SHARING_CENTER) && !CurrentUser.isUserInRole(SharingType.SHARING_ACTIVITY)) return;

    	grid.setVisible(true);
    	sharingLayout.setVisible(false);

    	User user = selectUsers.getValue(); 
    	selectUsers.setItems(this.findSharingsUsers());
    	selectUsers.setSelectedItem(user);
    	Activity activity = selectActivities.getValue(); 
    	selectActivities.setItems(activityRepository.findByCenter(CurrentUser.getCurrentCenter()));
    	selectActivities.setSelectedItem(activity);
		
    	loadGrid(selectUsers.getValue(), selectActivities.getValue());
    }
    
    private void loadGrid(User user, Activity activity){
		grid.setItems(this.findSharings(selectUsers.getValue(), selectActivities.getValue()));

		//Oredering
		GridSortOrderBuilder<Sharing> sort = GridSortOrder.asc(grid.getColumn("displayUser"));
		sort.thenAsc(grid.getColumn("displayActivityName"));
		sort.thenAsc(grid.getColumn("type"));
		grid.setSortOrder(sort);
    }

	private List<Sharing> findSharings(User user, Activity activity) {
		List<Sharing> sharingsDB = new ArrayList<Sharing>();
		if(user == null && activity == null)
			sharingsDB = sharingRepository.findByCenter(CurrentUser.getCurrentCenter());
		else if (user != null && activity == null)
			sharingsDB = sharingRepository.findByCenterAndUser(CurrentUser.getCurrentCenter(), user);
		else if (user == null && activity != null)
			sharingsDB = sharingRepository.findByCenterAndActivity(CurrentUser.getCurrentCenter(), activity);
		else
			sharingsDB = sharingRepository.findByCenterAndUserAndActivity(CurrentUser.getCurrentCenter(), user, activity);
			
		List<Sharing> sharings = new ArrayList<Sharing>();
		for(Sharing s: sharingsDB){
	    	if(s.isSharingCenter() && CurrentUser.isUserInRole(SharingType.SHARING_CENTER))
	    		sharings.add(s);
	    	else if(!s.isSharingCenter() && CurrentUser.isUserInRole(SharingType.SHARING_ACTIVITY))
	    		sharings.add(s);
		}
		
    	return sharings;
	}
	
	List<User> findSharingsUsers() {
		List<Sharing> sharings = this.findSharings(null, null);
		List<User> users= new ArrayList<User>();
		for(Sharing s : sharings){
			if(!users.contains(s.getUser()))
				users.add(s.getUser());
		}
		return users;
	}

}
