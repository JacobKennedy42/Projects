package com.cooksys.springMock;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.cooksys.springMock.entities.Album;
import com.cooksys.springMock.entities.Track;
import com.cooksys.springMock.repositories.AlbumRepository;
import com.cooksys.springMock.repositories.TrackRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class Seeder implements CommandLineRunner {
	
	private final AlbumRepository albumRepository;
	private final TrackRepository trackRepository;
	
	@Override
	public void run(String... args) throws Exception {
		
		Album album1 = new Album();
		album1.setName("Details");
		album1.setArtist("Frou Frou");
		
		albumRepository.saveAndFlush(album1);
		
		Track track1 = new Track();
		track1.setName("Let Go");
		track1.setArtist("Frou Frou");
		track1.setLength(4.14);
		
		Track track2 = new Track();
		track2.setName("Breathe In");
		track2.setArtist("Frou Frou");
		track2.setLength(4.37);
				
		Track track3 = new Track();
		track3.setName("It's Good To Be In Love");
		track3.setArtist("Frou Frou");
		track3.setLength(4.39);
		
		track1.setAlbum(album1);
		track2.setAlbum(album1);
		track3.setAlbum(album1);
		
		trackRepository.saveAndFlush(track1);
		trackRepository.saveAndFlush(track2);
		trackRepository.saveAndFlush(track3);
		
	}

}
