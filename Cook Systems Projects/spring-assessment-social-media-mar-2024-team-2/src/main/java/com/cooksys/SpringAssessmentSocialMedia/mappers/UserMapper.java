package com.cooksys.SpringAssessmentSocialMedia.mappers;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.cooksys.SpringAssessmentSocialMedia.dtos.UserRequestDto;
import com.cooksys.SpringAssessmentSocialMedia.dtos.UserResponseDto;
import com.cooksys.SpringAssessmentSocialMedia.entities.User;

@Mapper(componentModel = "spring", uses = {ProfileMapper.class, CredentialsMapper.class})
public interface UserMapper {
    
    // UserResponseDto entityToDto(User entity);
    // List<UserResponseDto> entitiesToDtos (List<User> entities);
    // User dtoToEntity (UserRequestDto request);
    // List<User> dtosToEntities (List<UserRequestDto> requests);

    @Mapping(target = "username", source = "credentials.username")
    // @Mapping(target = "joined", source = "joined")
    UserResponseDto entityToDto(User entity);
    @Mapping(target = "username", source = "credentials.username")
    List<UserResponseDto> entitiesToDtos (List<User> entities);
    User dtoToEntity (UserRequestDto request);
    List<User> dtosToEntities (List<UserRequestDto> requests);
}
