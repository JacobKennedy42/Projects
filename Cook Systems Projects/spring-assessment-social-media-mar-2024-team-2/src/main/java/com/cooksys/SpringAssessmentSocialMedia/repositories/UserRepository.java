package com.cooksys.SpringAssessmentSocialMedia.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.cooksys.SpringAssessmentSocialMedia.entities.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long>{
    Optional<User> findByIdAndDeletedFalse(Long id);
    List<User> findAllByDeletedFalse();
    Optional<User> findByCredentialsUsernameAndDeletedFalse(String username);
    Optional<User> findByCredentialsUsernameAndCredentialsPasswordAndDeletedTrue(String username, String password);
    Optional<User> findByCredentialsUsernameAndCredentialsPasswordAndDeletedFalse(String username, String password);
    @Query("select u from User u where u in ?1")
    List<User> findAllByDeletedFalseAndIsIn (List<User> users);
}
