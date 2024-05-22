import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import org.junit.jupiter.api.Test;

import technical.Album;
import technical.Track;

public class AlbumTest {

    @Test
    void constructorWithArtistAndTitleTest() {
        Album album = new Album("Artist1", "Album1");
        assertEquals("Artist1", album.getArtist());
        assertEquals("Album1", album.getAlbumName());
    }

    @Test
    void constructorWithArtistTracksAndTitleTest() {
        Track track1 = new Track("Track1", 2.42);
        Track track2 = new Track("Track2", 3.34);
        Track track3 = new Track("Track3", 1.57);
        Track track4 = new Track("Track4", 2.09);
        List<Track> tracks = new ArrayList<>(Arrays.asList(track1, track2, track3, track4));
        Album album = new Album("Artist1", new ArrayList<>(tracks), "Album1");
        assertEquals(tracks, album.getTracks());
        for (Track t : album.getTracks()) {
            assertEquals("Artist1", t.getArtist());
            assertEquals(album, t.getAlbum());
        }
        assertEquals("Artist1", album.getArtist());
        assertEquals("Album1", album.getAlbumName());
    }

    @Test
    void getArtistTest() {
        Album album = new Album("Artist1", "Album1");
        assertEquals("Artist1", album.getArtist());
    }

    @Test
    void setArtistTest() {
        Album album = new Album("Artist1", "Album1");
        album.setArtist("Artist2");
        assertEquals("Artist2", album.getArtist());
    }

    @Test
    void getTracksTest() {
        Track track1 = new Track("Track1", 2.42);
        Track track2 = new Track("Track2", 3.34);
        Track track3 = new Track("Track3", 1.57);
        Track track4 = new Track("Track4", 2.09);
        List<Track> tracks = new ArrayList<>(Arrays.asList(track1, track2, track3, track4));
        Album album = new Album("Artist1", new ArrayList<>(tracks), "Album1");
        assertEquals(tracks, album.getTracks());
    }

    @Test
    void getTracksDefensiveCopyTest() {
        Track track1 = new Track("Track1", 2.42);
        Track track2 = new Track("Track2", 3.34);
        Track track3 = new Track("Track3", 1.57);
        Track track4 = new Track("Track4", 2.09);
        List<Track> tracks = new ArrayList<>(Arrays.asList(track1, track2, track3, track4));
        Album album = new Album("Artist1", new ArrayList<>(tracks), "Album1");
        List<Track> modifyList = album.getTracks();
        modifyList.remove(track1);
        modifyList.remove(track2);
        assertNotEquals(tracks, modifyList);
        assertEquals(tracks, album.getTracks());
    }

    @Test
    void getTitleTest() {
        Album album = new Album("Artist1", "Album1");
        assertEquals("Album1", album.getAlbumName());
    }

    @Test
    void setTitleTest() {
        Album album = new Album("Artist1", "Album1");
        album.setAlbumName("Album2");
        assertEquals("Album2", album.getAlbumName());
    }

    @Test
    void getAlbumDurationTest() {
        Track track1 = new Track("Track1", 2.42);
        Track track2 = new Track("Track2", 3.34);
        Track track3 = new Track("Track3", 1.57);
        Track track4 = new Track("Track4", 2.09);
        List<Track> tracks = new ArrayList<>(Arrays.asList(track1, track2, track3, track4));
        Album album = new Album("Artist1", new ArrayList<>(tracks), "Album1");
        assertEquals(9.42, album.getAlbumDuration());
    }

    @Test
    void addTrackTest() {
        Track track1 = new Track("Track1", 2.42);
        Track track2 = new Track("Track2", 3.34);
        Track track3 = new Track("Track3", 1.57);
        Track track4 = new Track("Track4", 2.09);
        Track track5 = new Track("Track5", 2.48);
        List<Track> tracks = new ArrayList<>(Arrays.asList(track1, track2, track3, track4));
        Album album = new Album("Artist1", new ArrayList<>(tracks), "Album1");
        album.addTrack(track5);
        tracks.add(track5);
        assertEquals(tracks, album.getTracks());
        for (Track t : album.getTracks()) {
            assertEquals("Artist1", t.getArtist());
            assertEquals(album, t.getAlbum());
        }
        assertEquals(11.9, album.getAlbumDuration());
    }

    @Test
    void removeTrackTest() {
        Track track1 = new Track("Track1", 2.42);
        Track track2 = new Track("Track2", 3.34);
        Track track3 = new Track("Track3", 1.57);
        Track track4 = new Track("Track4", 2.09);
        List<Track> tracks = new ArrayList<>(Arrays.asList(track1, track2, track3, track4));
        Album album = new Album("Artist1", new ArrayList<>(tracks), "Album1");
        album.removeTrack(track4);
        tracks.remove(track4);
        assertEquals(tracks, album.getTracks());
        for (Track t : album.getTracks()) {
            assertEquals("Artist1", t.getArtist());
            assertEquals(album, t.getAlbum());
        }
    }

    @Test
    void sortByTest() {
        Track track1 = new Track("Track1", 2.42);
        Track track2 = new Track("Track2", 3.34);
        Track track3 = new Track("Track3", 1.57);
        Track track4 = new Track("Track4", 2.09);
        List<Track> tracks = new ArrayList<>(Arrays.asList(track1, track2, track3, track4));
        Album album = new Album("Artist1", new ArrayList<>(tracks), "Album1");
        assertEquals(tracks, album.sortBy(null));
        tracks.sort(Comparator.comparing(Track::getDuration));
        assertEquals(tracks, album.sortBy("duration"));
        tracks.sort(Comparator.comparing(Track::getTrackTitle));
        assertEquals(tracks, album.sortBy("title"));
    }

}
