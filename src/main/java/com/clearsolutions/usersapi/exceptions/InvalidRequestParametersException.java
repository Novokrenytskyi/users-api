package com.clearsolutions.usersapi.exceptions;

public class InvalidRequestParametersException extends RuntimeException {
    public InvalidRequestParametersException(String message) {
        super(message);
    }
}
