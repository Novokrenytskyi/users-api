package com.clearsolutions.usersapi.exceptions;

public class UserEmailAlreadyInUseException extends RuntimeException{
    public UserEmailAlreadyInUseException(String message) {
        super(message);
    }
}
