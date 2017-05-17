package com.attendance.backend.model;

import java.math.BigDecimal;
import java.sql.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.Email;
import org.springframework.util.StringUtils;

@Entity
public class Person {

    private @Id @GeneratedValue Long id;

    @NotNull
    @Size(min = 3, message = "Nome deve ter pelo menos três caracteres")
    private String name;

    private String shortName;

    @Email
    private String email;
	
    private String phone;
	
    private Date birthday;
	
    private @ManyToOne Center center;
	
    private String tag1;
	
    private String tag2;
	
    private String tag3;
	
    private String tag4;
	
    private PersonStatus status;

    private boolean checkUniversitario;
    private boolean checkColegial;
    private boolean checkCooperador;
    private Date checkCooperadorDate;
    private boolean checkContribui;
    private BigDecimal checkContribuiValue;

    private boolean checkEstudanteWA;
    private Date checkEstudanteWADate;
    private boolean checkEstudanteMail;
    private Date checkEstudanteMailDate;

    private boolean checkProfissionalWA;
    private Date checkProfissionalWADate;
    private boolean checkProfissionalMail;
    private Date checkProfissionalMailDate;

    private @Transient Integer totalAttendance = 0;

	protected Person() {
	}

	public Person(Long id) {
		this.id = id;
	}

	public Person(long id, String name) {
		this.id = id;
		this.name = name;
	}

	public Person(String name, Center center) {
		this.name = name;
		this.center = center;
	}

	public Integer getTotalAttendance() {
		return totalAttendance;
	}

	public void setTotalAttendance(Integer totalAttendance) {
		this.totalAttendance = totalAttendance;
	}

	public String getDisplayName() {
		return StringUtils.isEmpty(shortName) ? name : shortName;
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

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getShortName() {
		return shortName;
	}

	public void setShortName(String shortName) {
		this.shortName = shortName;
	}

	public PersonStatus getStatus() {
		return status;
	}

	public void setStatus(PersonStatus status) {
		this.status = status;
		this.name += " (NEW)"; 
	}

	public boolean isCheckUniversitario() {
		return checkUniversitario;
	}

	public void setCheckUniversitario(boolean checkUniversitario) {
		this.checkUniversitario = checkUniversitario;
	}

	public boolean isCheckColegial() {
		return checkColegial;
	}

	public void setCheckColegial(boolean checkColegial) {
		this.checkColegial = checkColegial;
	}

	public boolean isCheckCooperador() {
		return checkCooperador;
	}

	public void setCheckCooperador(boolean checkCooperador) {
		this.checkCooperador = checkCooperador;
	}

	public Date getCheckCooperadorDate() {
		return checkCooperadorDate;
	}

	public void setCheckCooperadorDate(Date checkCooperadorDate) {
		this.checkCooperadorDate = checkCooperadorDate;
	}

	public boolean isCheckContribui() {
		return checkContribui;
	}

	public void setCheckContribui(boolean checkContribui) {
		this.checkContribui = checkContribui;
	}

	public BigDecimal getCheckContribuiValue() {
		return checkContribuiValue;
	}

	public void setCheckContribuiValue(BigDecimal checkContribuiValue) {
		this.checkContribuiValue = checkContribuiValue;
	}

	public boolean isCheckEstudanteWA() {
		return checkEstudanteWA;
	}

	public void setCheckEstudanteWA(boolean checkEstudanteWA) {
		this.checkEstudanteWA = checkEstudanteWA;
	}

	public Date getCheckEstudanteWADate() {
		return checkEstudanteWADate;
	}

	public void setCheckEstudanteWADate(Date checkEstudanteWADate) {
		this.checkEstudanteWADate = checkEstudanteWADate;
	}

	public boolean isCheckEstudanteMail() {
		return checkEstudanteMail;
	}

	public void setCheckEstudanteMail(boolean checkEstudanteMail) {
		this.checkEstudanteMail = checkEstudanteMail;
	}

	public Date getCheckEstudanteMailDate() {
		return checkEstudanteMailDate;
	}

	public void setCheckEstudanteMailDate(Date checkEstudanteMailDate) {
		this.checkEstudanteMailDate = checkEstudanteMailDate;
	}

	public boolean isCheckProfissionalWA() {
		return checkProfissionalWA;
	}

	public void setCheckProfissionalWA(boolean checkProfissionalWA) {
		this.checkProfissionalWA = checkProfissionalWA;
	}

	public Date getCheckProfissionalWADate() {
		return checkProfissionalWADate;
	}

	public void setCheckProfissionalWADate(Date checkProfissionalWADate) {
		this.checkProfissionalWADate = checkProfissionalWADate;
	}

	public boolean isCheckProfissionalMail() {
		return checkProfissionalMail;
	}

	public void setCheckProfissionalMail(boolean checkProfissionalMail) {
		this.checkProfissionalMail = checkProfissionalMail;
	}

	public Date getCheckProfissionalMailDate() {
		return checkProfissionalMailDate;
	}

	public void setCheckProfissionalMailDate(Date checkProfissionalMailDate) {
		this.checkProfissionalMailDate = checkProfissionalMailDate;
	}
}
