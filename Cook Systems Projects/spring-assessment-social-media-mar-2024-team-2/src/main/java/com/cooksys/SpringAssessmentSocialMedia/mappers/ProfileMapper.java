package com.cooksys.SpringAssessmentSocialMedia.mappers;

import java.util.List;

import org.mapstruct.Mapper;

import com.cooksys.SpringAssessmentSocialMedia.dtos.ProfileDto;
import com.cooksys.SpringAssessmentSocialMedia.entities.Profile;

@Mapper(componentModel = "spring")
public interface ProfileMapper {
    ProfileDto entityToDto(Profile entity);
    List<ProfileDto> entitiesToDtos (List<Profile> entities);
    Profile dtoToEntity (ProfileDto request);
    List<Profile> dtosToEntities (List<ProfileDto> requests);
}
