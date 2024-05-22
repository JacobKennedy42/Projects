package com.cooksys.SpringAssessmentSocialMedia.dtos;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.util.List;

@NoArgsConstructor
@Data
public class TweetResponseDto {

    private Long id;

    private UserResponseDto author;

    private String content;

    private List<HashtagDto> hashtags;

    private Timestamp posted;

    private List<UserResponseDto> mentionedUsers;

    private TweetResponseDto repostOf;
}
