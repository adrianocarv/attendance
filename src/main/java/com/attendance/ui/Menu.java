package com.attendance.ui;

import java.util.HashMap;
import java.util.Map;

import com.attendance.backend.model.Center;
import com.attendance.backend.model.SharingType;
import com.attendance.ui.activity.ActivityView;
import com.attendance.ui.attendance.AttendanceView;
import com.attendance.ui.authentication.CurrentUser;
import com.attendance.ui.person.PersonView;
import com.attendance.ui.sharing.SharingView;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.navigator.Navigator;
import com.vaadin.navigator.View;
import com.vaadin.server.Page;
import com.vaadin.server.Resource;
import com.vaadin.server.ThemeResource;
import com.vaadin.server.VaadinSession;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.MenuBar;
import com.vaadin.ui.MenuBar.Command;
import com.vaadin.ui.MenuBar.MenuItem;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

/**
 * Responsive navigation menu presenting a list of available views to the user.
 */
public class Menu extends CssLayout {

	private static final long serialVersionUID = 1L;

	private static final String VALO_MENUITEMS = "valo-menuitems";
    private static final String VALO_MENU_TOGGLE = "valo-menu-toggle";
    private static final String VALO_MENU_VISIBLE = "valo-menu-visible";

    private Navigator navigator;
    private Map<String, Button> viewButtons = new HashMap<String, Button>();

    private CssLayout menuItemsLayout;
    private CssLayout menuPart;
    
    //Refresh components
    private Label title = new Label("");
    private MenuItem userMenu;
    private ComboBox<Center> selectCenter = new ComboBox<Center>();
    
    public Menu(Navigator navigator) {

    	this.navigator = navigator;
        setPrimaryStyleName(ValoTheme.MENU_ROOT);

        menuPart = new CssLayout();
        menuPart.addStyleName(ValoTheme.MENU_PART);

        menuPart.addComponent(buildHeader());
        menuPart.addComponent(buildUser());
        menuPart.addComponent(buildShowMenu());
        menuPart.addComponent(buildMenuItems());

        addComponent(menuPart);
    }

    private Component buildHeader() {

    	final HorizontalLayout top = new HorizontalLayout();
        top.setDefaultComponentAlignment(Alignment.MIDDLE_LEFT);
        top.addStyleName(ValoTheme.MENU_TITLE);
        title.addStyleName(ValoTheme.LABEL_H3);
        title.setSizeUndefined();

        //Logo
        //Image image = new Image(null, new ThemeResource("img/table-logo.png"));
        //image.setStyleName("logo");
        //top.addComponent(image);

        top.addComponent(title);
        
        return top;
    }

   	private Component buildUser() {
        final MenuBar settings = new MenuBar();
        settings.addStyleName("user-menu");

        userMenu = settings.addItem("", new ThemeResource("img/user-logged9.png"), null);

        userMenu.addItem("Perfil", new Command() {
			private static final long serialVersionUID = 1L;

			@Override
            public void menuSelected(final MenuItem selectedItem) {
				//navigator.navigateTo(UserView.VIEW_NAME);
            }
        });

        userMenu.addSeparator();

        userMenu.addItem("Sair", new Command() {
			private static final long serialVersionUID = 1L;

			@Override
            public void menuSelected(final MenuItem selectedItem) {
                VaadinSession.getCurrent().getSession().invalidate();
                Page.getCurrent().reload();
            }
        });

        return settings;
    }   
   	
   	private Component buildShowMenu() {
        // button for toggling the visibility of the menu when on a small screen
        final Button showMenu = new Button("Menu", new ClickListener() {
        	private static final long serialVersionUID = 1L;

        	@Override
            public void buttonClick(final ClickEvent event) {
                if (menuPart.getStyleName().contains(VALO_MENU_VISIBLE)) {
                    menuPart.removeStyleName(VALO_MENU_VISIBLE);
                } else {
                    menuPart.addStyleName(VALO_MENU_VISIBLE);
                }
            }
        });
        showMenu.addStyleName(ValoTheme.BUTTON_PRIMARY);
        showMenu.addStyleName(ValoTheme.BUTTON_SMALL);
        showMenu.addStyleName(VALO_MENU_TOGGLE);
        showMenu.setIcon(VaadinIcons.MENU);
        
        return showMenu;
   	}
   	
   	private Component buildMenuItems() {
        // container for the navigation buttons, which are added by addView()
        menuItemsLayout = new CssLayout();
        menuItemsLayout.setPrimaryStyleName(VALO_MENUITEMS);
        
        return menuItemsLayout;
   	}
   	
   	private Component buildSelectCenter() {

    	selectCenter.setItemCaptionGenerator(Center::getName);
   		selectCenter.setEmptySelectionAllowed(false);
        selectCenter.removeStyleName(VALO_MENUITEMS);

        selectCenter.addValueChangeListener(e -> {
		    if(e.getValue() == null || e.getValue() == e.getOldValue()) return;

		    CurrentUser.getUser().setCurrentCenter(e.getValue());
	    	this.refresh(false);
            
	    	//Hack to refresh current page
	    	String viewName = navigator.getState();
	    	navigator.navigateTo("");
	    	navigator.navigateTo(viewName);
		});

    	VerticalLayout layout = new VerticalLayout();
    	layout.setMargin(false);
    	layout.setSpacing(false);
    	layout.addStyleName("combo-menu");

        layout.addComponents(selectCenter);
        
        return layout;
   	}
    
    public void addView(View view, final String name, String caption, Resource icon) {
        navigator.addView(name, view);
        createViewButton(name, caption, icon);
    }

    public void addView(View view, final String name) {
        navigator.addView(name, view);
    }
    
    public void addSelectCenter() {
        menuItemsLayout.addComponent(buildSelectCenter());
    }

    private void createViewButton(final String name, String caption, Resource icon) {
        Button button = new Button(caption, new ClickListener() {
        	private static final long serialVersionUID = 1L;

            @Override
            public void buttonClick(ClickEvent event) {
                navigator.navigateTo(name);

            }
        });
        button.setPrimaryStyleName(ValoTheme.MENU_ITEM);
        button.setIcon(icon);
        menuItemsLayout.addComponent(button);
        viewButtons.put(name, button);
    }

    public boolean isVisibleView(String viewName) {
        Button selected = viewButtons.get(viewName);
        if (selected != null) {
            return selected.isVisible();
        }
        return false;
	}

    public void setActiveView(String viewName) {
        for (Button button : viewButtons.values()) {
            button.removeStyleName("selected");
        }
        Button selected = viewButtons.get(viewName);
        if (selected != null) {
            selected.addStyleName("selected");
        }
        menuPart.removeStyleName(VALO_MENU_VISIBLE);
    }
    
    public void refresh(){
    	this.refresh(true);;
    }
    
    private void refresh(boolean updatesSelectCenter){
    	if(updatesSelectCenter){
        	this.selectCenter.setItems(CurrentUser.getCenters());
        	this.selectCenter.setSelectedItem(CurrentUser.getCurrentCenter());
        	this.selectCenter.setVisible(CurrentUser.getCenters().size() > 1);
    	}
    	this.title.setValue(CurrentUser.getCurrentCenter().getName());
    	this.userMenu.setText(CurrentUser.getUser().getUsername());
    	this.setViewsVisibility();
    }
    
	private void setViewsVisibility() {
		for (Button button : viewButtons.values()) {
			boolean visible = true;
			switch (button.getCaption()) {
			case AttendanceView.VIEW_NAME:
				visible = CurrentUser.isUserInRole(SharingType.ATTENDANCE_READ);
				break;
			case PersonView.VIEW_NAME:
				visible = CurrentUser.isUserInRole(SharingType.PERSON_READ);
				break;
			case ActivityView.VIEW_NAME:
				visible = CurrentUser.isUserInRole(SharingType.ACTIVITY_READ);
				break;
			case SharingView.VIEW_NAME:
				visible = CurrentUser.isUserInRolesOR(SharingType.SHARING_CENTER, SharingType.SHARING_ACTIVITY);
				break;
			}
			button.setVisible(visible);
		}
	}
}
