package com.project.medtech.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class FileEmptyException extends RuntimeException {
    private final String message;
}
