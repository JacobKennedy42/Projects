package technical;

import java.util.LinkedList;
import java.util.List;

import technical.exceptions.MissingImplementationException;

public class Album {

    // Add required fields here (Make sure they are private)
    private String artist;
    private String albumName;
    private List<Track> tracks;

    /**
     * Constructs an Album and assigns values to the artist and albumName fields
     *
     * @param artist      the artist to assign to the artist field
     * @param albumName   the name to assign to the albumName field
     */
    public Album(String artist, String albumName) {
        this.artist = artist;
        this.albumName = albumName;
    }

    /**
     * Constructs an Album and assigns values to the artist, tracks, and albumName fields.
     * This constructor should also set each track's artist to match this album's 
     * artist and set this album to be each track's album.
     *
     * @param artist      the artist to assign to the artist field
     * @param tracks      the tracks to assign to the tracks field
     * @param albumName   the name to assign to the albumName field
     */
    public Album(String artist, List<Track> tracks, String albumName) {
        setArtist(artist);
        setAlbumName(albumName);
        setTracks(tracks);
    }

    /**
     * Getter method for the name of an album
     *
     * @return the name of the album
     */
    public String getAlbumName() {
        return albumName;
    }

    /**
     * Setter method for the name of the album
     *
     * @param albumName the new name of the album
     */
    public void setAlbumName(String albumName) {
        this.albumName = albumName;
    }

    /**
     * Getter method for the artist of an album.
     *
     * @return the artist of the album
     */
    public String getArtist() {
        return artist;
    }

    /**
     * Setter method for the artist of the album.
     *
     * @param artist the new artist of the album
     */
    public void setArtist(String artist) {
        this.artist = artist;
    }

    /**
     * Getter method for the tracks on an album.
     * 
     * IMPORTANT: This should return a defensive copy.
     *
     * @return a list of tracks on the album
     */
    public List<Track> getTracks() {
        return new LinkedList<>(tracks);
    }

    /**
     * Calculate the total duration of the album by summing the duration of each track on this
     * album.
     *
     * @return the sum of each individual track's length on this album
     */
    public double getAlbumDuration() {
        double sum = 0;
        for (Track track : tracks)
            sum += track.getDuration();
        return sum;
    }

    /**
     * This method should add the provided track to this album's tracks list. It
     * should not add null values to the list. It should also set the track's
     * artist to match this album's artist and set this album to be the track's
     * album. If the addition was successful, return true, otherwise, return false.
     *
     * @param track the track to add to the tracks list
     * @return true if the track was added. false otherwise.
     */
    public boolean addTrack(Track track) {
        if (track == null)
            return false;

        track.setArtist(artist);
        track.setAlbum(this);
        return tracks.add(track);
    }

    private void setTracks (List<Track> tracks) {
        this.tracks = new LinkedList<>();
        for (Track track : tracks)
            addTrack(track);
    }

    /**
     * This method should remove the provided track from this album. It should also
     * set the track's artist and album to null if the provided track was in the
     * tracks list. If the removal was successful, return true, otherwise, return false.
     *
     * @param track the track to remove from the tracks list
     * @return true if the track was removed. false otherwise.
     */
    public boolean removeTrack(Track track) {
        if (!tracks.remove(track))
            return false;

        track.setArtist(null);
        track.setAlbum(null);
        return true;
    }

    /**
     * BONUS: This method should directly sort the tracks list based on the rule string
     * passed in:
     * If rule is "duration" then directly sort the tracks in ascending order by their duration
     * (in minutes)
     * If rule is "title" then directly sort the tracks in alphabetical order based on their
     * trackTitle
     * Otherwise don't apply any sort to the tracks list
     *
     * @param rule the rule string to determine the sort
     * @return the tracks list after the appropriate sort is applied
     */
    public List<Track> sortBy(String rule) {
        throw new MissingImplementationException();
    }

}
