package com.cooksys.SpringAssessmentSocialMedia.entities;

import java.sql.Timestamp;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@NoArgsConstructor
@Data
@Table(name="users")
public class User {
    @Id
    @GeneratedValue
    private Long id;
    
    @CreationTimestamp
    @Column(updatable = false)
    @Setter(AccessLevel.NONE)
    private Timestamp joined;

    @Embedded
    @Column(nullable = false)
    private Profile profile;

    @Embedded
    @Column(nullable = false)
    private Credentials credentials;

    @OneToMany(mappedBy = "author")
    private List<Tweet> tweets;

    @ManyToMany
    @JoinTable(
        name = "followers_following",
        joinColumns = @JoinColumn(name ="following_id"),
        inverseJoinColumns = @JoinColumn(name = "follower_id")
    )
    private List<User> following;

    @ManyToMany (mappedBy = "following")
    private List<User> followers;

    @Column(nullable = false)
    private boolean deleted = false;

    @ManyToMany
    @JoinTable(
        name = "user_likes",
        joinColumns = @JoinColumn(name ="user_id"),
        inverseJoinColumns = @JoinColumn(name = "tweet_id")
    )
    private List<Tweet> likedTweets;

    @ManyToMany (mappedBy = "mentionedUsers")
    private List<Tweet> mentionedTweets;

}
