package com.project.medtech.dto;

import lombok.Data;

@Data
public class RequestPatient {
    private long patientId;

    public RequestPatient(long patientId) {
        this.patientId = patientId;
    }
}
