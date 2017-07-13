package com.attendance.backend.model;

public enum SharingStatus {
	INVITED("Convidado"), ACCEPTED("Aceito");

    private final String name;

    private SharingStatus(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
