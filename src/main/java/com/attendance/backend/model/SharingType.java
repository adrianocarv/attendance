package com.attendance.backend.model;

public enum SharingType {
	ATTENDANCE_READ("Visualizar presenças"),
	ATTENDANCE_WRITE("Lançar presenças"),
	PERSON_READ("Visualizar pessoas"),
	PERSON_WRITE("Gerenciar pessoas"),
	ACTIVITY_READ("Visualizar atividades"),
	ACTIVITY_WRITE("Gerenciar atividades"),
	SHARING_ACTIVITY("Compartilhar atividades"),
	SHARING_CENTER("Compartilhar centro");

	private final String name;

	private SharingType(String name) {
		this.name = name;
	}
	
	@Override
	public String toString() {
		return name;
	}
}
