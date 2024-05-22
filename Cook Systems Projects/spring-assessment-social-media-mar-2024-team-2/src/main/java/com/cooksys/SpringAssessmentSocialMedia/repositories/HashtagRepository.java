package com.cooksys.SpringAssessmentSocialMedia.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.cooksys.SpringAssessmentSocialMedia.entities.Hashtag;

@Repository
public interface HashtagRepository extends JpaRepository<Hashtag, Long> {
    Optional<Hashtag> findById(Long id);
    List<Hashtag> findAll();

    Optional<Hashtag> findByLabel(String label);
}
