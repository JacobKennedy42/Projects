package com.cooksys.springMock.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.cooksys.springMock.entities.Album;

@Repository
public interface AlbumRepository extends JpaRepository<Album, Long> {

}
