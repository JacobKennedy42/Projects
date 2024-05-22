package com.cooksys.SpringAssessmentSocialMedia.exceptions;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;

@AllArgsConstructor
@Getter
@Setter
public class NotAuthorizedException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 6906260265327570609L;

    private String message;
}
