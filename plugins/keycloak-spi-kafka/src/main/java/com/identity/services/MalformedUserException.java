package com.identity.services;

import lombok.Getter;

public class MalformedUserException extends Throwable {

    public @Getter final String userPath;

    public MalformedUserException(String resourcePath) {
        super(resourcePath);
        this.userPath = resourcePath;
    }
}
