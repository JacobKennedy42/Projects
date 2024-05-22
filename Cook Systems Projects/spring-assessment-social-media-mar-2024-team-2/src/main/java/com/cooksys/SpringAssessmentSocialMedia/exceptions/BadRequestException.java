package com.cooksys.SpringAssessmentSocialMedia.exceptions;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;

@AllArgsConstructor
@Getter
@Setter
public class BadRequestException extends RuntimeException{
    @Serial
    private static final long serialVersionUID = -6253581322488403108L;

    private String message;
}
