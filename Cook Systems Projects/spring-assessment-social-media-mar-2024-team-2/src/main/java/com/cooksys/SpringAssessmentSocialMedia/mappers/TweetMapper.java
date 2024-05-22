package com.cooksys.SpringAssessmentSocialMedia.mappers;

import java.util.List;

import org.mapstruct.Mapper;

import com.cooksys.SpringAssessmentSocialMedia.dtos.TweetRequestDto;
import com.cooksys.SpringAssessmentSocialMedia.dtos.TweetResponseDto;
import com.cooksys.SpringAssessmentSocialMedia.entities.Tweet;


@Mapper(componentModel = "spring", uses = {UserMapper.class, HashtagMapper.class})
public interface TweetMapper {
    TweetResponseDto entityToDto(Tweet entity);
    List<TweetResponseDto> entitiesToDtos (List<Tweet> entities);
    Tweet dtoToEntity (TweetRequestDto request);
    List<Tweet> dtosToEntities (List<TweetRequestDto> requests);
}
