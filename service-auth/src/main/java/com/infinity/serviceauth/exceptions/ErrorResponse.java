package com.infinity.serviceauth.exceptions;

import lombok.Data;

@Data
public class ErrorResponse {
    private String timestamp;
    private int status;
    private String error;
    private String message;
    private String path;

}

