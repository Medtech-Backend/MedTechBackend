package com.project.medtech.dto.enums;

public enum Education {

    ELEMENTARY("начальное"),
    SECONDARY("среднее"),
    SECONDARY_SPECIALIZED("среднее специальное"),
    SECONDARY_INCOMPLETE("не оконченное среднее"),
    SPECIALIZED_TECHNICAL("среднее техническое"),
    HIGHER("высшее"),
    COMPLETED_HIGHER("оконченное высшее"),
    NONE("без");

    private final String type;

    Education(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
