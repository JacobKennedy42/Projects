package com.cooksys.SpringAssessmentSocialMedia.mappers;

import java.util.List;

import org.mapstruct.Mapper;

import com.cooksys.SpringAssessmentSocialMedia.dtos.CredentialsDto;
import com.cooksys.SpringAssessmentSocialMedia.entities.Credentials;

@Mapper(componentModel = "spring")
public interface CredentialsMapper {
    CredentialsDto entityToDto(Credentials entity);
    List<CredentialsDto> entitiesToDtos (List<Credentials> entities);
    Credentials dtoToEntity (CredentialsDto request);
    List<Credentials> dtosToEntities (List<CredentialsDto> requests);
}
