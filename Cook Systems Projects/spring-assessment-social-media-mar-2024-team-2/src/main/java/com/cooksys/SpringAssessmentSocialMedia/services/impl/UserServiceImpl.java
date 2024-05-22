package com.cooksys.SpringAssessmentSocialMedia.services.impl;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.cooksys.SpringAssessmentSocialMedia.dtos.CredentialsDto;
import com.cooksys.SpringAssessmentSocialMedia.dtos.TweetResponseDto;
import com.cooksys.SpringAssessmentSocialMedia.dtos.UserRequestDto;
import com.cooksys.SpringAssessmentSocialMedia.dtos.UserResponseDto;
import com.cooksys.SpringAssessmentSocialMedia.entities.Credentials;
import com.cooksys.SpringAssessmentSocialMedia.entities.Profile;
import com.cooksys.SpringAssessmentSocialMedia.entities.Tweet;
import com.cooksys.SpringAssessmentSocialMedia.entities.User;
import com.cooksys.SpringAssessmentSocialMedia.exceptions.BadRequestException;
import com.cooksys.SpringAssessmentSocialMedia.exceptions.NotAuthorizedException;
import com.cooksys.SpringAssessmentSocialMedia.exceptions.NotFoundException;
import com.cooksys.SpringAssessmentSocialMedia.mappers.CredentialsMapper;
import com.cooksys.SpringAssessmentSocialMedia.mappers.TweetMapper;
import com.cooksys.SpringAssessmentSocialMedia.mappers.UserMapper;
import com.cooksys.SpringAssessmentSocialMedia.repositories.TweetRepository;
import com.cooksys.SpringAssessmentSocialMedia.repositories.UserRepository;
import com.cooksys.SpringAssessmentSocialMedia.services.UserService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final TweetRepository tweetRepository;
    private final TweetMapper tweetMapper;
    private final CredentialsMapper credentialsMapper;

    private boolean checkRequiredFields (User user) {
        Profile profile = user.getProfile();
        Credentials credentials = user.getCredentials();
        return profile == null || profile.getEmail() == null
            || credentials == null || credentials.getUsername() == null || credentials.getPassword() == null;
    }
    private boolean usernameAlreadyTaken (User user) {
        Optional<User> existingUser = userRepository.findByCredentialsUsernameAndDeletedFalse(user.getCredentials().getUsername());
        return existingUser.isPresent();
    }
    private User saveUser (User user) {
        if (checkRequiredFields(user))
            throw new BadRequestException("Email, Username, and Password are required fields");
        if (usernameAlreadyTaken(user))
            throw new BadRequestException("Username already taken");
        
        Optional<User> deletedUser = userRepository.findByCredentialsUsernameAndCredentialsPasswordAndDeletedTrue(user.getCredentials().getUsername(), user.getCredentials().getPassword());

        if (deletedUser.isPresent())
            user = deletedUser.get();
        
        user.setDeleted(false);
        return userRepository.saveAndFlush(user);
    }

    private User getUser (String username) {
        Optional<User> maybeUser = userRepository.findByCredentialsUsernameAndDeletedFalse(username);
        if (maybeUser.isEmpty())
            throw new NotFoundException("username does not match existing user");
        return maybeUser.get();
    }

    private User getUser (Credentials credentials) {
        Optional<User> maybeUser = userRepository.findByCredentialsUsernameAndCredentialsPasswordAndDeletedFalse(credentials.getUsername(), credentials.getPassword());
        if (maybeUser.isEmpty())
            throw new NotAuthorizedException("username or password does not match existing user");
        return maybeUser.get();
    }

    @Override
    public List<UserResponseDto> getAllUsers() {
        return userMapper.entitiesToDtos(userRepository.findAllByDeletedFalse());
    }

    @Override
    public UserResponseDto postUser (UserRequestDto request) {
        User user = userMapper.dtoToEntity(request);
        return userMapper.entityToDto(saveUser(user));
    }

    @Override
    public UserResponseDto getUserByUsername(String username) {
        return userMapper.entityToDto(getUser(username));
    }

    @Override
    public UserResponseDto patchUserProfile (String username, UserRequestDto request) {
        User update = userMapper.dtoToEntity(request);
        Credentials credentials = update.getCredentials();
        Profile newProfile = update.getProfile();
        User user = getUser(credentials);
        user.setProfile(newProfile);
        return userMapper.entityToDto(userRepository.saveAndFlush(user));
    }

    @Override
    public UserResponseDto deleteUser (String username, CredentialsDto request) {
        Credentials credentials = credentialsMapper.dtoToEntity(request);
        User user = getUser(credentials);
        user.setDeleted(true);
        return userMapper.entityToDto(userRepository.saveAndFlush(user));
    }

    @Override
    public void followUser (String username, CredentialsDto request) {
        Credentials credentials = credentialsMapper.dtoToEntity(request);
        User user = getUser(credentials);
        User followedUser = getUser(username);
        if (user.getFollowing().contains(followedUser))
            throw new BadRequestException("Already following "+ followedUser.getCredentials().getUsername());
        user.getFollowing().add(followedUser);
        userRepository.saveAndFlush(user);
    }

    @Override
    public void unfollowUser (String username, CredentialsDto request) {
        Credentials credentials = credentialsMapper.dtoToEntity(request);
        User user = getUser(credentials);
        User followedUser = getUser(username);
        if (!user.getFollowing().contains(followedUser))
            throw new BadRequestException("Not already following "+ followedUser.getCredentials().getUsername());
        user.getFollowing().remove(followedUser);
        userRepository.saveAndFlush(user);
    }

    @Override
    public List<TweetResponseDto> getUserFeed (String username) {
        User user = getUser(username);
        List<User> feedUsers = new LinkedList<>(user.getFollowing());
        feedUsers.add(user);
        List<Tweet> feedTweets = tweetRepository.findAllByAuthorInAndDeletedFalseOrderByPostedDesc(feedUsers);
        return tweetMapper.entitiesToDtos(feedTweets);
    }

    @Override
    public List<TweetResponseDto> getUserTweets (String username) {
        User user = getUser(username);
        List<Tweet> tweets = tweetRepository.findAllByAuthorAndDeletedFalseOrderByPostedDesc(user);
        return tweetMapper.entitiesToDtos(tweets);
    }

    @Override
    public List<TweetResponseDto> getUserMentions (String username) {
        User user = getUser(username);
        List<Tweet> tweets = tweetRepository.findAllByDeletedFalseAndMentionedUsersContainingOrderByPostedDesc(user);
        return tweetMapper.entitiesToDtos(tweets);
    }

    @Override
    public List<UserResponseDto> getUserFollowers (String username) {
        User user = getUser(username);
        List<User> followers = userRepository.findAllByDeletedFalseAndIsIn(user.getFollowers());
        return userMapper.entitiesToDtos(followers);
    }

    @Override
    public List<UserResponseDto> getUserFollowing (String username) {
        User user = getUser(username);
        List<User> followers = userRepository.findAllByDeletedFalseAndIsIn(user.getFollowing());
        return userMapper.entitiesToDtos(followers);
    }
}
