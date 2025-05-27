package com.jenkins.platform.model;

public enum BuildStatus {
    SUCCESS("SUCCESS"),
    FAILURE("FAILURE"),
    IN_PROGRESS("IN_PROGRESS"),
    NOT_BUILT("NOT_BUILT"),
    ABORTED("ABORTED"),
    UNKNOWN("UNKNOWN");

    private final String value;

    BuildStatus(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static BuildStatus fromString(String text) {
        for (BuildStatus status : BuildStatus.values()) {
            if (status.value.equalsIgnoreCase(text)) {
                return status;
            }
        }
        return UNKNOWN;
    }
}