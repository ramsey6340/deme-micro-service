package com.infinity.servicecause.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class RessourceExistantException extends RuntimeException {

    public RessourceExistantException(String message) {
        super(message);
    }
}
