package com.attendance.ui.center;

import org.springframework.beans.factory.annotation.Autowired;

import com.attendance.backend.model.Center;
import com.attendance.backend.repository.SharingUserRepository;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.SelectionMode;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.renderers.HtmlRenderer;
import com.vaadin.ui.themes.ValoTheme;

@SpringComponent
@UIScope
public class CenterView extends CssLayout implements View {

	private static final long serialVersionUID = 1L;

    public static final String VIEW_NAME = "Centros";

	/** Dependences */
    @Autowired private SharingUserRepository sharingUserRepository;
    private final CenterLayout centerLayout;

    /** Components */
	private Button buttonNew= new Button("Novo centro", VaadinIcons.PLUS_CIRCLE);
	private Grid<Center> grid = new Grid<>(Center.class);
	
	@Autowired
	public CenterView(CenterLayout centerLayout) {
		this.centerLayout = centerLayout;
		
		buildLayout();
		configureComponents();
		hookLogicToComponents();
	}

    private void buildLayout(){
        setSizeFull();
        addStyleName("crud-view");

        HorizontalLayout topLayout = new HorizontalLayout(buttonNew);
        topLayout.setWidth("100%");
        topLayout.setStyleName("top-bar");
        topLayout.setComponentAlignment(buttonNew, Alignment.MIDDLE_RIGHT);

    	VerticalLayout vertical = new VerticalLayout(topLayout, grid);
    	vertical.setSizeFull();
    	vertical.setMargin(true);
    	vertical.setStyleName("form-layout");
    	vertical.setExpandRatio(grid,  1);
    	
        addComponents(centerLayout, vertical);
	}
	
	private void configureComponents() {
        buttonNew.addStyleName(ValoTheme.BUTTON_PRIMARY);
		
		grid.setSizeFull();
		grid.setColumns("displayName");
		grid.addColumn(Center::getDisplayCurrentUserDefault, new HtmlRenderer()).setCaption("Padrão");
		grid.getColumn("displayName").setCaption("Centros").setResizable(false).setSortable(true);
		grid.sort("displayName");
		grid.setSelectionMode(SelectionMode.SINGLE);
	}

	private void hookLogicToComponents() {

		buttonNew.addClickListener(e -> centerLayout.enter(this, new Center(null)));

		grid.asSingleSelect().addValueChangeListener(e -> {
			if(e.getValue() == null) return;
			
			centerLayout.enter(this, e.getValue());
		});
	}

    @Override
    public void enter(ViewChangeEvent event) {
    	grid.setVisible(true);
    	centerLayout.setVisible(false);

    	this.loadGrid();
    }
    
    void loadGrid(){
    	grid.setItems(sharingUserRepository.findCurrentUserCenters());
    }
}
