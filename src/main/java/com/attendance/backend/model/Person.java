package com.attendance.backend.model;

import java.sql.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
public class Person {

    private @Id @GeneratedValue Long id;
	private String name;
	private String email;
	private String phone;
	private Date birthday;
	private @ManyToOne Center center;
	private String tag1;
	private String tag2;
	private String tag3;
	private String tag4;
	private String waGuest;
	private String waEntered;
	private String waStatus;
	private String waComment;

	protected Person() {
	}

	public Person(long id) {
		this.id = id;
	}

	public Person(long id, String name) {
		this.id = id;
		this.name = name;
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

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public Date getBirthday() {
		return birthday;
	}

	public void setBirthday(Date birthday) {
		this.birthday = birthday;
	}

	public Center getCenter() {
		return center;
	}

	public void setCenter(Center center) {
		this.center = center;
	}

	public String getTag1() {
		return tag1;
	}

	public void setTag1(String tag1) {
		this.tag1 = tag1;
	}

	public String getTag2() {
		return tag2;
	}

	public void setTag2(String tag2) {
		this.tag2 = tag2;
	}

	public String getTag3() {
		return tag3;
	}

	public void setTag3(String tag3) {
		this.tag3 = tag3;
	}

	public String getTag4() {
		return tag4;
	}

	public void setTag4(String tag4) {
		this.tag4 = tag4;
	}

	public String getWaGuest() {
		return waGuest;
	}

	public void setWaGuest(String waGuest) {
		this.waGuest = waGuest;
	}

	public String getWaEntered() {
		return waEntered;
	}

	public void setWaEntered(String waEntered) {
		this.waEntered = waEntered;
	}

	public String getWaStatus() {
		return waStatus;
	}

	public void setWaStatus(String waStatus) {
		this.waStatus = waStatus;
	}

	public String getWaComment() {
		return waComment;
	}

	public void setWaComment(String waComment) {
		this.waComment = waComment;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
}
