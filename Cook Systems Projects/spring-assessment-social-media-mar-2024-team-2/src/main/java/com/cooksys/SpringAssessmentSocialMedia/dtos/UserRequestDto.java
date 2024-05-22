package com.cooksys.SpringAssessmentSocialMedia.dtos;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class UserRequestDto {

    private String username;

    private ProfileDto profile;

    private CredentialsDto credentials;

}
