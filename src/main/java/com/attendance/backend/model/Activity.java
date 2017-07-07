
package com.attendance.backend.model;

import java.sql.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Entity
public class Activity {

    private @Id @GeneratedValue Long id;

    @NotNull
    @Size(min = 3, message = "Atividade deve ter pelo menos três caracteres")
    private String name;
	
    private String nameComplement;
	
    private String description;
	
	private boolean checkTitleRequired;

	private Integer personSuggestionByEvents;

	private Integer personSuggestionByDays;

	private Integer resumoMensalId;

	private Date lastAttendanceDate;

	private Long lastAttendanceTotal;
    
    private @ManyToOne Center center;

    private @Transient Integer totalAttendance = 0;
    
    protected Activity() {
	}

	public Activity(Long id) {
		this.id = id;
	}

	public Integer getTotalAttendance() {
		return totalAttendance;
	}

	public void setTotalAttendance(Integer totalAttendance) {
		this.totalAttendance = totalAttendance;
	}

	public String getDisplayName() {
		if(lastAttendanceDate == null)
			return name;

		String display = name;
		display += " (" + lastAttendanceTotal + ") ";
		
		//days distance
		if(lastAttendanceDate.getTime() > System.currentTimeMillis())
			return display += "Data futura";

		long diff = System.currentTimeMillis() - lastAttendanceDate.getTime();
		long days = diff / (24 * 60 * 60 * 1000);

		if(days == 0)
			return display += "HOJE";
		
		if(days == 1)
			return display += "Ontem";

		display += days + "d";
		
		return display;
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

	public Date getLastAttendanceDate() {
		return lastAttendanceDate;
	}

	public void setLastAttendanceDate(Date lastAttendanceDate) {
		this.lastAttendanceDate = lastAttendanceDate;
	}

	public Long getLastAttendanceTotal() {
		return lastAttendanceTotal;
	}

	public void setLastAttendanceTotal(Long lastAttendanceTotal) {
		this.lastAttendanceTotal = lastAttendanceTotal;
	}

	public Integer getPersonSuggestionByEvents() {
		return personSuggestionByEvents;
	}

	public void setPersonSuggestionByEvents(Integer personSugestionByEvents) {
		this.personSuggestionByEvents = personSugestionByEvents;
	}

	public Integer getPersonSuggestionByDays() {
		return personSuggestionByDays;
	}

	public void setPersonSuggestionByDays(Integer personSugestionByDays) {
		this.personSuggestionByDays = personSugestionByDays;
	}

	public boolean isCheckTitleRequired() {
		return checkTitleRequired;
	}

	public void setCheckTitleRequired(boolean checkTitleRequired) {
		this.checkTitleRequired = checkTitleRequired;
	}

	public Integer getResumoMensalId() {
		return resumoMensalId;
	}

	public void setResumoMensalId(Integer resumoMensalId) {
		this.resumoMensalId = resumoMensalId;
	}
	
}
