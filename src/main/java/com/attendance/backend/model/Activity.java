package com.attendance.backend.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
public class Activity {

    private @Id @GeneratedValue Long id;
	private String name;
	private String nameComplement;
	private String description;
	private @ManyToOne Center center;

	protected Activity() {
	}

	public Activity(Long id) {
		this.id = id;
	}

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

	public String getNameComplement() {
		return nameComplement;
	}

	public void setNameComplement(String nameComplement) {
		this.nameComplement = nameComplement;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Center getCenter() {
		return center;
	}

	public void setCenter(Center center) {
		this.center = center;
	}
}
