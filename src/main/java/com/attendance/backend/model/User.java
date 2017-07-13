package com.attendance.backend.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;

@Entity
public class User {

    private @Id @GeneratedValue Long id;
	private String username;
	private String name;
	private String email;
	private String password;
    private @ManyToOne Center defaultCenter;

    private @Transient List<Center> centers = new ArrayList<Center>();
    private @Transient Center currentCenter;

    protected User() {
	}

	public User(Long id) {
		this.id = id;
	}

	public User(String username) {
		this.username = username;
	}

	public Center getCurrentCenter(){

		if(this.currentCenter == null && this.centers.isEmpty())
			return this.currentCenter; 
		
		if(this.currentCenter != null)
			return this.currentCenter;
		
		for (Center center : this.centers) {
			if(center.isCurrentUserDefault()){
				this.currentCenter = center;
				return this.currentCenter;
			}
		}
		
		this.currentCenter = this.centers.get(0);
		return this.currentCenter;
	}
	
	//accessors
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Center getDefaultCenter() {
		return defaultCenter;
	}

	public void setDefaultCenter(Center defaultCenter) {
		this.defaultCenter = defaultCenter;
	}

	public List<Center> getCenters() {
		return centers;
	}

	public void setCenters(List<Center> centers) {
		this.centers = centers;
	}

	public void setCurrentCenter(Center currentCenter) {
		this.currentCenter = currentCenter;
	}
}
