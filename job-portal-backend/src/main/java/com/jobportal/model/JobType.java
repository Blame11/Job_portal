package com.jobportal.model;

public enum JobType {
    FULL_TIME("full-time"),
    PART_TIME("part-time"),
    INTERNSHIP("internship");

    private final String displayValue;

    JobType(String displayValue) {
        this.displayValue = displayValue;
    }

    public String getDisplayValue() {
        return displayValue;
    }

    public static JobType fromDisplayValue(String value) {
        for (JobType type : JobType.values()) {
            if (type.displayValue.equals(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Invalid job type: " + value);
    }
}
