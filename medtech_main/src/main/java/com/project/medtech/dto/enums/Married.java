package com.project.medtech.dto.enums;

public enum Married {

    YES("да"),
    NO("нет"),
    CIVIL_MARRIAGE("гражданский брак");

    private final String type;

    Married(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
