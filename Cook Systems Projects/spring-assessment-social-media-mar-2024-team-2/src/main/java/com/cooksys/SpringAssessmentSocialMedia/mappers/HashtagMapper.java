package com.cooksys.SpringAssessmentSocialMedia.mappers;

import java.util.List;

import org.mapstruct.Mapper;

import com.cooksys.SpringAssessmentSocialMedia.dtos.HashtagDto;
import com.cooksys.SpringAssessmentSocialMedia.entities.Hashtag;

@Mapper(componentModel = "spring")
public interface HashtagMapper {
    HashtagDto entityToDto(Hashtag entity);
    List<HashtagDto> entitiesToDtos (List<Hashtag> entities);
    Hashtag dtoToEntity (HashtagDto request);
    List<Hashtag> dtosToEntities (List<HashtagDto> requests);
}
