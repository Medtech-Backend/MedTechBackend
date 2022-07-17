package com.project.medtech.dto.enums;

public enum LateRegistration {

    OTHER_MEDICAL_ORGANIZATION("отдельная мед. организация"),
    NO_PERMANENT_INCOME_SOURCE("отсутствие постоянного источника дохода"),
    NO_DOCUMENTS("отсутствие документов"),
    NO_REGISTRATION("отсутствие прописки"),
    OUT_OF_IGNORANCE("по незнанию");

    private final String type;

    LateRegistration(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
