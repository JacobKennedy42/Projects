package com.cooksys.SpringAssessmentSocialMedia.entities;

import java.sql.Timestamp;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@NoArgsConstructor
@Data
public class Tweet {
    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User author;

    @CreationTimestamp
    @Column(updatable = false)
    @Setter(AccessLevel.NONE)
    private Timestamp posted;

    @Column(nullable = false)
    private boolean deleted = false;

    private String content;

    @ManyToOne
    @JoinColumn(name = "reply_id")
    private Tweet inReplyTo;

    @OneToMany (mappedBy = "inReplyTo")
    private List<Tweet> replies;

    @ManyToOne
    @JoinColumn(name = "repost_id")
    private Tweet repostOf;

    @OneToMany (mappedBy = "repostOf")
    private List<Tweet> reposts;

    @ManyToMany (mappedBy = "likedTweets")
    private List<User> likedBy;

    @ManyToMany
    @JoinTable(
        name = "user_mentions",
        joinColumns = @JoinColumn(name ="user_id"),
        inverseJoinColumns = @JoinColumn(name = "tweet_id")
    )
    private List<User> mentionedUsers;

    @ManyToMany
    @JoinTable(
        name = "tweet_hashtags",
        joinColumns = @JoinColumn(name ="tweet_id"),
        inverseJoinColumns = @JoinColumn(name = "hashtag_id")
    )
    private List<Hashtag> hashtags;
}
