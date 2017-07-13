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

	public Sharing(long id) {
		this.id = id;
	}

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
