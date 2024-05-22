package com.cooksys.SpringAssessmentSocialMedia.services;

import com.cooksys.SpringAssessmentSocialMedia.dtos.CredentialsDto;
import com.cooksys.SpringAssessmentSocialMedia.dtos.TweetResponseDto;
import com.cooksys.SpringAssessmentSocialMedia.dtos.UserRequestDto;
import com.cooksys.SpringAssessmentSocialMedia.dtos.UserResponseDto;

import java.util.List;

public interface UserService {
    List<UserResponseDto> getAllUsers();
    UserResponseDto postUser(UserRequestDto request);
    UserResponseDto getUserByUsername(String username);
    UserResponseDto patchUserProfile(String username, UserRequestDto request);
    UserResponseDto deleteUser (String username, CredentialsDto credentials);
    void followUser (String username, CredentialsDto request);
    void unfollowUser (String username, CredentialsDto request);
    List<TweetResponseDto> getUserFeed (String username);
    List<TweetResponseDto> getUserTweets(String username);
    List<TweetResponseDto> getUserMentions(String username);
    List<UserResponseDto> getUserFollowers(String username);
    List<UserResponseDto> getUserFollowing(String username);
}
