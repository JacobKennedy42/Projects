package com.cooksys.springMock.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.cooksys.springMock.entities.Track;

@Repository
public interface TrackRepository extends JpaRepository<Track, Long> {

}
