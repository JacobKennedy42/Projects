package com.cooksys.SpringAssessmentSocialMedia.controllers;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cooksys.SpringAssessmentSocialMedia.dtos.ContextDto;
import com.cooksys.SpringAssessmentSocialMedia.dtos.CredentialsDto;
import com.cooksys.SpringAssessmentSocialMedia.dtos.HashtagDto;
import com.cooksys.SpringAssessmentSocialMedia.dtos.TweetRequestDto;
import com.cooksys.SpringAssessmentSocialMedia.dtos.TweetResponseDto;
import com.cooksys.SpringAssessmentSocialMedia.dtos.UserResponseDto;
import com.cooksys.SpringAssessmentSocialMedia.exceptions.NotFoundException;
import com.cooksys.SpringAssessmentSocialMedia.services.TweetService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/tweets")
public class TweetController {

    private final TweetService tweetService;

    @GetMapping
    public List<TweetResponseDto> getAllTweets () {
        return tweetService.getAllTweets();
    }

    @PostMapping
    public TweetResponseDto postTweet (@RequestBody TweetRequestDto request) {
        return tweetService.postTweet(request);
    }

    @GetMapping("/{id}")
    public TweetResponseDto getTweetsId (@PathVariable Long id) {
        TweetResponseDto tweet = tweetService.getTweetById(id);
        if (tweet == null) {
            throw new NotFoundException("Tweet not found or deleted");
        }
        return tweet;
    }

    @GetMapping("/{id}/context")
    public ContextDto getTweetContextById(@PathVariable Long id) {
        ContextDto context = tweetService.getTweetContextById(id);
        if (context == null) {
            throw new NotFoundException("Tweet context not found or deleted");
        }
        return context;
    }

    @DeleteMapping("/{id}")
    public TweetResponseDto deleteTweet(@PathVariable Long id, @RequestBody CredentialsDto credentials) {
        return tweetService.deleteTweet(id, credentials);
    }

    @PostMapping("/{id}/reply")
    public TweetResponseDto replyToTweet(@PathVariable("id") Long id, @RequestBody TweetRequestDto tweetReply ) {
        return tweetService.replyToTweet(id, tweetReply);
    }

    @GetMapping("/{id}/replies")
    public List<TweetResponseDto> getRepliesToTweet(@PathVariable("id") Long id) {
        return tweetService.getRepliesToTweet(id);
    }

    @PostMapping("/{id}/like")
    public ResponseEntity<Void> likeTweet(@PathVariable("id") Long id, @RequestBody CredentialsDto credentials) {
        tweetService.likeTweet(id, credentials);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/likes")
    public List<UserResponseDto> getLikesOnTweet(@PathVariable("id") Long id) {
        return tweetService.getLikesOnTweet(id);
    }

    @PostMapping("/{id}/repost")
    public TweetResponseDto repostTweet(@PathVariable("id") Long id, @RequestBody CredentialsDto credentials) {
        return tweetService.repostTweet(id, credentials);
    }

    @GetMapping("/{id}/reposts")
    public List<TweetResponseDto> getRepostsOnTweet(@PathVariable("id") Long id) {
        return tweetService.getRepostsOnTweet(id);
    }

    @GetMapping("/{id}/mentions")
    public List<UserResponseDto> getMentionsInTweet(@PathVariable("id") Long id) {
        return tweetService.getMentionsInTweet(id);
    }

    @GetMapping("/{id}/tags")
    public List<HashtagDto> getTagsOnTweet(@PathVariable("id") Long id) {
        return tweetService.getTagsOnTweet(id);
    }
}
