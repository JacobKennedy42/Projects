package com.cooksys.SpringAssessmentSocialMedia.services;

import com.cooksys.SpringAssessmentSocialMedia.dtos.HashtagDto;
import com.cooksys.SpringAssessmentSocialMedia.dtos.TweetResponseDto;

import java.util.List;

public interface HashtagService {
    List<HashtagDto> getAllTags();

    List<TweetResponseDto> getTweetsByHashtagLabel(String label);
}
