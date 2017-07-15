package com.attendance.backend.model;

import java.sql.Timestamp;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import com.attendance.ui.authentication.CurrentUser;

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
	
	public boolean matches(SharingType type, Activity activity){

		if(type == null)
			return false;
		
		//validate center
		if(this.getSharingCenter().getId() != CurrentUser.getCurrentCenter().getId())
			return false;

		return activity == null ? this.matches(type) : this.matchesActivity(type, activity);
	}

	private boolean matches(SharingType type){

		//validate type
		if(this.type == type)
			return true;
		
		if(this.type == SharingType.ATTENDANCE_WRITE && type == SharingType.ATTENDANCE_READ) return true;
		if(this.type == SharingType.PERSON_WRITE && type == SharingType.PERSON_READ) return true;
		if(this.type == SharingType.ACTIVITY_WRITE && type == SharingType.ACTIVITY_READ) return true;

		return false;
	}
	
	private boolean matchesActivity(SharingType type, Activity activity){

		//sharing center has priority
		if(this.isSharingCenter())
			return this.matches(type);
		
		//validate activity
		if(this.activity.getId() != activity.getId())
			return false;
		
		//validate type
		if(this.type == type)
			return true;

		if(this.type == SharingType.ATTENDANCE_WRITE && type == SharingType.ATTENDANCE_READ) return true;
		
		return false;
	}
	
	private boolean isSharingCenter(){
		return this.activity == null && this.center != null;
	}
	
	private Center getSharingCenter(){
		return this.isSharingCenter() ? this.getCenter() : this.activity.getCenter();
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
