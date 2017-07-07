package com.attendance.backend.model;

import java.sql.Date;
import java.sql.Timestamp;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;

@Entity
public class Attendance {

    private @Id @GeneratedValue Long id;
	private Date date;
	private String title;
	private Timestamp lastEditTime;
	private @ManyToOne User lastEditUser;
	private @ManyToOne Activity activity;
	private @ManyToOne Person person;
	private @Transient boolean present;
	
	protected Attendance() {
		this.present = true;
	}

	public Attendance(long id) {
		this.id = id;
	}

	public Attendance(Activity activity, Person person, Date date) {
		this.activity = activity;
		this.person = person;
		this.date = date;
	}

	public String getName(){
		return person.getDisplayName();
	}
	
	@Override
	public boolean equals(Object other) {
		if(other == null || !(other instanceof Attendance) ) return false;
		
		Attendance b = (Attendance) other;
		if(b.getPerson() == null || b.getPerson().getId() == null) return false;
		if(this.getPerson() == null || this.getPerson().getId() == null) return false;
		
		return this.getPerson().getId().longValue() == b.getPerson().getId().longValue();
	}

	//accessors
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public Activity getActivity() {
		return activity;
	}

	public void setActivity(Activity activity) {
		this.activity = activity;
	}

	public Person getPerson() {
		return person;
	}

	public void setPerson(Person person) {
		this.person = person;
	}

	public boolean isPresent() {
		return present;
	}

	public void setPresent(boolean present) {
		this.present = present;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public User getLastEditUser() {
		return lastEditUser;
	}

	public void setLastEditUser(User lastEditUser) {
		this.lastEditUser = lastEditUser;
	}

	public Timestamp getLastEditTime() {
		return lastEditTime;
	}

	public void setLastEditTime(Timestamp lastEditTime) {
		this.lastEditTime = lastEditTime;
	}
	
}
