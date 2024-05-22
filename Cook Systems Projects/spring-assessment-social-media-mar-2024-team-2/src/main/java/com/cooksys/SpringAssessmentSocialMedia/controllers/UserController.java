package com.cooksys.SpringAssessmentSocialMedia.controllers;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cooksys.SpringAssessmentSocialMedia.dtos.CredentialsDto;
import com.cooksys.SpringAssessmentSocialMedia.dtos.TweetResponseDto;
import com.cooksys.SpringAssessmentSocialMedia.dtos.UserRequestDto;
import com.cooksys.SpringAssessmentSocialMedia.dtos.UserResponseDto;
import com.cooksys.SpringAssessmentSocialMedia.services.UserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    @GetMapping
    public List<UserResponseDto> getAllUsers() {
        return userService.getAllUsers();
    }

    @PostMapping
    public UserResponseDto postUser (@RequestBody UserRequestDto request) {
        return userService.postUser(request);
    }

    @GetMapping ("/@{username}")
    public UserResponseDto getUserByUsername (@PathVariable String username) {
        return userService.getUserByUsername(username);
    }

    @PatchMapping("/@{username}")
    public UserResponseDto patchUserProfile (@PathVariable String username, @RequestBody UserRequestDto request) {
        return userService.patchUserProfile(username, request);
    }

    @DeleteMapping("/@{username}")
    public UserResponseDto deleteUser (@PathVariable String username, @RequestBody CredentialsDto request) {
        return userService.deleteUser(username, request);
    }

    @PostMapping("/@{username}/follow")
    public void followUser (@PathVariable String username, @RequestBody CredentialsDto request) {
        userService.followUser(username, request);
    }

    @PostMapping("/@{username}/unfollow")
    public void unfollowUser (@PathVariable String username, @RequestBody CredentialsDto request) {
        userService.unfollowUser(username, request);
    }

    @GetMapping("/@{username}/feed")
    public List<TweetResponseDto> getUserFeed (@PathVariable String username) {
        return userService.getUserFeed(username);
    }

    @GetMapping ("/@{username}/tweets")
    public List<TweetResponseDto> getUserTweets (@PathVariable String username) {
        return userService.getUserTweets(username);
    }

    @GetMapping("/@{username}/mentions")
    public List<TweetResponseDto> getUserMentions (@PathVariable String username) {
        return userService.getUserMentions(username);
    }

    @GetMapping("/@{username}/followers")
    public List<UserResponseDto> getUserFollowers (@PathVariable String username) {
        return userService.getUserFollowers(username);
    }

    @GetMapping("/@{username}/following")
    public List<UserResponseDto> getUserFollowing (@PathVariable String username) {
        return userService.getUserFollowing(username);
    }

}
