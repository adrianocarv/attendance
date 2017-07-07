package com.attendance.backend.model;

import java.sql.Date;
import java.time.format.DateTimeFormatter;

public class ActivityAttendance {

	private Date date;
	private String title;
	private Activity activity;
	private Long totalPresent;

	public ActivityAttendance(Long activityId, Date date, String title, Long totalPresent) {
		this.activity = new Activity(activityId);
		this.date = date;
		this.title = title;
		this.totalPresent = totalPresent;
	}

	public ActivityAttendance(Activity activity) {
		this.activity = activity;
	}

	public String getAttendanceTitle() {
		String strDate = date.toLocalDate().format(DateTimeFormatter.ofPattern("E, dd/MM/yyyy"));
		return strDate + " (" + totalPresent + ")";
	}

	//Gettes and settrs
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


	public Long getTotalPresent() {
		return totalPresent;
	}

	public void setTotalPresent(Long totalPresent) {
		this.totalPresent = totalPresent;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

}
