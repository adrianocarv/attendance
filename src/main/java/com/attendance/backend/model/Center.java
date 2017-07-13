package com.attendance.backend.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import com.attendance.ui.authentication.CurrentUser;
import com.vaadin.icons.VaadinIcons;

@Entity
public class Center {

    private @Id @GeneratedValue Long id;
	private String name;
	private String description;
	private @ManyToOne User owner;

	protected Center() {
	}

	public Center(Long id) {
		this.id = id;
		this.owner = CurrentUser.getUser();
	}

	public Center(Long id, String name, String description, User owner) {
	    this.id = id;
	    this.name = name;
	    this.description = description;
	    this.owner = owner;
	}

	public boolean isCurrentUserOwner(){
		return owner != null && owner.getId() == CurrentUser.getUser().getId();
	}
	
	public boolean isCurrentUserDefault(){
		if(CurrentUser.getUser().getDefaultCenter() == null)
			return false;
		return id == CurrentUser.getUser().getDefaultCenter().getId();
	}
	
	public String getDisplayName(){
		return isCurrentUserOwner() ? name : name + " (compartilhado)";
	}
	
	public String getDisplayCurrentUserDefault(){
		String display = isCurrentUserDefault() ? VaadinIcons.STAR.getHtml() : VaadinIcons.STAR_O.getHtml();
		return display;
	}

	//accessors
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public User getOwner() {
		return owner;
	}

	public void setOwner(User owner) {
		this.owner = owner;
	}
}
