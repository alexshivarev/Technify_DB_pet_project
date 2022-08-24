package technify;

import org.junit.Test;
import technify.business.Playlist;
import technify.business.ReturnValue;
import technify.business.Song;
import technify.business.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.function.Function;

import static org.junit.Assert.assertEquals;
import static technify.business.ReturnValue.*;

public class InfoshocTest extends AbstractTest {
    @Test
    public void userAddGetDeleteTest() {
        User user0 = createUser(0, "Rudolf", "Germany", false);
        User userNegative = createUser(-1, "Rudolf", "Germany", true);
        User user1a = createUser(1, "Hansi", "Germany", false);
        User user1b = createUser(1, "Hans", "Germany", true);
        User user2 = createUser(2, "Yngwie", "Switzerland", false);
        //User userNullId = createUser(null, "Yngwie", "Switzerland", false);
        User userNullName = createUser(2, null, "Switzerland", false);
        User userNullCountry = createUser(2, "Yngwie", null, false);
        //User userNullPremium = createUser(2, "Yngwie", "Switzerland", null);
        User userInjectionName = createUser(
                42,
                "Rudolf', 'Germany', 'FALSE'); DROP TABLE users CASCADE; --",
                "Germany",
                false
        );
        User userInjectionCountry = createUser(
                44,
                "Rudolf",
                "'Germany', 'FALSE'); DROP TABLE users CASCADE; --",
                true
        );

        assertEquals(BAD_PARAMS, Solution.addUser(user0));
        assertEquals(BAD_PARAMS, Solution.addUser(user0));
        assertEquals(BAD_PARAMS, Solution.addUser(userNegative));
        assertEquals(BAD_PARAMS, Solution.addUser(userNegative));
//        assertEquals(BAD_PARAMS, Solution.addUser(userNullId));
//        assertEquals(BAD_PARAMS, Solution.addUser(userNullId));
        assertEquals(BAD_PARAMS, Solution.addUser(userNullName));
        assertEquals(BAD_PARAMS, Solution.addUser(userNullCountry));
        assertEquals(BAD_PARAMS, Solution.addUser(userNullCountry));
//        assertEquals(BAD_PARAMS, Solution.addUser(userNullPremium));
//        assertEquals(BAD_PARAMS, Solution.addUser(userNullPremium));

        assertOK(Solution.addUser(userInjectionName));
        assertEquals(userInjectionName, Solution.getUserProfile(userInjectionName.getId()));
        assertOK(Solution.addUser(userInjectionCountry));
        assertEquals(userInjectionCountry, Solution.getUserProfile(userInjectionCountry.getId()));

        assertOK(Solution.addUser(user1a));
        assertEquals(ALREADY_EXISTS, Solution.addUser(user1a));
        assertEquals(ALREADY_EXISTS, Solution.addUser(user1b));
        assertOK(Solution.addUser(user2));
        assertEquals(ALREADY_EXISTS, Solution.addUser(user1a));
        assertEquals(ALREADY_EXISTS, Solution.addUser(user1b));

        assertEquals(user1a, Solution.getUserProfile(user1a.getId()));
        assertEquals(user2, Solution.getUserProfile(user2.getId()));
        assertEquals(User.badUser(), Solution.getUserProfile(userNegative.getId()));
        assertEquals(User.badUser(), Solution.getUserProfile(user0.getId()));
        assertEquals(User.badUser(), Solution.getUserProfile(MY_PERSONAL_NULL_INTEGER_WITH_BLACK_JACK_AND_HOOKERS));

        assertEquals(NOT_EXISTS, Solution.deleteUser(user0));
        assertEquals(NOT_EXISTS, Solution.deleteUser(user0));
        assertEquals(NOT_EXISTS, Solution.deleteUser(userNegative));
        assertEquals(NOT_EXISTS, Solution.deleteUser(userNegative));
//        assertEquals(NOT_EXISTS, Solution.deleteUser(userNullId));
//        assertEquals(NOT_EXISTS, Solution.deleteUser(userNullId));
        assertEquals(OK, Solution.deleteUser(userNullName));
        assertOK(Solution.addUser(user2));
        assertEquals(OK, Solution.deleteUser(userNullCountry));
        assertOK(Solution.addUser(user2));
        assertEquals(OK, Solution.deleteUser(userNullCountry));
        assertOK(Solution.addUser(user2));
//        assertEquals(NOT_EXISTS, Solution.deleteUser(userNullPremium));
//        assertEquals(NOT_EXISTS, Solution.deleteUser(userNullPremium));
        assertEquals(OK, Solution.deleteUser(user1b));
        assertOK(Solution.addUser(user1a));
        assertEquals(OK, Solution.deleteUser(user1b));
        assertOK(Solution.addUser(user1a));

        assertOK(Solution.deleteUser(user1a));
        assertEquals(User.badUser(), Solution.getUserProfile(user1a.getId()));
        assertEquals(user2, Solution.getUserProfile(user2.getId()));
        assertEquals(NOT_EXISTS, Solution.deleteUser(user1a));
        assertEquals(NOT_EXISTS, Solution.deleteUser(user1a));
        assertOK(Solution.addUser(user1a));
        assertEquals(user1a, Solution.getUserProfile(user1a.getId()));
        assertEquals(ALREADY_EXISTS, Solution.addUser(user1a));
    }

    @Test
    public void userChangePremiumTest() {
        User user1NotPremium = createUser(1, "Ronnie", "England", false);
        User user1Premium = createUser(1, "Ronnie", "England", true);
        User user2NotPremium = createUser(2, "Ozzy", "England", false);
        User user2Premium = createUser(2, "Ozzy", "England", true);
        User user3 = createUser(3, "Bruce", "England", false);
        User user4 = createUser(4, "Steve", "England", true);

        assertOK(Solution.addUser(user1NotPremium));
        assertEquals(user1NotPremium, Solution.getUserProfile(1));
        assertOK(Solution.addUser(user2Premium));
        assertEquals(user2Premium, Solution.getUserProfile(2));
        assertOK(Solution.addUser(user3));
        assertEquals(user3, Solution.getUserProfile(3));
        assertOK(Solution.addUser(user4));
        assertEquals(user4, Solution.getUserProfile(4));
        assertEquals(ALREADY_EXISTS, Solution.updateUserPremium(2));
        assertEquals(user2Premium, Solution.getUserProfile(2));
        assertEquals(ALREADY_EXISTS, Solution.updateUserNotPremium(1));
        assertEquals(user1NotPremium, Solution.getUserProfile(1));
        assertOK(Solution.updateUserPremium(1));
        assertEquals(user1Premium, Solution.getUserProfile(1));
        assertEquals(user2Premium, Solution.getUserProfile(2));
        assertEquals(user3, Solution.getUserProfile(3));
        assertEquals(user4, Solution.getUserProfile(4));
        assertOK(Solution.updateUserNotPremium(1));
        assertEquals(user1NotPremium, Solution.getUserProfile(1));
        assertEquals(user2Premium, Solution.getUserProfile(2));
        assertEquals(user3, Solution.getUserProfile(3));
        assertEquals(user4, Solution.getUserProfile(4));
        assertOK(Solution.updateUserNotPremium(2));
        assertEquals(user1NotPremium, Solution.getUserProfile(1));
        assertEquals(user2NotPremium, Solution.getUserProfile(2));
        assertEquals(user3, Solution.getUserProfile(3));
        assertEquals(user4, Solution.getUserProfile(4));
        assertEquals(ALREADY_EXISTS, Solution.updateUserNotPremium(2));

        assertEquals(NOT_EXISTS, Solution.updateUserPremium(MY_PERSONAL_NULL_INTEGER_WITH_BLACK_JACK_AND_HOOKERS));
        assertEquals(NOT_EXISTS, Solution.updateUserPremium(-1));
        assertEquals(NOT_EXISTS, Solution.updateUserPremium(0));
        assertEquals(NOT_EXISTS, Solution.updateUserPremium(42));
        assertEquals(NOT_EXISTS, Solution.updateUserNotPremium(MY_PERSONAL_NULL_INTEGER_WITH_BLACK_JACK_AND_HOOKERS));
        assertEquals(NOT_EXISTS, Solution.updateUserNotPremium(-1));
        assertEquals(NOT_EXISTS, Solution.updateUserNotPremium(0));
        assertEquals(NOT_EXISTS, Solution.updateUserNotPremium(42));
    }

    @Test
    public void songAddGetDeleteTest() {
        Song songIdNegative = createSong(-1, "Holy Diver", "Heavy Metal", "England", 0);
        Song songNameNull = createSong(1, null, "Heavy Metal", "England", 0);
        Song songGenreNull = createSong(1, "Holy Diver", null, "England", 0);
        Song song0 = createSong(0, "Holy Diver", "Heavy Metal", "England", 0);
        Song song1 = createSong(1, "Holy Diver", "Heavy Metal", "England", 0);
        Song song1_42 = createSong(1, "Holy Diver", "Heavy Metal", "England", 42);
        Song song2 = createSong(2, "Holy Diver", "Heavy Metal", "England", 0);
//        Song song2Null = createSong(2, "Holy Diver", "Heavy Metal", "England", null);
        Song song3 = createSong(3, "Holy Diver", "Heavy Metal", null, 0);
        Song song4 = createSong(4, "Holy Diver", "Heavy Metal", "England", 0);
        Song song4Negative = createSong(4, "Holy Diver", "Heavy Metal", "England", -1);
        Song songInjectionName = createSong(
                42,
                "Holy Diver', 'Heavy Metal', 'England'); " +
                        "DROP TABLE  songs CASCADE; " +
                        "--",
                "Heavy Metal",
                "England",
                0
        );
        Song songInjectionGenre = createSong(
                43,
                "Holy Diver",
                "Heavy Metal', 'England');" +
                        "DROP TABLE songs CASCADE;" +
                        "--",
                "England",
                0
        );
        Song songInjectionCountry = createSong(
                44,
                "Holy Diver",
                "Heavy Metal",
                "England');" +
                        "DROP TABLE songs CASCADE;" +
                        "--",
                0
        );
        
        assertEquals(BAD_PARAMS, Solution.addSong(songIdNegative));
        assertEquals(Song.badSong(), Solution.getSong(-1));
        assertEquals(NOT_EXISTS, Solution.deleteSong(songIdNegative));
        
        assertEquals(BAD_PARAMS, Solution.addSong(songNameNull));
        assertEquals(Song.badSong(), Solution.getSong(1));
        assertEquals(NOT_EXISTS, Solution.deleteSong(songNameNull));
        
        assertEquals(BAD_PARAMS, Solution.addSong(songGenreNull));
        assertEquals(Song.badSong(), Solution.getSong(1));
        assertEquals(NOT_EXISTS, Solution.deleteSong(songGenreNull));
        
        assertEquals(BAD_PARAMS, Solution.addSong(song0));
        assertEquals(Song.badSong(), Solution.getSong(0));
        assertEquals(NOT_EXISTS, Solution.deleteSong(song0));

        assertOK(Solution.addSong(songInjectionName));
        assertEquals(songInjectionName, Solution.getSong(songInjectionName.getId()));
        assertOK(Solution.addSong(songInjectionGenre));
        assertEquals(songInjectionGenre, Solution.getSong(songInjectionGenre.getId()));
        assertOK(Solution.addSong(songInjectionCountry));
        assertEquals(songInjectionCountry, Solution.getSong(songInjectionCountry.getId()));

        assertEquals(Song.badSong(), Solution.getSong(1));
        assertOK(Solution.addSong(song1_42));
        assertEquals(song1, Solution.getSong(1));
        assertEquals(Song.badSong(), Solution.getSong(2));
        assertOK(Solution.addSong(song2));
        assertEquals(song2, Solution.getSong(2));
        assertEquals(Song.badSong(), Solution.getSong(3));
        assertOK(Solution.addSong(song3));
        assertEquals(song3, Solution.getSong(3));
        assertEquals(Song.badSong(), Solution.getSong(4));
        assertOK(Solution.addSong(song4Negative));
        assertEquals(song4, Solution.getSong(4));
        
        assertEquals(OK, Solution.deleteSong(song4Negative));
        assertOK(Solution.addSong(song4Negative));
        assertEquals(song4, Solution.getSong(4));
        assertOK(Solution.deleteSong(song4));
        assertEquals(Song.badSong(), Solution.getSong(4));
        assertOK(Solution.addSong(song4));
        assertEquals(song4, Solution.getSong(4));
    }
    
    @Test
    public void updateSongNameTest() {
        Song song3 = createSong(3, "Holy Diver", "Heavy Metal", null, 0);
        Song song3a = createSong(3, "Child", "Hard Rock", "Great Britain", 23);
        Song song3b = createSong(3, "Child", "Heavy Metal", null, 0);
        Song song3NullName = createSong(3, null, "Heavy Metal", null, 0);
        Song songIdNegative = createSong(-1, "Holy Diver", "Heavy Metal", "England", 0);
        Song songNameNull = createSong(1, null, "Heavy Metal", "England", 0);
        Song songGenreNull = createSong(1, "Holy Diver", null, "England", 0);
        Song song0 = createSong(0, "Holy Diver", "Heavy Metal", "England", 0);
        Song song1 = createSong(1, "Holy Diver", "Heavy Metal", "England", 0);
        Song songInjection = createSong(3,
                "Child' WHERE id = 3;\n" +
                        "DROP TABLE songs CASCADE;\n" +
                        "--",
                "Heavy Metal",
                null,
                0
        );
        
        assertOK(Solution.addSong(song3));
        assertOK(Solution.updateSongName(song3a));
        assertEquals(song3b, Solution.getSong(3));
        assertEquals(NOT_EXISTS, Solution.updateSongName(songIdNegative));
        assertEquals(NOT_EXISTS, Solution.updateSongName(songNameNull));
        assertEquals(NOT_EXISTS, Solution.updateSongName(songGenreNull));
        assertEquals(NOT_EXISTS, Solution.updateSongName(song0));
        assertEquals(NOT_EXISTS, Solution.updateSongName(song1));
        assertEquals(BAD_PARAMS, Solution.updateSongName(song3NullName));
        assertOK(Solution.updateSongName(songInjection));
        assertEquals(songInjection, Solution.getSong(3));
    }

    @Test
    public void playListAddGetDeleteTest() {
        Playlist playlistNegative = createPlayList(-1, "Heavy", "Maiden");
        Playlist playlist0 = createPlayList(0, "Heavy", "Maiden");
        Playlist playlistNullGenre = createPlayList(1, null, "Maiden");
        Playlist playlistNullDescription = createPlayList(1, "Heavy", null);
        Playlist playlist1 = createPlayList(1, "Heavy", "Maiden");
        Playlist playlist1a = createPlayList(1, "Heavy Metal", "Iron Maiden");
        Playlist playlist2 = createPlayList(2, "Heavy", "Maiden");
        Playlist playlistInjection = createPlayList(
                3,
                "'; DROP TABLE playlists CASCADE; --",
                "'; DROP TABLE playlists CASCADE; --"
        );
        
        assertEquals(BAD_PARAMS, Solution.addPlaylist(playlistNegative));
        assertEquals(BAD_PARAMS, Solution.addPlaylist(playlist0));
        assertEquals(BAD_PARAMS, Solution.addPlaylist(playlistNullGenre));
        assertEquals(BAD_PARAMS, Solution.addPlaylist(playlistNullDescription));

        assertOK(Solution.addPlaylist(playlistInjection));
        assertEquals(playlistInjection, Solution.getPlaylist(playlistInjection.getId()));

        assertOK(Solution.addPlaylist(playlist1));
        assertEquals(playlist1, Solution.getPlaylist(1));
        assertEquals(ALREADY_EXISTS, Solution.addPlaylist(playlist1a));
        assertOK(Solution.addPlaylist(playlist2));
        assertOK(Solution.deletePlaylist(playlist1a));
        assertEquals(Playlist.badPlaylist(), Solution.getPlaylist(1));
        assertEquals(NOT_EXISTS, Solution.deletePlaylist(playlistNullGenre));
        assertEquals(NOT_EXISTS, Solution.deletePlaylist(playlistNullDescription));
        assertEquals(NOT_EXISTS, Solution.deletePlaylist(playlist1));
        assertEquals(NOT_EXISTS, Solution.deletePlaylist(playlistNegative));
        assertEquals(NOT_EXISTS, Solution.deletePlaylist(playlist0));
        assertEquals(Playlist.badPlaylist(), Solution.getPlaylist(-1));
        assertEquals(Playlist.badPlaylist(), Solution.getPlaylist(0));
        assertEquals(Playlist.badPlaylist(), Solution.getPlaylist(MY_PERSONAL_NULL_INTEGER_WITH_BLACK_JACK_AND_HOOKERS));

    }

    @Test
    public void updatePlayListDescriptionTest() {
        Playlist playlistNegative = createPlayList(-1, "Heavy", "Maiden");
        Playlist playlist0 = createPlayList(0, "Heavy", "Maiden");
        Playlist playlist2 = createPlayList(0, "Heavy", null);
        Playlist playlist1 = createPlayList(1, "Heavy", "Maiden");
        Playlist playlist1a = createPlayList(1, "Hard", "Scorpions");
        Playlist playlist1b  = createPlayList(1, "Heavy", "Scorpions");
        Playlist playlist1Null = createPlayList(1, "Heavy", null);
        Playlist playlistInjection = createPlayList(1, "Heavy", "'; DROP TABLE playlist CASCADE; --");

        assertEquals(NOT_EXISTS, Solution.updatePlaylist(playlist0));
        assertOK(Solution.addPlaylist(playlist1));
        assertEquals(NOT_EXISTS, Solution.updatePlaylist(playlistNegative));
        assertEquals(NOT_EXISTS, Solution.updatePlaylist(playlist0));
        assertEquals(NOT_EXISTS, Solution.updatePlaylist(playlist2));
        assertEquals(playlist1, Solution.getPlaylist(1));
        assertOK(Solution.updatePlaylist(playlist1a));
        assertEquals(playlist1b, Solution.getPlaylist(1));
        assertEquals(BAD_PARAMS, Solution.updatePlaylist(playlist1Null));
        assertEquals(playlist1b, Solution.getPlaylist(1));
        assertOK(Solution.updatePlaylist(playlistInjection));
        assertEquals(playlistInjection, Solution.getPlaylist(1));
    }

    @Test
    public void addPlayTest() {
        Song song1_0 = createSong(1, "Eagle fly free", "Heavy metal", "Germany", 0);
        Song song1_42 = createSong(1, "Eagle fly free", "Heavy metal", "Germany", 42);

        assertOK(Solution.addSong(song1_42));
        assertEquals(song1_0, Solution.getSong(1));
        assertEquals(BAD_PARAMS, Solution.songPlay(1, -1));
        assertEquals(NOT_EXISTS, Solution.songPlay(2, 1));
        assertEquals(NOT_EXISTS, Solution.songPlay(2, -1));
        assertEquals(NOT_EXISTS, Solution.songPlay(0, 1));
        assertEquals(NOT_EXISTS, Solution.songPlay(MY_PERSONAL_NULL_INTEGER_WITH_BLACK_JACK_AND_HOOKERS, 1));
        assertEquals(NOT_EXISTS, Solution.songPlay(-1, 1));
        assertOK(Solution.songPlay(1, 0));
        assertEquals(song1_0, Solution.getSong(1));
        assertOK(Solution.songPlay(1, 42));
        assertEquals(song1_42, Solution.getSong(1));
        assertOK(Solution.songPlay(1, -42));
        assertEquals(song1_0, Solution.getSong(1));
        assertOK(Solution.songPlay(1, 42));
        assertEquals(song1_42, Solution.getSong(1));
        assertEquals(BAD_PARAMS, Solution.songPlay(1, -43));
        assertOK(Solution.deleteSong(song1_0));
        assertEquals(Song.badSong(), Solution.getSong(1));
        assertEquals(NOT_EXISTS, Solution.deleteSong(song1_42));
        assertOK(Solution.addSong(song1_42));
        assertEquals(song1_0, Solution.getSong(1));
    }

    @Test
    public void addRemoveSongToFromPlaylistTest() {
        Song song1 = createSong(1, "Shame", "Heavy", "England", 0);
        Song song2 = createSong(2, "On", "Metal", "England", 0);
        Playlist playlist1 = createPlayList(1, "Metal", "Dio");
        Playlist playlist2 = createPlayList(2, "Heavy", "Ronnie");
        Playlist playlist4 = createPlayList(4, "Heavy", "Klaus");

        assertOK(Solution.addSong(song1));
        assertOK(Solution.addSong(song2));
        assertOK(Solution.addPlaylist(playlist1));
        assertOK(Solution.addPlaylist(playlist2));
        assertOK(Solution.addPlaylist(playlist4));
        assertOK(Solution.addSongToPlaylist(1, 2));
        assertOK(Solution.addSongToPlaylist(2, 1));
        assertEquals(BAD_PARAMS, Solution.addSongToPlaylist(1, 1));
        assertEquals(BAD_PARAMS, Solution.addSongToPlaylist(2, 2));
        assertEquals(ALREADY_EXISTS, Solution.addSongToPlaylist(1, 2));
        assertEquals(ALREADY_EXISTS, Solution.addSongToPlaylist(2, 1));
        assertEquals(BAD_PARAMS, Solution.addSongToPlaylist(MY_PERSONAL_NULL_INTEGER_WITH_BLACK_JACK_AND_HOOKERS, MY_PERSONAL_NULL_INTEGER_WITH_BLACK_JACK_AND_HOOKERS));
        assertEquals(BAD_PARAMS, Solution.addSongToPlaylist(MY_PERSONAL_NULL_INTEGER_WITH_BLACK_JACK_AND_HOOKERS, -1));
        assertEquals(BAD_PARAMS, Solution.addSongToPlaylist(MY_PERSONAL_NULL_INTEGER_WITH_BLACK_JACK_AND_HOOKERS, 0));
        assertEquals(BAD_PARAMS, Solution.addSongToPlaylist(MY_PERSONAL_NULL_INTEGER_WITH_BLACK_JACK_AND_HOOKERS, 1));
        assertEquals(BAD_PARAMS, Solution.addSongToPlaylist(MY_PERSONAL_NULL_INTEGER_WITH_BLACK_JACK_AND_HOOKERS, 2));
        assertEquals(BAD_PARAMS, Solution.addSongToPlaylist(MY_PERSONAL_NULL_INTEGER_WITH_BLACK_JACK_AND_HOOKERS, 3));
        assertEquals(BAD_PARAMS, Solution.addSongToPlaylist(-1, MY_PERSONAL_NULL_INTEGER_WITH_BLACK_JACK_AND_HOOKERS));
        assertEquals(BAD_PARAMS, Solution.addSongToPlaylist(-1, -1));
        assertEquals(BAD_PARAMS, Solution.addSongToPlaylist(-1, 0));
        assertEquals(BAD_PARAMS, Solution.addSongToPlaylist(-1, 1));
        assertEquals(BAD_PARAMS, Solution.addSongToPlaylist(-1, 2));
        assertEquals(BAD_PARAMS, Solution.addSongToPlaylist(-1, 3));
        assertEquals(BAD_PARAMS, Solution.addSongToPlaylist(0, MY_PERSONAL_NULL_INTEGER_WITH_BLACK_JACK_AND_HOOKERS));
        assertEquals(BAD_PARAMS, Solution.addSongToPlaylist(0, 0));
        assertEquals(BAD_PARAMS, Solution.addSongToPlaylist(0, 1));
        assertEquals(BAD_PARAMS, Solution.addSongToPlaylist(0, 2));
        assertEquals(BAD_PARAMS, Solution.addSongToPlaylist(0, 3));
        assertEquals(BAD_PARAMS, Solution.addSongToPlaylist(1, MY_PERSONAL_NULL_INTEGER_WITH_BLACK_JACK_AND_HOOKERS));
        assertEquals(BAD_PARAMS, Solution.addSongToPlaylist(1, 0));
        assertEquals(BAD_PARAMS, Solution.addSongToPlaylist(1, 3));
        assertEquals(BAD_PARAMS, Solution.addSongToPlaylist(3, MY_PERSONAL_NULL_INTEGER_WITH_BLACK_JACK_AND_HOOKERS));
        assertEquals(BAD_PARAMS, Solution.addSongToPlaylist(3, -1));
        assertEquals(BAD_PARAMS, Solution.addSongToPlaylist(3, 0));
        assertEquals(BAD_PARAMS, Solution.addSongToPlaylist(3, 1));
        assertEquals(BAD_PARAMS, Solution.addSongToPlaylist(3, 2));
        assertEquals(BAD_PARAMS, Solution.addSongToPlaylist(3, 3));

        assertEquals(NOT_EXISTS, Solution.removeSongFromPlaylist(MY_PERSONAL_NULL_INTEGER_WITH_BLACK_JACK_AND_HOOKERS, MY_PERSONAL_NULL_INTEGER_WITH_BLACK_JACK_AND_HOOKERS));
        assertEquals(NOT_EXISTS, Solution.removeSongFromPlaylist(MY_PERSONAL_NULL_INTEGER_WITH_BLACK_JACK_AND_HOOKERS, -1));
        assertEquals(NOT_EXISTS, Solution.removeSongFromPlaylist(MY_PERSONAL_NULL_INTEGER_WITH_BLACK_JACK_AND_HOOKERS, 0));
        assertEquals(NOT_EXISTS, Solution.removeSongFromPlaylist(MY_PERSONAL_NULL_INTEGER_WITH_BLACK_JACK_AND_HOOKERS, 1));
        assertEquals(NOT_EXISTS, Solution.removeSongFromPlaylist(MY_PERSONAL_NULL_INTEGER_WITH_BLACK_JACK_AND_HOOKERS, 2));
        assertEquals(NOT_EXISTS, Solution.removeSongFromPlaylist(MY_PERSONAL_NULL_INTEGER_WITH_BLACK_JACK_AND_HOOKERS, 3));
        assertEquals(NOT_EXISTS, Solution.removeSongFromPlaylist(-1, MY_PERSONAL_NULL_INTEGER_WITH_BLACK_JACK_AND_HOOKERS));
        assertEquals(NOT_EXISTS, Solution.removeSongFromPlaylist(-1, -1));
        assertEquals(NOT_EXISTS, Solution.removeSongFromPlaylist(-1, 0));
        assertEquals(NOT_EXISTS, Solution.removeSongFromPlaylist(-1, 1));
        assertEquals(NOT_EXISTS, Solution.removeSongFromPlaylist(-1, 2));
        assertEquals(NOT_EXISTS, Solution.removeSongFromPlaylist(-1, 3));
        assertEquals(NOT_EXISTS, Solution.removeSongFromPlaylist(0, MY_PERSONAL_NULL_INTEGER_WITH_BLACK_JACK_AND_HOOKERS));
        assertEquals(NOT_EXISTS, Solution.removeSongFromPlaylist(0, 0));
        assertEquals(NOT_EXISTS, Solution.removeSongFromPlaylist(0, 1));
        assertEquals(NOT_EXISTS, Solution.removeSongFromPlaylist(0, 2));
        assertEquals(NOT_EXISTS, Solution.removeSongFromPlaylist(0, 3));
        assertEquals(NOT_EXISTS, Solution.removeSongFromPlaylist(1, MY_PERSONAL_NULL_INTEGER_WITH_BLACK_JACK_AND_HOOKERS));
        assertEquals(NOT_EXISTS, Solution.removeSongFromPlaylist(1, 0));
        assertEquals(NOT_EXISTS, Solution.removeSongFromPlaylist(1, 3));
        assertEquals(NOT_EXISTS, Solution.removeSongFromPlaylist(3, MY_PERSONAL_NULL_INTEGER_WITH_BLACK_JACK_AND_HOOKERS));
        assertEquals(NOT_EXISTS, Solution.removeSongFromPlaylist(3, -1));
        assertEquals(NOT_EXISTS, Solution.removeSongFromPlaylist(3, 0));
        assertEquals(NOT_EXISTS, Solution.removeSongFromPlaylist(3, 1));
        assertEquals(NOT_EXISTS, Solution.removeSongFromPlaylist(3, 2));
        assertEquals(NOT_EXISTS, Solution.removeSongFromPlaylist(3, 3));

        assertEquals(OK, Solution.removeSongFromPlaylist(2, 1));
        assertEquals(NOT_EXISTS, Solution.removeSongFromPlaylist(2, 1));
        assertEquals(OK, Solution.addSongToPlaylist(2, 1));
        assertEquals(OK, Solution.removeSongFromPlaylist(1, 2));
        assertEquals(NOT_EXISTS, Solution.removeSongFromPlaylist(1, 2));
        assertEquals(OK, Solution.addSongToPlaylist(1, 2));
        assertOK(Solution.deleteSong(song1));
        assertEquals(NOT_EXISTS, Solution.removeSongFromPlaylist(1, 2));
        assertOK(Solution.addSong(song1));
        assertEquals(NOT_EXISTS, Solution.removeSongFromPlaylist(1, 2));
        assertEquals(OK, Solution.addSongToPlaylist(1, 2));
        assertOK(Solution.deletePlaylist(playlist2));
        assertEquals(NOT_EXISTS, Solution.removeSongFromPlaylist(1, 2));
        assertOK(Solution.addPlaylist(playlist2));
        assertEquals(NOT_EXISTS, Solution.removeSongFromPlaylist(1, 2));
        assertEquals(OK, Solution.addSongToPlaylist(1, 2));
        assertEquals(ALREADY_EXISTS, Solution.addSongToPlaylist(1, 2));
        assertEquals(OK, Solution.addSongToPlaylist(1, 4));
        assertEquals(OK, Solution.removeSongFromPlaylist(1, 2));
        assertEquals(OK, Solution.removeSongFromPlaylist(1, 4));
    }

    @Test
    public void followUnfollowGetFollowerCountPlayListTest() {
        User user1 = createUser(1, "Kurt", "USA", false);
        User user2 = createUser(2, "Paul", "USA", true);
        Playlist playlist1 = createPlayList(1, "Grunge", "Nirvana");
        Playlist playlist2 = createPlayList(2, "Glam", "Kiss");

        assertOK(Solution.addUser(user1));
        assertOK(Solution.addUser(user2));
        assertOK(Solution.addPlaylist(playlist1));
        assertOK(Solution.addPlaylist(playlist2));

        assertEquals(NOT_EXISTS, Solution.followPlaylist(MY_PERSONAL_NULL_INTEGER_WITH_BLACK_JACK_AND_HOOKERS, MY_PERSONAL_NULL_INTEGER_WITH_BLACK_JACK_AND_HOOKERS));
        assertEquals(NOT_EXISTS, Solution.followPlaylist(MY_PERSONAL_NULL_INTEGER_WITH_BLACK_JACK_AND_HOOKERS, -1));
        assertEquals(NOT_EXISTS, Solution.followPlaylist(MY_PERSONAL_NULL_INTEGER_WITH_BLACK_JACK_AND_HOOKERS, 0));
        assertEquals(NOT_EXISTS, Solution.followPlaylist(MY_PERSONAL_NULL_INTEGER_WITH_BLACK_JACK_AND_HOOKERS, 1));
        assertEquals(NOT_EXISTS, Solution.followPlaylist(MY_PERSONAL_NULL_INTEGER_WITH_BLACK_JACK_AND_HOOKERS, 3));

        assertEquals(NOT_EXISTS, Solution.followPlaylist(-1, MY_PERSONAL_NULL_INTEGER_WITH_BLACK_JACK_AND_HOOKERS));
        assertEquals(NOT_EXISTS, Solution.followPlaylist(-1, -1));
        assertEquals(NOT_EXISTS, Solution.followPlaylist(-1, 0));
        assertEquals(NOT_EXISTS, Solution.followPlaylist(-1, 1));
        assertEquals(NOT_EXISTS, Solution.followPlaylist(-1, 3));

        assertEquals(NOT_EXISTS, Solution.followPlaylist(0, MY_PERSONAL_NULL_INTEGER_WITH_BLACK_JACK_AND_HOOKERS));
        assertEquals(NOT_EXISTS, Solution.followPlaylist(0, -1));
        assertEquals(NOT_EXISTS, Solution.followPlaylist(0, 0));
        assertEquals(NOT_EXISTS, Solution.followPlaylist(0, 1));
        assertEquals(NOT_EXISTS, Solution.followPlaylist(0, 3));

        assertEquals(NOT_EXISTS, Solution.followPlaylist(1, MY_PERSONAL_NULL_INTEGER_WITH_BLACK_JACK_AND_HOOKERS));
        assertEquals(NOT_EXISTS, Solution.followPlaylist(1, -1));
        assertEquals(NOT_EXISTS, Solution.followPlaylist(1, 0));
        assertEquals(NOT_EXISTS, Solution.followPlaylist(1, 3));

        assertEquals(NOT_EXISTS, Solution.followPlaylist(3, MY_PERSONAL_NULL_INTEGER_WITH_BLACK_JACK_AND_HOOKERS));
        assertEquals(NOT_EXISTS, Solution.followPlaylist(3, -1));
        assertEquals(NOT_EXISTS, Solution.followPlaylist(3, 0));
        assertEquals(NOT_EXISTS, Solution.followPlaylist(3, 1));
        assertEquals(NOT_EXISTS, Solution.followPlaylist(3, 3));


        assertEquals(NOT_EXISTS, Solution.stopFollowPlaylist(MY_PERSONAL_NULL_INTEGER_WITH_BLACK_JACK_AND_HOOKERS, MY_PERSONAL_NULL_INTEGER_WITH_BLACK_JACK_AND_HOOKERS));
        assertEquals(NOT_EXISTS, Solution.stopFollowPlaylist(MY_PERSONAL_NULL_INTEGER_WITH_BLACK_JACK_AND_HOOKERS, -1));
        assertEquals(NOT_EXISTS, Solution.stopFollowPlaylist(MY_PERSONAL_NULL_INTEGER_WITH_BLACK_JACK_AND_HOOKERS, 0));
        assertEquals(NOT_EXISTS, Solution.stopFollowPlaylist(MY_PERSONAL_NULL_INTEGER_WITH_BLACK_JACK_AND_HOOKERS, 1));
        assertEquals(NOT_EXISTS, Solution.stopFollowPlaylist(MY_PERSONAL_NULL_INTEGER_WITH_BLACK_JACK_AND_HOOKERS, 3));

        assertEquals(NOT_EXISTS, Solution.stopFollowPlaylist(-1, MY_PERSONAL_NULL_INTEGER_WITH_BLACK_JACK_AND_HOOKERS));
        assertEquals(NOT_EXISTS, Solution.stopFollowPlaylist(-1, -1));
        assertEquals(NOT_EXISTS, Solution.stopFollowPlaylist(-1, 0));
        assertEquals(NOT_EXISTS, Solution.stopFollowPlaylist(-1, 1));
        assertEquals(NOT_EXISTS, Solution.stopFollowPlaylist(-1, 3));

        assertEquals(NOT_EXISTS, Solution.stopFollowPlaylist(0, MY_PERSONAL_NULL_INTEGER_WITH_BLACK_JACK_AND_HOOKERS));
        assertEquals(NOT_EXISTS, Solution.stopFollowPlaylist(0, -1));
        assertEquals(NOT_EXISTS, Solution.stopFollowPlaylist(0, 0));
        assertEquals(NOT_EXISTS, Solution.stopFollowPlaylist(0, 1));
        assertEquals(NOT_EXISTS, Solution.stopFollowPlaylist(0, 3));

        assertEquals(NOT_EXISTS, Solution.stopFollowPlaylist(1, MY_PERSONAL_NULL_INTEGER_WITH_BLACK_JACK_AND_HOOKERS));
        assertEquals(NOT_EXISTS, Solution.stopFollowPlaylist(1, -1));
        assertEquals(NOT_EXISTS, Solution.stopFollowPlaylist(1, 0));
        assertEquals(NOT_EXISTS, Solution.stopFollowPlaylist(1, 3));

        assertEquals(NOT_EXISTS, Solution.stopFollowPlaylist(3, MY_PERSONAL_NULL_INTEGER_WITH_BLACK_JACK_AND_HOOKERS));
        assertEquals(NOT_EXISTS, Solution.stopFollowPlaylist(3, -1));
        assertEquals(NOT_EXISTS, Solution.stopFollowPlaylist(3, 0));
        assertEquals(NOT_EXISTS, Solution.stopFollowPlaylist(3, 1));
        assertEquals(NOT_EXISTS, Solution.stopFollowPlaylist(3, 3));

        assertOK(Solution.followPlaylist(1, 1));
        assertEquals(Integer.valueOf(1), Solution.getPlaylistFollowersCount(1));
        assertEquals(Integer.valueOf(0), Solution.getPlaylistFollowersCount(2));
        assertOK(Solution.followPlaylist(1, 2));
        assertEquals(Integer.valueOf(1), Solution.getPlaylistFollowersCount(1));
        assertEquals(Integer.valueOf(1), Solution.getPlaylistFollowersCount(2));
        assertEquals(ALREADY_EXISTS, Solution.followPlaylist(1, 1));
        assertEquals(Integer.valueOf(1), Solution.getPlaylistFollowersCount(1));
        assertEquals(Integer.valueOf(1), Solution.getPlaylistFollowersCount(2));
        assertEquals(ALREADY_EXISTS, Solution.followPlaylist(1, 2));
        assertEquals(Integer.valueOf(1), Solution.getPlaylistFollowersCount(1));
        assertEquals(Integer.valueOf(1), Solution.getPlaylistFollowersCount(2));
        assertEquals(OK, Solution.stopFollowPlaylist(1, 1));
        assertEquals(Integer.valueOf(0), Solution.getPlaylistFollowersCount(1));
        assertEquals(Integer.valueOf(1), Solution.getPlaylistFollowersCount(2));
        assertEquals(OK, Solution.stopFollowPlaylist(1, 2));
        assertEquals(Integer.valueOf(0), Solution.getPlaylistFollowersCount(1));
        assertEquals(Integer.valueOf(0), Solution.getPlaylistFollowersCount(2));
        assertEquals(NOT_EXISTS, Solution.stopFollowPlaylist(1, 1));
        assertEquals(Integer.valueOf(0), Solution.getPlaylistFollowersCount(1));
        assertEquals(Integer.valueOf(0), Solution.getPlaylistFollowersCount(2));
        assertEquals(NOT_EXISTS, Solution.stopFollowPlaylist(1, 2));
        assertEquals(Integer.valueOf(0), Solution.getPlaylistFollowersCount(1));
        assertEquals(Integer.valueOf(0), Solution.getPlaylistFollowersCount(2));
        assertOK(Solution.followPlaylist(1, 1));
        assertEquals(Integer.valueOf(1), Solution.getPlaylistFollowersCount(1));
        assertEquals(Integer.valueOf(0), Solution.getPlaylistFollowersCount(2));
        assertOK(Solution.followPlaylist(1, 2));
        assertEquals(Integer.valueOf(1), Solution.getPlaylistFollowersCount(1));
        assertEquals(Integer.valueOf(1), Solution.getPlaylistFollowersCount(2));

        assertEquals(Integer.valueOf(0), Solution.getPlaylistTotalPlayCount(MY_PERSONAL_NULL_INTEGER_WITH_BLACK_JACK_AND_HOOKERS));
        assertEquals(Integer.valueOf(0), Solution.getPlaylistTotalPlayCount(-1));
        assertEquals(Integer.valueOf(0), Solution.getPlaylistTotalPlayCount(0));
        assertEquals(Integer.valueOf(0), Solution.getPlaylistTotalPlayCount(42));
    }

    @Test
    public void getPlayListTotalPlayCountTest() {
        Playlist playlist1 = createPlayList(1, "Power", "Helloween");
        Playlist playlist2 = createPlayList(2, "Power", "Helloween");
        Song song1 = createSong(1, "Heaven", "Power", "Germany", 0);
        Song song2 = createSong(2, "Tells", "Power", "Germany", 0);
        Song song3 = createSong(3, "No", "Power", "Germany", 0);
        Song song4 = createSong(4, "Lies", "Power", "Germany", 0);
        Song song5 = createSong(5, "Hold", "Hard", "Germany", 0);

        assertOK(Solution.addPlaylist(playlist1));
        assertOK(Solution.addPlaylist(playlist2));
        assertOK(Solution.addSong(song1));
        assertOK(Solution.addSong(song2));
        assertOK(Solution.addSong(song3));
        assertOK(Solution.addSong(song4));
        assertOK(Solution.addSong(song5));

        assertEquals(Integer.valueOf(0), Solution.getPlaylistTotalPlayCount(MY_PERSONAL_NULL_INTEGER_WITH_BLACK_JACK_AND_HOOKERS));
        assertEquals(Integer.valueOf(0), Solution.getPlaylistTotalPlayCount(-1));
        assertEquals(Integer.valueOf(0), Solution.getPlaylistTotalPlayCount(0));
        assertEquals(Integer.valueOf(0), Solution.getPlaylistTotalPlayCount(3));

        assertOK(Solution.songPlay(1, 1));
        assertEquals(Integer.valueOf(0), Solution.getPlaylistTotalPlayCount(1));
        assertEquals(Integer.valueOf(0), Solution.getPlaylistTotalPlayCount(2));
        assertOK(Solution.songPlay(2, 2));
        assertEquals(Integer.valueOf(0), Solution.getPlaylistTotalPlayCount(1));
        assertEquals(Integer.valueOf(0), Solution.getPlaylistTotalPlayCount(2));
        assertOK(Solution.songPlay(3, 4));
        assertEquals(Integer.valueOf(0), Solution.getPlaylistTotalPlayCount(1));
        assertEquals(Integer.valueOf(0), Solution.getPlaylistTotalPlayCount(2));
        assertOK(Solution.songPlay(4, 8));
        assertEquals(Integer.valueOf(0), Solution.getPlaylistTotalPlayCount(1));
        assertEquals(Integer.valueOf(0), Solution.getPlaylistTotalPlayCount(2));

        assertOK(Solution.songPlay(5, 42));
        assertEquals(BAD_PARAMS, Solution.addSongToPlaylist(5, 1));

        assertOK(Solution.addSongToPlaylist(1, 1));
        assertEquals(Integer.valueOf(1), Solution.getPlaylistTotalPlayCount(1));
        assertEquals(Integer.valueOf(0), Solution.getPlaylistTotalPlayCount(2));
        assertOK(Solution.addSongToPlaylist(2, 1));
        assertEquals(Integer.valueOf(3), Solution.getPlaylistTotalPlayCount(1));
        assertEquals(Integer.valueOf(0), Solution.getPlaylistTotalPlayCount(2));
        assertEquals(ALREADY_EXISTS, Solution.addSongToPlaylist(2, 1));
        assertEquals(Integer.valueOf(3), Solution.getPlaylistTotalPlayCount(1));
        assertEquals(Integer.valueOf(0), Solution.getPlaylistTotalPlayCount(2));
        assertOK(Solution.addSongToPlaylist(3, 1));
        assertEquals(Integer.valueOf(7), Solution.getPlaylistTotalPlayCount(1));
        assertEquals(Integer.valueOf(0), Solution.getPlaylistTotalPlayCount(2));
        assertOK(Solution.songPlay(1, 1));
        assertEquals(Integer.valueOf(8), Solution.getPlaylistTotalPlayCount(1));
        assertEquals(Integer.valueOf(0), Solution.getPlaylistTotalPlayCount(2));
        assertEquals(BAD_PARAMS, Solution.songPlay(1, -3));
        assertEquals(Integer.valueOf(8), Solution.getPlaylistTotalPlayCount(1));
        assertEquals(Integer.valueOf(0), Solution.getPlaylistTotalPlayCount(2));
        assertOK(Solution.songPlay(1, -2));
        assertEquals(Integer.valueOf(6), Solution.getPlaylistTotalPlayCount(1));
        assertEquals(Integer.valueOf(0), Solution.getPlaylistTotalPlayCount(2));
        assertOK(Solution.songPlay(1, 1));
        assertEquals(Integer.valueOf(7), Solution.getPlaylistTotalPlayCount(1));
        assertEquals(Integer.valueOf(0), Solution.getPlaylistTotalPlayCount(2));
        assertOK(Solution.removeSongFromPlaylist(1, 1));
        assertEquals(Integer.valueOf(6), Solution.getPlaylistTotalPlayCount(1));
        assertEquals(Integer.valueOf(0), Solution.getPlaylistTotalPlayCount(2));
        assertOK(Solution.deleteSong(song2));
        assertEquals(Integer.valueOf(4), Solution.getPlaylistTotalPlayCount(1));
        assertEquals(Integer.valueOf(0), Solution.getPlaylistTotalPlayCount(2));
        assertOK(Solution.deletePlaylist(playlist1));
        assertEquals(Integer.valueOf(0), Solution.getPlaylistTotalPlayCount(1));
        assertEquals(Integer.valueOf(0), Solution.getPlaylistTotalPlayCount(2));
        assertOK(Solution.addPlaylist(playlist1));
        assertEquals(Integer.valueOf(0), Solution.getPlaylistTotalPlayCount(1));
        assertEquals(Integer.valueOf(0), Solution.getPlaylistTotalPlayCount(2));
    }

    @Test
    public void getMostPopularSongTest() {
        Playlist playlist1 = createPlayList(1, "Power", "Blind");
        Playlist playlist2 = createPlayList(2, "Heavy", "Iron");
        Playlist playlist3 = createPlayList(3, "Power", "Blind");
        Playlist playlist4 = createPlayList(4, "Heavy", "Iron");
        Playlist playlist5 = createPlayList(5, "Power", "Blind");
        Playlist playlist6 = createPlayList(6, "Heavy", "Iron");
        Song song1 = createSong(1, "Nightfall", "Power", "Germany", 0);
        Song song2 = createSong(2, "Aces", "Heavy", "England", 0);
        Song song3 = createSong(3, "Aces", "Heavy", "England", 0);

        assertEquals(NO_SONGS_MESSAGE, Solution.getMostPopularSong());
        assertOK(Solution.addSong(song1));
        assertEquals(NO_SONGS_MESSAGE, Solution.getMostPopularSong());
        assertOK(Solution.addSong(song2));
        assertEquals(NO_SONGS_MESSAGE, Solution.getMostPopularSong());
        assertOK(Solution.addSong(song3));
        assertEquals(NO_SONGS_MESSAGE, Solution.getMostPopularSong());
        assertOK(Solution.addPlaylist(playlist1));
        assertEquals(NO_SONGS_MESSAGE, Solution.getMostPopularSong());
        assertOK(Solution.addPlaylist(playlist2));
        assertEquals(NO_SONGS_MESSAGE, Solution.getMostPopularSong());
        assertOK(Solution.addPlaylist(playlist3));
        assertEquals(NO_SONGS_MESSAGE, Solution.getMostPopularSong());
        assertOK(Solution.addPlaylist(playlist4));
        assertEquals(NO_SONGS_MESSAGE, Solution.getMostPopularSong());
        assertOK(Solution.addPlaylist(playlist5));
        assertEquals(NO_SONGS_MESSAGE, Solution.getMostPopularSong());
        assertOK(Solution.addPlaylist(playlist6));
        assertEquals(NO_SONGS_MESSAGE, Solution.getMostPopularSong());
        assertOK(Solution.addSongToPlaylist(1, 1));
        assertEquals(song1.getName(), Solution.getMostPopularSong());
        assertOK(Solution.addSongToPlaylist(3, 4));
        assertEquals(song3.getName(), Solution.getMostPopularSong());
        assertOK(Solution.addSongToPlaylist(2, 2));
        assertEquals(song3.getName(), Solution.getMostPopularSong());
        assertOK(Solution.addSongToPlaylist(1, 3));
        assertEquals(song1.getName(), Solution.getMostPopularSong());
        assertOK(Solution.addSongToPlaylist(2, 4));
        assertEquals(song2.getName(), Solution.getMostPopularSong());
        assertOK(Solution.addSongToPlaylist(1, 5));
        assertEquals(song1.getName(), Solution.getMostPopularSong());
        assertOK(Solution.addSongToPlaylist(2, 6));
        assertEquals(song2.getName(), Solution.getMostPopularSong());
        assertOK(Solution.removeSongFromPlaylist(2, 6));
        assertEquals(song1.getName(), Solution.getMostPopularSong());
        assertOK(Solution.removeSongFromPlaylist(1, 5));
        assertEquals(song2.getName(), Solution.getMostPopularSong());
        assertOK(Solution.deletePlaylist(playlist4));
        assertEquals(song1.getName(), Solution.getMostPopularSong());
        assertOK(Solution.deletePlaylist(playlist3));
        assertEquals(song2.getName(), Solution.getMostPopularSong());
        assertOK(Solution.removeSongFromPlaylist(2, 2));
        assertEquals(song1.getName(), Solution.getMostPopularSong());
        assertOK(Solution.removeSongFromPlaylist(1, 1));
        assertEquals(NO_SONGS_MESSAGE, Solution.getMostPopularSong());
    }

    @Test
    public void getMostPopularPlayListTest() {
        Playlist playlist1 = createPlayList(1, "Hard", "Scorpions");
        Playlist playlist2 = createPlayList(2, "Rock", "Kino");
        Playlist playlist3 = createPlayList(3, "Heavy", "Iron");

        Song song1 = createSong(1, "Zoo", "Hard", "Germany", 0);
        Song song2 = createSong(2, "MotherAnarcy", "Rock", "USSR", 0);
        Song song3 = createSong(3, "Zoo", "Hard", "Germany", 0);
        Song song4 = createSong(4, "MotherAnarcy", "Rock", "USSR", 0);
        Song song5 = createSong(5, "Zoo", "Hard", "Germany", 0);
        Song song6 = createSong(6, "MotherAnarcy", "Rock", "USSR", 0);

        assertEquals(Integer.valueOf(0), Solution.getMostPopularPlaylist());
        assertOK(Solution.addPlaylist(playlist1));
        assertEquals(Integer.valueOf(1), Solution.getMostPopularPlaylist());
        assertOK(Solution.addPlaylist(playlist3));
        assertEquals(Integer.valueOf(3), Solution.getMostPopularPlaylist());
        assertOK(Solution.addPlaylist(playlist2));
        assertEquals(Integer.valueOf(3), Solution.getMostPopularPlaylist());

        assertOK(Solution.addSong(song1));
        assertEquals(Integer.valueOf(3), Solution.getMostPopularPlaylist());
        assertOK(Solution.addSong(song2));
        assertEquals(Integer.valueOf(3), Solution.getMostPopularPlaylist());
        assertOK(Solution.addSong(song3));
        assertEquals(Integer.valueOf(3), Solution.getMostPopularPlaylist());
        assertOK(Solution.addSong(song4));
        assertEquals(Integer.valueOf(3), Solution.getMostPopularPlaylist());
        assertOK(Solution.addSong(song5));
        assertEquals(Integer.valueOf(3), Solution.getMostPopularPlaylist());
        assertOK(Solution.addSong(song6));
        assertEquals(Integer.valueOf(3), Solution.getMostPopularPlaylist());

        assertOK(Solution.songPlay(1, 1));
        assertOK(Solution.addSongToPlaylist(1, 1));
        assertEquals(Integer.valueOf(1), Solution.getMostPopularPlaylist());
        assertEquals(BAD_PARAMS, Solution.songPlay(1, -2));
        assertEquals(Integer.valueOf(1), Solution.getMostPopularPlaylist());
        assertOK(Solution.songPlay(2, 1));
        assertEquals(Integer.valueOf(1), Solution.getMostPopularPlaylist());
        assertOK(Solution.addSongToPlaylist(2, 2));
        assertEquals(Integer.valueOf(2), Solution.getMostPopularPlaylist());
        assertOK(Solution.songPlay(2, -1));
        assertEquals(Integer.valueOf(1), Solution.getMostPopularPlaylist());
        assertOK(Solution.songPlay(1, -1));
        assertEquals(Integer.valueOf(3), Solution.getMostPopularPlaylist());
        assertOK(Solution.songPlay(1, 1));
        assertEquals(Integer.valueOf(1), Solution.getMostPopularPlaylist());
        assertOK(Solution.songPlay(2, 1));
        assertEquals(Integer.valueOf(2), Solution.getMostPopularPlaylist());
        assertOK(Solution.addSongToPlaylist(3, 1));
        assertEquals(Integer.valueOf(2), Solution.getMostPopularPlaylist());
        assertOK(Solution.songPlay(3, 1));
        assertEquals(Integer.valueOf(1), Solution.getMostPopularPlaylist());
        assertOK(Solution.songPlay(3, -1));
        assertEquals(Integer.valueOf(2), Solution.getMostPopularPlaylist());
        assertOK(Solution.deleteSong(song2));
        assertEquals(Integer.valueOf(1), Solution.getMostPopularPlaylist());
        assertOK(Solution.removeSongFromPlaylist(1, 1));
        assertEquals(Integer.valueOf(3), Solution.getMostPopularPlaylist());
        assertOK(Solution.deletePlaylist(playlist3));
        assertEquals(Integer.valueOf(2), Solution.getMostPopularPlaylist());
        assertOK(Solution.deletePlaylist(playlist2));
        assertEquals(Integer.valueOf(1), Solution.getMostPopularPlaylist());
        assertOK(Solution.deletePlaylist(playlist1));
        assertEquals(Integer.valueOf(0), Solution.getMostPopularPlaylist());
    }

    @Test
    public void hottestPlayListOnTechnifyTest() {
        Playlist[] playLists = new Playlist[20];
        Song[] songs = new Song[40];

        assertEquals(new ArrayList<Integer>(), Solution.hottestPlaylistsOnTechnify());

        for (int i = 1; i < playLists.length; ++i) {
            playLists[i] = createPlayList(i, "Alternative", "O.Torvald");
            assertOK(Solution.addPlaylist(playLists[i]));
            assertEquals(new ArrayList<Integer>(), Solution.hottestPlaylistsOnTechnify());
        }

        for (int i = 1; i < songs.length; ++i) {
            songs[i] = createSong(i, "Mr. DJ", "Alternative", "Ukraine", 0);
            assertOK(Solution.addSong(songs[i]));
            assertOK(Solution.songPlay(i, i));
        }

        assertOK(Solution.addSongToPlaylist(1, 1));
        assertEquals(new ArrayList<Integer>(){{
            add(1);
        }}, Solution.hottestPlaylistsOnTechnify());

        assertOK(Solution.addSongToPlaylist(2, 2));
        assertEquals(new ArrayList<Integer>(){{
            add(2);
            add(1);
        }}, Solution.hottestPlaylistsOnTechnify());
        assertOK(Solution.addSongToPlaylist(3, 1));
        assertEquals(new ArrayList<Integer>(){{
            add(1);
            add(2);
        }}, Solution.hottestPlaylistsOnTechnify());
        assertOK(Solution.songPlay(2, 1));
        assertEquals(new ArrayList<Integer>(){{
            add(2);
            add(1);
        }}, Solution.hottestPlaylistsOnTechnify());
        assertOK(Solution.addSongToPlaylist(5, 1));
        assertEquals(new ArrayList<Integer>(){{
            add(1);
            add(2);
        }}, Solution.hottestPlaylistsOnTechnify());
        assertOK(Solution.addSongToPlaylist(4, 3));
        assertEquals(new ArrayList<Integer>(){{
            add(3);
            add(1);
            add(2);
        }}, Solution.hottestPlaylistsOnTechnify());
        assertOK(Solution.addSongToPlaylist(9, 1));
        assertEquals(new ArrayList<Integer>(){{
            add(1);
            add(3);
            add(2);
        }}, Solution.hottestPlaylistsOnTechnify());
        assertOK(Solution.addSongToPlaylist(10, 4));
        assertEquals(new ArrayList<Integer>(){{
            add(4);
            add(1);
            add(3);
            add(2);
        }}, Solution.hottestPlaylistsOnTechnify());
        assertOK(Solution.addSongToPlaylist(11, 5));
        assertEquals(new ArrayList<Integer>(){{
            add(5);
            add(4);
            add(1);
            add(3);
            add(2);
        }}, Solution.hottestPlaylistsOnTechnify());
        assertOK(Solution.addSongToPlaylist(12, 6));
        assertEquals(new ArrayList<Integer>(){{
            add(6);
            add(5);
            add(4);
            add(1);
            add(3);
            add(2);
        }}, Solution.hottestPlaylistsOnTechnify());
        assertOK(Solution.addSongToPlaylist(13, 7));
        assertEquals(new ArrayList<Integer>(){{
            add(7);
            add(6);
            add(5);
            add(4);
            add(1);
            add(3);
            add(2);
        }}, Solution.hottestPlaylistsOnTechnify());
        assertOK(Solution.addSongToPlaylist(14, 8));
        assertEquals(new ArrayList<Integer>(){{
            add(8);
            add(7);
            add(6);
            add(5);
            add(4);
            add(1);
            add(3);
            add(2);
        }}, Solution.hottestPlaylistsOnTechnify());
        assertOK(Solution.addSongToPlaylist(15, 9));
        assertEquals(new ArrayList<Integer>(){{
            add(9);
            add(8);
            add(7);
            add(6);
            add(5);
            add(4);
            add(1);
            add(3);
            add(2);
        }}, Solution.hottestPlaylistsOnTechnify());
        assertOK(Solution.addSongToPlaylist(16, 10));
        assertEquals(new ArrayList<Integer>(){{
            add(10);
            add(9);
            add(8);
            add(7);
            add(6);
            add(5);
            add(4);
            add(1);
            add(3);
            add(2);
        }}, Solution.hottestPlaylistsOnTechnify());
        assertOK(Solution.addSongToPlaylist(17, 11));
        assertEquals(new ArrayList<Integer>(){{
            add(11);
            add(10);
            add(9);
            add(8);
            add(7);
            add(6);
            add(5);
            add(4);
            add(1);
            add(3);
        }}, Solution.hottestPlaylistsOnTechnify());
        assertOK(Solution.addSongToPlaylist(18, 12));
        assertEquals(new ArrayList<Integer>(){{
            add(12);
            add(11);
            add(10);
            add(9);
            add(8);
            add(7);
            add(6);
            add(5);
            add(4);
            add(1);
        }}, Solution.hottestPlaylistsOnTechnify());
        assertOK(Solution.removeSongFromPlaylist(14, 8));
        assertEquals(new ArrayList<Integer>(){{
            add(12);
            add(11);
            add(10);
            add(9);
            add(7);
            add(6);
            add(5);
            add(4);
            add(1);
            add(3);
        }}, Solution.hottestPlaylistsOnTechnify());
        assertOK(Solution.deleteSong(songs[12]));
        assertEquals(new ArrayList<Integer>(){{
            add(12);
            add(11);
            add(10);
            add(9);
            add(7);
            add(5);
            add(4);
            add(1);
            add(3);
            add(2);
        }}, Solution.hottestPlaylistsOnTechnify());
    }

    @Test
    public void hottestPlayListOnTechnifyTest2() {
        Playlist playlist1 = createPlayList(1, "Industrial", "Rammstein");
        Playlist playlist2 = createPlayList(2, "Power", "Avantasia");
        Song song1 = createSong(1, "Rammstein", "Industrial", "Germany", 0);
        Song song2 = createSong(2, "Scarecrow", "Power", "Germany", 0);
        Song song3 = createSong(3, "Avantasia", "Power", "Germany", 0);

        assertOK(Solution.addPlaylist(playlist1));
        assertOK(Solution.addPlaylist(playlist2));

        assertOK(Solution.addSong(song1));
        assertOK(Solution.addSong(song2));
        assertOK(Solution.addSong(song3));

        assertOK(Solution.addSongToPlaylist(1, 1));
        assertEquals(new ArrayList<Integer>(){{
                add(1);
        }}, Solution.hottestPlaylistsOnTechnify());
        assertOK(Solution.songPlay(1, 4));
        assertEquals(new ArrayList<Integer>(){{
            add(1);
        }}, Solution.hottestPlaylistsOnTechnify());
        assertOK(Solution.addSongToPlaylist(2, 2));
        assertEquals(new ArrayList<Integer>(){{
            add(1);
            add(2);
        }}, Solution.hottestPlaylistsOnTechnify());
        assertOK(Solution.songPlay(2, 9));
        assertEquals(new ArrayList<Integer>(){{
            add(2);
            add(1);
        }}, Solution.hottestPlaylistsOnTechnify());
        assertOK(Solution.addSongToPlaylist(3, 2));
        assertEquals(new ArrayList<Integer>(){{
            add(1);
            add(2);
        }}, Solution.hottestPlaylistsOnTechnify());
    }

    @Test
    public void getSimilarUsersTest() {
        Playlist playlist1 = createPlayList(1, "Heavy", "Maiden");
        Playlist playlist2 = createPlayList(2, "Heavy", "Maiden");
        Playlist playlist3 = createPlayList(3, "Heavy", "Maiden");
        Playlist playlist4 = createPlayList(4, "Heavy", "Maiden");

        assertOK(Solution.addPlaylist(playlist1));
        assertOK(Solution.addPlaylist(playlist2));
        assertOK(Solution.addPlaylist(playlist3));
        assertOK(Solution.addPlaylist(playlist4));

        User user1 = createUser(1, "Ben", "Canada", true);
        User user2 = createUser(2, "Claus", "Germany", false);
        User user3 = createUser(3, "Tarja", "Finland", true);
        User user4 = createUser(4, "Marco", "Finland", false);
        User user5 = createUser(5, "Tuomas", "Finland", true);
        User user6 = createUser(6, "Tsoy", "USSR", true);

        assertOK(Solution.addUser(user1));
        assertOK(Solution.addUser(user3));
        assertOK(Solution.addUser(user2));
        assertOK(Solution.addUser(user4));
        assertOK(Solution.addUser(user5));
        assertOK(Solution.addUser(user6));

        assertOK(Solution.followPlaylist(user1.getId(), playlist1.getId()));
        assertOK(Solution.followPlaylist(user1.getId(), playlist2.getId()));
        assertOK(Solution.followPlaylist(user1.getId(), playlist3.getId()));
        assertOK(Solution.followPlaylist(user1.getId(), playlist4.getId()));

        assertOK(Solution.followPlaylist(user3.getId(), playlist1.getId()));
        assertOK(Solution.followPlaylist(user3.getId(), playlist2.getId()));
        assertOK(Solution.followPlaylist(user3.getId(), playlist3.getId()));

        assertOK(Solution.followPlaylist(user2.getId(), playlist1.getId()));
        assertOK(Solution.followPlaylist(user2.getId(), playlist2.getId()));
        assertOK(Solution.followPlaylist(user2.getId(), playlist3.getId()));
        assertOK(Solution.followPlaylist(user2.getId(), playlist4.getId()));

        assertOK(Solution.followPlaylist(user4.getId(), playlist1.getId()));
        assertOK(Solution.followPlaylist(user4.getId(), playlist2.getId()));

        assertOK(Solution.followPlaylist(user5.getId(), playlist1.getId()));

        assertEquals(
                new ArrayList<Integer>(){{add(user2.getId()); add(user3.getId());}},
                Solution.getSimilarUsers(user1.getId())
        );

        assertEquals(
                new ArrayList<Integer>(){{
                    // They told no user :/
//                    add(user1.getId());
//                    add(user2.getId());
//                    add(user3.getId());
//                    add(user4.getId());
//                    add(user5.getId());
                }},
                Solution.getSimilarUsers(user6.getId())
        );

        // TODO: test limit etc
    }

    @Test
    public void getPlaylistRecommendationTest() {
        User[] users = new User[40];
        Playlist[] playLists = new Playlist[40];

        assertEquals(new ArrayList<Integer>(), Solution.getPlaylistRecommendation(MY_PERSONAL_NULL_INTEGER_WITH_BLACK_JACK_AND_HOOKERS));
        assertEquals(new ArrayList<Integer>(), Solution.getPlaylistRecommendation(-1));
        assertEquals(new ArrayList<Integer>(), Solution.getPlaylistRecommendation(0));
        assertEquals(new ArrayList<Integer>(), Solution.getPlaylistRecommendation(1));

        for (int i = 1; i < playLists.length; ++i) {
            playLists[i] = createPlayList(i, "Power", "Helloween");
            assertOK(Solution.addPlaylist(playLists[i]));
        }

        for (int i = 1; i < users.length; ++i) {
            users[i] = createUser(i, "Andy", "Germany", i % 2 == 0);
            assertOK(Solution.addUser(users[i]));
            assertEquals(new ArrayList<Integer>(), Solution.getPlaylistRecommendation(i));

            for (int j = 1; j <= i; ++j) {
                assertOK(Solution.followPlaylist(i, j));
            }
        }

        assertEquals(
                new ArrayList<Integer>(){{
                    add(2);
                    add(3);
                    add(4);
                    add(5);
                    add(6);
                }},
                Solution.getPlaylistRecommendation(1)
        );

        assertEquals(
                new ArrayList<Integer>(){{
                    add(21);
                    add(22);
                    add(23);
                    add(24);
                    add(25);
                }},
                Solution.getPlaylistRecommendation(20)
        );
    }

    @Test
    public void getTopCountryPlaylistsTest() {
        User user1 = createUser(1, "Mark", "USA", false);
        User user2 = createUser(2, "Travis", "USA", true);
        Song song1 = createSong(1, "I", "Alt", "USA", 0);
        Song song2 = createSong(2, "I", "Alt", "UK", 0);
        Song song3 = createSong(3, "I", "Alt", "USA", 0);
        Song song4 = createSong(4, "I am the mountain", "Stoner", null, 0);
        Song song5 = createSong(5, "I", "Alt", "USA", 0);
        Song song6 = createSong(6, "I", "Alt", null, 0);
        Song song7 = createSong(7, "I", "Alt", "USA", 0);
        Song song8 = createSong(8, "I", "Alt", "USA", 0);
        Song song9 = createSong(9, "I", "Alt", "USA", 0);
        Song song10 = createSong(10, "I", "Alt", "USA", 0);
        Song song11 = createSong(11, "I", "Alt", "USA", 0);
        Song song12 = createSong(12, "I", "Alt", "USA", 0);
        Song song13 = createSong(13, "I", "Alt", "USA", 0);

        Playlist playlist1 = createPlayList(1, "Alt", "");
        Playlist playlist2 = createPlayList(2, "Alt", "");
        Playlist playlist3 = createPlayList(3, "Alt", "");
        Playlist playlist4 = createPlayList(4, "Alt", "");
        Playlist playlist5 = createPlayList(5, "Stoner", "Stoned Jesus");
        Playlist playlist6 = createPlayList(6, "Alt", "");
        Playlist playlist7 = createPlayList(7, "Alt", "");
        Playlist playlist8 = createPlayList(8, "Alt", "");
        Playlist playlist9 = createPlayList(9, "Alt", "");
        Playlist playlist10 = createPlayList(10, "Alt", "");
        Playlist playlist11 = createPlayList(11, "Alt", "");
        Playlist playlist12 = createPlayList(12, "Alt", "");
        Playlist playlist13 = createPlayList(13, "Alt", "");

        assertEquals(new ArrayList<Integer>(), Solution.getTopCountryPlaylists(MY_PERSONAL_NULL_INTEGER_WITH_BLACK_JACK_AND_HOOKERS));
        assertEquals(new ArrayList<Integer>(), Solution.getTopCountryPlaylists(-1));
        assertEquals(new ArrayList<Integer>(), Solution.getTopCountryPlaylists(0));
        assertEquals(new ArrayList<Integer>(), Solution.getTopCountryPlaylists(1));

        assertOK(Solution.addUser(user1));
        assertOK(Solution.addUser(user2));
        assertOK(Solution.addSong(song1));
        assertOK(Solution.addSong(song2));
        assertOK(Solution.addSong(song3));
        assertOK(Solution.addSong(song4));
        assertOK(Solution.addSong(song5));
        assertOK(Solution.addSong(song6));
        assertOK(Solution.addSong(song7));
        assertOK(Solution.addSong(song8));
        assertOK(Solution.addSong(song9));
        assertOK(Solution.addSong(song10));
        assertOK(Solution.addSong(song11));
        assertOK(Solution.addSong(song12));
        assertOK(Solution.addSong(song13));
        assertOK(Solution.addPlaylist(playlist1));
        assertOK(Solution.addPlaylist(playlist2));
        assertOK(Solution.addPlaylist(playlist3));
        assertOK(Solution.addPlaylist(playlist4));
        assertOK(Solution.addPlaylist(playlist5));
        assertOK(Solution.addPlaylist(playlist6));
        assertOK(Solution.addPlaylist(playlist7));
        assertOK(Solution.addPlaylist(playlist8));
        assertOK(Solution.addPlaylist(playlist9));
        assertOK(Solution.addPlaylist(playlist10));
        assertOK(Solution.addPlaylist(playlist11));
        assertOK(Solution.addPlaylist(playlist12));
        assertOK(Solution.addPlaylist(playlist13));
        assertOK(Solution.addSongToPlaylist(2, 1));
        assertOK(Solution.addSongToPlaylist(1, 3));
        assertOK(Solution.addSongToPlaylist(3, 3));
        assertOK(Solution.addSongToPlaylist(1, 2));
        assertOK(Solution.addSongToPlaylist(2, 2));
        assertOK(Solution.addSongToPlaylist(3, 2));
        assertOK(Solution.addSongToPlaylist(1, 4));
        assertOK(Solution.addSongToPlaylist(2, 4));
        assertOK(Solution.addSongToPlaylist(3, 4));
        assertOK(Solution.followPlaylist(1, 2));
        assertOK(Solution.followPlaylist(2, 1));
        assertOK(Solution.songPlay(2, 42));
        assertOK(Solution.songPlay(1, 23));
        assertOK(Solution.songPlay(3, 7));
        assertOK(Solution.songPlay(4, 69));
        assertOK(Solution.addSongToPlaylist(4, 5));


        assertEquals(new ArrayList<Integer>(), Solution.getTopCountryPlaylists(MY_PERSONAL_NULL_INTEGER_WITH_BLACK_JACK_AND_HOOKERS));
        assertEquals(new ArrayList<Integer>(), Solution.getTopCountryPlaylists(-1));
        assertEquals(new ArrayList<Integer>(), Solution.getTopCountryPlaylists(0));
        assertEquals(new ArrayList<Integer>(), Solution.getTopCountryPlaylists(1));
        assertEquals(new ArrayList<Integer>(), Solution.getTopCountryPlaylists(3));

        assertEquals(new ArrayList<Integer>(){{
            add(2);
            add(4);
            add(3);
        }}, Solution.getTopCountryPlaylists(2));

        assertOK(Solution.addSongToPlaylist(5, 7));

        assertEquals(new ArrayList<Integer>(){{
            add(2);
            add(4);
            add(3);
            add(7);
        }}, Solution.getTopCountryPlaylists(2));

        assertOK(Solution.songPlay(5, 26));

        assertEquals(new ArrayList<Integer>(){{
            add(2);
            add(4);
            add(3);
            add(7);
        }}, Solution.getTopCountryPlaylists(2));

        assertOK(Solution.addSongToPlaylist(6, 6));

        assertEquals(new ArrayList<Integer>(){{
            add(2);
            add(4);
            add(3);
            add(7);
        }}, Solution.getTopCountryPlaylists(2));

        assertOK(Solution.songPlay(6, 26));

        assertEquals(new ArrayList<Integer>(){{
            add(2);
            add(4);
            add(3);
            add(7);
        }}, Solution.getTopCountryPlaylists(2));

        assertOK(Solution.addSongToPlaylist(7, 6));

        assertEquals(new ArrayList<Integer>(){{
            add(2);
            add(4);
            add(3);
            add(6);
            add(7);
        }}, Solution.getTopCountryPlaylists(2));

        assertOK(Solution.songPlay(7, 1));

        assertEquals(new ArrayList<Integer>(){{
            add(2);
            add(4);
            add(3);
            add(6);
            add(7);
        }}, Solution.getTopCountryPlaylists(2));

        assertOK(Solution.addSongToPlaylist(7, 10));

        assertEquals(new ArrayList<Integer>(){{
            add(2);
            add(4);
            add(3);
            add(6);
            add(7);
            add(10);
        }}, Solution.getTopCountryPlaylists(2));

        assertOK(Solution.addSongToPlaylist(8, 10));

        assertEquals(new ArrayList<Integer>(){{
            add(2);
            add(4);
            add(3);
            add(6);
            add(7);
            add(10);
        }}, Solution.getTopCountryPlaylists(2));

        assertOK(Solution.songPlay(8, 27));
        assertOK(Solution.addSongToPlaylist(9, 9));
        assertEquals(new ArrayList<Integer>(){{
            add(2);
            add(4);
            add(3);
            add(10);
            add(6);
            add(7);
            add(9);
        }}, Solution.getTopCountryPlaylists(2));
        assertOK(Solution.songPlay(9, 28));

        assertEquals(new ArrayList<Integer>(){{
            add(2);
            add(4);
            add(3);
            add(9);
            add(10);
            add(6);
            add(7);
        }}, Solution.getTopCountryPlaylists(2));

        assertOK(Solution.addSongToPlaylist(10, 8));
        assertEquals(new ArrayList<Integer>(){{
            add(2);
            add(4);
            add(3);
            add(9);
            add(10);
            add(6);
            add(7);
            add(8);
        }}, Solution.getTopCountryPlaylists(2));

        assertOK(Solution.songPlay(10, 28));
        assertEquals(new ArrayList<Integer>(){{
            add(2);
            add(4);
            add(3);
            add(8);
            add(9);
            add(10);
            add(6);
            add(7);
        }}, Solution.getTopCountryPlaylists(2));

        assertOK(Solution.addSongToPlaylist(11, 12));
        assertEquals(new ArrayList<Integer>(){{
            add(2);
            add(4);
            add(3);
            add(8);
            add(9);
            add(10);
            add(6);
            add(7);
            add(12);
        }}, Solution.getTopCountryPlaylists(2));

        assertOK(Solution.songPlay(11, 29));
        assertEquals(new ArrayList<Integer>(){{
            add(2);
            add(4);
            add(3);
            add(12);
            add(8);
            add(9);
            add(10);
            add(6);
            add(7);
        }}, Solution.getTopCountryPlaylists(2));

        assertOK(Solution.addSongToPlaylist(12, 11));
        assertEquals(new ArrayList<Integer>(){{
            add(2);
            add(4);
            add(3);
            add(12);
            add(8);
            add(9);
            add(10);
            add(6);
            add(7);
            add(11);
        }}, Solution.getTopCountryPlaylists(2));
        assertOK(Solution.songPlay(12, 29));
        assertEquals(new ArrayList<Integer>(){{
            add(2);
            add(4);
            add(3);
            add(11);
            add(12);
            add(8);
            add(9);
            add(10);
            add(6);
            add(7);
        }}, Solution.getTopCountryPlaylists(2));
        assertOK(Solution.addSongToPlaylist(13, 13));
        assertEquals(new ArrayList<Integer>(){{
            add(2);
            add(4);
            add(3);
            add(11);
            add(12);
            add(8);
            add(9);
            add(10);
            add(6);
            add(7);
        }}, Solution.getTopCountryPlaylists(2));
        assertOK(Solution.songPlay(13, 321));
        assertEquals(new ArrayList<Integer>(){{
            add(13);
            add(2);
            add(4);
            add(3);
            add(11);
            add(12);
            add(8);
            add(9);
            add(10);
            add(6);
        }}, Solution.getTopCountryPlaylists(2));
    }

    @Test
    public void getSongsRecommendationByGenreTest() {
        User user1 = createUser(1, "Noodles", "USA", false);
        User user2 = createUser(2, "Noodles", "USA", true);
        Playlist playlist1 = createPlayList(1, "Punk", "");
        Playlist playlist2 = createPlayList(2, "Alternative", "");
        Song song1 = createSong(1, "The", "Punk", "USA", 0);
        Song song2 = createSong(2, "Kids", "Punk", "USA", 0);
        Song song3 = createSong(3, "Aren't", "Punk", "USA", 0);
        Song song4 = createSong(4, "All", "Punk", "USA", 0);
        Song song5 = createSong(5, "Right", "Punk", "USA", 0);
        String injectionGenre = "'; DROP TABLE songs CASCADE;--";
        Song songInjectionGenre = createSong(42, "I Want Out", injectionGenre, "USA", 0);

        assertEquals(new ArrayList<Integer>(), Solution.getSongsRecommendationByGenre(MY_PERSONAL_NULL_INTEGER_WITH_BLACK_JACK_AND_HOOKERS, MY_PERSONAL_NULL_STRING_WITH_BLACK_JACK_AND_HOOKERS));
        assertEquals(new ArrayList<Integer>(), Solution.getSongsRecommendationByGenre(MY_PERSONAL_NULL_INTEGER_WITH_BLACK_JACK_AND_HOOKERS, ""));
        assertEquals(new ArrayList<Integer>(), Solution.getSongsRecommendationByGenre(-1, MY_PERSONAL_NULL_STRING_WITH_BLACK_JACK_AND_HOOKERS));
        assertEquals(new ArrayList<Integer>(), Solution.getSongsRecommendationByGenre(-1, ""));
        assertEquals(new ArrayList<Integer>(), Solution.getSongsRecommendationByGenre(0, MY_PERSONAL_NULL_STRING_WITH_BLACK_JACK_AND_HOOKERS));
        assertEquals(new ArrayList<Integer>(), Solution.getSongsRecommendationByGenre(0, ""));
        assertEquals(new ArrayList<Integer>(), Solution.getSongsRecommendationByGenre(1, MY_PERSONAL_NULL_STRING_WITH_BLACK_JACK_AND_HOOKERS));
        assertEquals(new ArrayList<Integer>(), Solution.getSongsRecommendationByGenre(1, ""));

        assertOK(Solution.addSong(song1));
        assertOK(Solution.addSong(song2));
        assertOK(Solution.addSong(song3));
        assertOK(Solution.addSong(song4));
        assertOK(Solution.addSong(song5));
        assertOK(Solution.addPlaylist(playlist1));
        assertOK(Solution.addPlaylist(playlist2));
        assertOK(Solution.addUser(user1));
        assertOK(Solution.addUser(user2));

        assertOK(Solution.songPlay(5, 5));
        assertOK(Solution.songPlay(2, 5));
        assertOK(Solution.songPlay(3, 3));
        assertOK(Solution.songPlay(4, 2));
        assertOK(Solution.songPlay(1, 1));
        assertOK(Solution.followPlaylist(1, 1));
        assertOK(Solution.followPlaylist(2, 2));

        assertEquals(new ArrayList<Integer>(){{
            add(2);
            add(5);
            add(3);
            add(4);
            add(1);
        }}, Solution.getSongsRecommendationByGenre(1, "Punk"));

        assertEquals(new ArrayList<Integer>(){{
            add(2);
            add(5);
            add(3);
            add(4);
            add(1);
        }}, Solution.getSongsRecommendationByGenre(2, "Punk"));

        assertOK(Solution.addSongToPlaylist(5, 1));

        assertEquals(new ArrayList<Integer>(){{
            add(2);
            add(3);
            add(4);
            add(1);
        }}, Solution.getSongsRecommendationByGenre(1, "Punk"));

        assertEquals(new ArrayList<Integer>(){{
            add(2);
            add(5);
            add(3);
            add(4);
            add(1);
        }}, Solution.getSongsRecommendationByGenre(2, "Punk"));

        assertOK(Solution.addSong(songInjectionGenre));

        assertEquals(new ArrayList<Integer>(){{add(42);}}, Solution.getSongsRecommendationByGenre(1, injectionGenre));
    }

    @Test
    public void stress() {
        int numberOfCommands = 42;
        long seed = 42;
        String[] countries = new String[] {"Israel", "Ukraine", "Germany", "England", "France", "Poland"};
        String[] genres = new String[] {"Power", "Heavy", "Hard", "Punk", "Alternative", "Rock", "Metal"};
        Map<Integer, User> idToUser = new HashMap<>();
        Map<Integer, Song> idToSong = new HashMap<>();
        Map<Integer, Playlist> idToPlayList = new HashMap<>();
        Random random = new Random(seed);

        for (int commandIndex = 0; commandIndex < numberOfCommands; ++commandIndex) {
            int commandType = random.nextInt();

            switch (commandType) {
                case 0:
                    User user = createUser(
                            random.nextInt(2 * idToUser.size()),
                            "",
                            countries[random.nextInt(countries.length)],
                            random.nextBoolean()
                    );
                    add(user.getId(), user, idToUser, Solution::addUser);
                    break;

                case 1:
                    Song song = createSong(
                            random.nextInt(2 * idToSong.size()),
                            "",
                            genres[random.nextInt(genres.length)],
                            countries[random.nextInt(countries.length)],
                            0
                    );
                    add(song.getId(), song, idToSong, Solution::addSong);
                    break;

                case 2:
                    Playlist playlist = createPlayList(
                            random.nextInt(2 * idToPlayList.size()),
                            genres[random.nextInt(genres.length)],
                            ""
                    );
                    add(playlist.getId(), playlist, idToPlayList, Solution::addPlaylist);
                    break;

                case 3:
                    delete(random.nextInt(2 * idToUser.size()), idToUser, Solution::deleteUser);

                    break;

                case 4:
                    delete(random.nextInt(2 * idToSong.size()), idToSong, Solution::deleteSong);

                    break;

                case 5:
                    delete(random.nextInt(2 * idToPlayList.size()), idToPlayList, Solution::deletePlaylist);

                    break;

                case 6:
                    int songId= random.nextInt(2 * idToSong.size());
                    int times = random.nextInt(42);

                    if (idToSong.containsKey(songId)) {
                        int newPlayCount = idToSong.get(songId).getPlayCount() + times;
                        if (newPlayCount >= 0) {
                            assertOK(Solution.songPlay(songId, times));
                            idToSong.get(songId).setPlayCount(newPlayCount);
                        }
                        else {
                            assertEquals(BAD_PARAMS, Solution.songPlay(songId, times));
                        }
                    }
                    else {
                        assertEquals(NOT_EXISTS, Solution.songPlay(songId, times));
                    }

            }
        }
    }

    private<T> void  add(int id, T value, Map<Integer, T> idToValue, Function<T, ReturnValue> adder) {
        if (idToValue.containsKey(id)) {
            assertEquals(ALREADY_EXISTS, adder.apply(value));
        }
        else {
            idToValue.put(id, value);
            assertEquals(OK, adder.apply(value));
        }

    }

    private<T> void  delete(int id, Map<Integer, T> idToValue, Function<T, ReturnValue> deleter) {
        if (idToValue.containsKey(id)) {
            assertEquals(OK, deleter.apply(idToValue.get(id)));
            idToValue.remove(id);
        }
    }

    private Playlist createPlayList(Integer id, String genre, String description) {
        Playlist playlist = new Playlist();
        playlist.setId(id);
        playlist.setGenre(genre);
        playlist.setDescription(description);

        return playlist;
    }

    private User createUser(Integer id, String name, String country, Boolean premium) {
        User user = new User();
        user.setId(id);
        user.setName(name);
        user.setCountry(country);
        user.setPremium(premium);

        return user;
    }

    private Song createSong(Integer id, String name, String genre, String country, Integer playCount) {
        Song song = Song.badSong();
        song.setId(id);
        song.setName(name);
        song.setGenre(genre);
        song.setCountry(country);
        song.setPlayCount(playCount);

        return song;
    }

    private void assertOK(ReturnValue returnValue) {
        assertEquals(OK, returnValue);
    }

    private static final String NO_SONGS_MESSAGE = "No songs";

    // It was promised then no function receives null...
    private static final Integer MY_PERSONAL_NULL_INTEGER_WITH_BLACK_JACK_AND_HOOKERS = -1;
    private static final String MY_PERSONAL_NULL_STRING_WITH_BLACK_JACK_AND_HOOKERS = "(-null=)";
}
