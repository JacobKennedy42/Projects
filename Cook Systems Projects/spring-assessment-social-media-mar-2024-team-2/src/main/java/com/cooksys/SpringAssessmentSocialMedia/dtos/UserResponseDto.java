package com.cooksys.SpringAssessmentSocialMedia.dtos;

import java.sql.Timestamp;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class UserResponseDto {

    private Long id;

    private ProfileDto profile;

    private String username;

    private Timestamp joined;

}
