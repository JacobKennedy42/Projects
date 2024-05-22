package com.cooksys.SpringAssessmentSocialMedia.exceptions;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;

@AllArgsConstructor
@Getter
@Setter
public class NotFoundException extends RuntimeException{
    @Serial
    private static final long serialVersionUID = -3237599932894681148L;

    private String message;
}
