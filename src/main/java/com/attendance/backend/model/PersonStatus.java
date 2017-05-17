package com.attendance.backend.model;

public enum PersonStatus {
	NEW("New"), CHANGED("Changed");

    private final String name;

    private PersonStatus(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
