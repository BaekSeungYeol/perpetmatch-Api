package com.perpetmatch.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class UpdateException extends RuntimeException {
    private String who;

    public UpdateException( String who) {
        super(String.format("%s 님은 수정할 권한이 없습니다.", who));
    }

    public String getWho() {
        return who;
    }
}
