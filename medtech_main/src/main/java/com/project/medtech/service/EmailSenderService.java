package com.project.medtech.service;

import lombok.SneakyThrows;

public interface EmailSenderService {

    @SneakyThrows
    String send(String email, String type);
}
