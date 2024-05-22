package com.cooksys.SpringAssessmentSocialMedia.services.impl;

import com.cooksys.SpringAssessmentSocialMedia.dtos.HashtagDto;
import com.cooksys.SpringAssessmentSocialMedia.dtos.TweetResponseDto;
import com.cooksys.SpringAssessmentSocialMedia.entities.Hashtag;
import com.cooksys.SpringAssessmentSocialMedia.entities.Tweet;
import com.cooksys.SpringAssessmentSocialMedia.mappers.HashtagMapper;
import com.cooksys.SpringAssessmentSocialMedia.mappers.TweetMapper;
import com.cooksys.SpringAssessmentSocialMedia.mappers.UserMapper;
import com.cooksys.SpringAssessmentSocialMedia.repositories.HashtagRepository;
import com.cooksys.SpringAssessmentSocialMedia.repositories.TweetRepository;
import com.cooksys.SpringAssessmentSocialMedia.services.HashtagService;
import lombok.RequiredArgsConstructor;
import org.antlr.v4.runtime.tree.pattern.ParseTreePattern;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HashtagServiceImpl implements HashtagService {

    private final HashtagRepository hashtagRepository;
    private final HashtagMapper hashtagMapper;
    private final TweetRepository tweetRepository;
    private final TweetMapper tweetMapper;
    private final UserMapper userMapper;


    @Override
    public List<HashtagDto> getAllTags() {
        List<Hashtag> hashtags = hashtagRepository.findAll();
        return hashtags.stream()
                .map(hashtag -> {
                    HashtagDto hashtagDto = new HashtagDto();
                    hashtagDto.setLabel(hashtag.getLabel().replace("#", ""));
                    return hashtagDto;
                })
                .collect(Collectors.toList());
    }

    private TweetResponseDto mapToDto(Tweet tweet) {
        TweetResponseDto tweetDto = new TweetResponseDto();
        tweetDto.setId(tweet.getId());
        tweetDto.setAuthor(userMapper.entityToDto(tweet.getAuthor()));

        // Map each hashtag to its DTO representation
        List<HashtagDto> hashtagDtos = tweet.getHashtags().stream()
                .map(hashtagMapper::entityToDto)
                .collect(Collectors.toList());
        tweetDto.setHashtags(hashtagDtos);

        tweetDto.setPosted(tweet.getPosted());
        tweetDto.setMentionedUsers(userMapper.entitiesToDtos(tweet.getMentionedUsers()));

        tweetDto.setContent(tweet.getContent());
        return tweetDto;
    }

    @Override
    public List<TweetResponseDto> getTweetsByHashtagLabel(String label) {
        List<Tweet> tweets = tweetRepository.findAllByContentContaining("#" + label);
        return tweets.stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }
}
