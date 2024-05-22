package com.cooksys.SpringAssessmentSocialMedia.services;

import java.util.List;

import com.cooksys.SpringAssessmentSocialMedia.dtos.ContextDto;
import com.cooksys.SpringAssessmentSocialMedia.dtos.CredentialsDto;
import com.cooksys.SpringAssessmentSocialMedia.dtos.HashtagDto;
import com.cooksys.SpringAssessmentSocialMedia.dtos.TweetRequestDto;
import com.cooksys.SpringAssessmentSocialMedia.dtos.TweetResponseDto;
import com.cooksys.SpringAssessmentSocialMedia.dtos.UserResponseDto;

public interface TweetService {
    List<TweetResponseDto> getAllTweets();
    TweetResponseDto postTweet (TweetRequestDto request);
    TweetResponseDto getTweetById(Long id);
    ContextDto getTweetContextById(Long id);
    TweetResponseDto deleteTweet(Long id, CredentialsDto credentials);
    TweetResponseDto replyToTweet(Long id, TweetRequestDto tweetReply);
    List<TweetResponseDto> getRepliesToTweet(Long id);
    void likeTweet(Long id, CredentialsDto credentials);
    List<UserResponseDto> getLikesOnTweet(Long id);
    TweetResponseDto repostTweet(Long id, CredentialsDto credentials);
    List<TweetResponseDto> getRepostsOnTweet(Long id);
    List<UserResponseDto> getMentionsInTweet(Long id);
    List<HashtagDto> getTagsOnTweet(Long id);
}
