
package com.attendance.backend.model;

public enum UserStatus {
	NEW("NEW"), NEW_BY_SHARING("NEW_BY_SHARING"), CHANGED_MAIL("CHANGED_MAIL"), FORGOT_PASSWORD("FORGOT_PASSWORD");

    private final String name;

    private UserStatus(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
