package com.attendance.backend.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Email;

@Entity
public class User {

    private @Id @GeneratedValue Long id;
	private @NotNull String username;
	private String name;
	private @NotNull @Email String email;
	private String password;
    private @ManyToOne Center defaultCenter;
    private @NotNull boolean activated;

    private @Transient List<Center> centers = new ArrayList<Center>();
    private @Transient List<Sharing> sharings = new ArrayList<Sharing>();
    private @Transient Center currentCenter;

    protected User() {
	}

	public User(Long id) {
		this.id = id;
	}

	public User(String email) {
		this.email = email;
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
	
	public boolean hasSharing(Center center, SharingType type){
		return this.hasSharing(center, type, null);
	}
	
	public boolean hasSharing(Center center, SharingType type, Activity activity){
		if(type == null || center == null)
			return false;
		
		for(Sharing s : this.sharings){
			
			if( (activity == null && !s.isSharingCenter()) || (activity != null && s.isSharingCenter()) )
				continue;

			if(s.getCenter().getId() != center.getId() || s.getType() != type) 
				continue;

			if(!s.isSharingCenter() && s.getActivity().getId() != activity.getId()) 
				continue;

			return true;
		}

		return false;
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

	public List<Sharing> getSharings() {
		return sharings;
	}

	public void setSharings(List<Sharing> sharings) {
		this.sharings = sharings;
	}

	public boolean isActivated() {
		return activated;
	}

	public void setActivated(boolean activated) {
		this.activated = activated;
	}
}
