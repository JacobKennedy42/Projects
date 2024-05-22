package com.cooksys.SpringAssessmentSocialMedia.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.cooksys.SpringAssessmentSocialMedia.entities.Tweet;
import com.cooksys.SpringAssessmentSocialMedia.entities.User;

@Repository
public interface TweetRepository extends JpaRepository<Tweet, Long> {
    Optional<Tweet> findByIdAndDeletedFalse(Long id);
    List<Tweet> findAllByDeletedFalseOrderByPostedDesc();
    List<Tweet> findAllByDeletedFalseAndMentionedUsersContainingOrderByPostedDesc(User user);   //TODO: this is probably incorrect, containing only works on strings i think, might need to use @Query
    List<Tweet> findAllByAuthorAndDeletedFalseOrderByPostedDesc(User user);
    List<Tweet> findAllByAuthorInAndDeletedFalseOrderByPostedDesc(List<User> user);

    List<Tweet> findByInReplyToId(Long id);
    List<Tweet> findByInReplyTo(Tweet tweet);
    List<Tweet> findAllByContentContaining(String s);
}
