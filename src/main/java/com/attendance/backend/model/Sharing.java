package com.attendance.backend.model;

import java.sql.Timestamp;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
public class Sharing {

    private @Id @GeneratedValue Long id;
	private @ManyToOne User user;
	private @ManyToOne Center center;
	private @ManyToOne Activity activity;
    private SharingType type;
    private SharingStatus status;
	private Timestamp statusTime;

	protected Sharing() {
	}

	public Sharing(Long id) {
		this.id = id;
	}

	public Sharing(User user, SharingType type, Center center) {
		this.user = user;
		this.type = type;
		this.center = center;
		this.status = SharingStatus.ACCEPTED;
		this.statusTime = new Timestamp(System.currentTimeMillis());
	}
	
	public Sharing(User user, SharingType type, Activity activity) {
		this.user = user;
		this.type = type;
		this.activity = activity;
		this.status = SharingStatus.ACCEPTED;
		this.statusTime = new Timestamp(System.currentTimeMillis());
	}
	
	public boolean matches(SharingType type, Center center){

		//nulls verifications
		if(type == null || center == null || this.type == null || this.getSharingCenter() == null )
			return false;
		
		//validate center
		if(this.getSharingCenter().getId() != center.getId())
			return false;

		//validate type
		if(this.type == type)
			return true;
		
		if(this.type == SharingType.ATTENDANCE_WRITE && type == SharingType.ATTENDANCE_READ) return true;
		if(this.type == SharingType.PERSON_WRITE && type == SharingType.PERSON_READ) return true;
		if(this.type == SharingType.ACTIVITY_WRITE && type == SharingType.ACTIVITY_READ) return true;

		return false;
	}
	
	public boolean matches(SharingType type, Center center, Activity activity){

		//Center has priority
		if(this.isSharingCenter()) 
			return this.matches(type, center);
		
		//First verification
		if(!this.matches(type, center))
			return false;
		
		//null verifications
		if(activity == null)
			return false;
		
		//validate activity
		return this.activity.getId() == activity.getId();
	}
	
	public Center getSharingCenter(){
		return isSharingCenter() ? this.center : this.activity.getCenter();
	}
	
	public boolean isSharingCenter(){
		return this.center != null && this.activity == null;
	}
	
	//accessors
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Activity getActivity() {
		return activity;
	}

	public void setActivity(Activity activity) {
		this.activity = activity;
	}

	public Center getCenter() {
		return center;
	}

	public void setCenter(Center center) {
		this.center = center;
	}

	public SharingType getType() {
		return type;
	}

	public void setType(SharingType type) {
		this.type = type;
	}

	public SharingStatus getStatus() {
		return status;
	}

	public void setStatus(SharingStatus status) {
		this.status = status;
	}

	public Timestamp getStatusTime() {
		return statusTime;
	}

	public void setStatusTime(Timestamp statusTime) {
		this.statusTime = statusTime;
	}
	
}
