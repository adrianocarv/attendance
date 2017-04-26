package com.attendance.backend.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
public class Sharing {

    private @Id @GeneratedValue Long id;
	private @ManyToOne User user;
	private @ManyToOne Activity activity;
	private Character type;

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

	public Character getType() {
		return type;
	}

	public void setType(Character type) {
		this.type = type;
	}
}
