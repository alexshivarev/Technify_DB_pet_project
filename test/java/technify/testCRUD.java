package technify;

import org.junit.Test;
import technify.business.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import static technify.business.ReturnValue.*;


public class testCRUD extends  AbstractTest {

    @Test
    public void simpleUserTest() {
        Solution.createTables();
        User v = new User();
        v.setId(1);
        v.setName("Dani");
        v.setCountry("Israel");
        v.setPremium(true);
        //add dani
        ReturnValue ret = Solution.addUser(v);
        assertEquals(OK, ret);
        // change dani premium /fail/
        ret = Solution.updateUserPremium(1);
        assertEquals(ALREADY_EXISTS, ret);
        // change dani not premium /ok/
        ret = Solution.updateUserNotPremium(1);
        assertEquals(OK, ret);
        //get dani
        User u = Solution.getUserProfile(1);
        assertEquals("Dani", u.getName());
        assertEquals(false, u.getPremium());
        // change dani not premium /fail/
        ret = Solution.updateUserNotPremium(1);
        assertEquals(ALREADY_EXISTS, ret);
        //change again to premium
        ret = Solution.updateUserPremium(1);
        assertEquals(OK, ret);
        //change for not exist
        ret = Solution.updateUserPremium(2);
        assertEquals(NOT_EXISTS, ret);
        ret = Solution.updateUserPremium(3);
        assertEquals(NOT_EXISTS, ret);
        // get bad
        User u2 = Solution.getUserProfile(2);
        assertEquals(u2.getId(), -1);
        // delete bad
        ret = Solution.deleteUser(u2);
        assertEquals(NOT_EXISTS, ret);
        // delete dani
        ret = Solution.deleteUser(u);
        assertEquals(OK, ret);
        // get dani /bad/
        User bad = Solution.getUserProfile(1);
        assertEquals(bad.getId(), -1);
        Solution.dropTables();
    }

    @Test
    public void simpleUserTest2()
    {
        Solution.createTables();
        User v = new User();
        v.setId(1);
        v.setName("Dani");
        v.setCountry("Israel");
        v.setPremium(true);

        User v2 = new User();
        v2.setId(1);
        v2.setName("Yossi");
        v2.setCountry("Israel");
        v2.setPremium(true);
        //add dani
        ReturnValue ret = Solution.addUser(v);
        assertEquals(OK, ret);
        //same user id
        ret = Solution.addUser(v2);
        assertEquals(ALREADY_EXISTS, ret);
        // now ok
        v2.setId(2);
        ret = Solution.addUser(v2);
        assertEquals(OK, ret);
        //get dani
        User u = Solution.getUserProfile(2);
        assertEquals(2,u.getId());
        Solution.clearTables();
        //both cleared
        ret = Solution.deleteUser(v);
        assertEquals(NOT_EXISTS , ret);
        ret = Solution.deleteUser(v2);
        assertEquals(NOT_EXISTS , ret);
        Solution.dropTables();
    }

    @Test
    public void testSimpleSong()
    {
        Solution.createTables();
        Song song = new Song();
        song.setId(1);

        song.setGenre("pop");
        song.setCountry("Japan");
        //fail null Name
        ReturnValue ret;
        ret = Solution.addSong(song);
        assertEquals(BAD_PARAMS, ret);
        song.setName("A");
        //now ok
        ret = Solution.addSong(song);
        assertEquals(OK, ret);
        //already exist
        ret = Solution.addSong(song);
        assertEquals(ALREADY_EXISTS, ret);

        Song song2 = new Song();
        song2.setId(2);
        song2.setGenre("Rock");
        song2.setCountry("UK");
        song2.setName("B");
        ret = Solution.addSong(song2);
        assertEquals(OK, ret);

        Song song_ret = Solution.getSong(3);
        //no song /get bad song/
        assertEquals(-1,song_ret.getId());
        //get second song
        song_ret = Solution.getSong(2);
        assertEquals("B", song_ret.getName());
        //delete bad song /not exist
        ret = Solution.deleteSong(Song.badSong());
        assertEquals(NOT_EXISTS,ret);
        //delete song 2 should be ok
        ret = Solution.deleteSong(song2);
        assertEquals(OK,ret);
        //get song 2 should fail
        song_ret = Solution.getSong(2);
        assertEquals(song_ret, Song.badSong());

        // try not exist
        ret = Solution.updateSongName(Song.badSong());
        assertEquals(NOT_EXISTS, ret);

        //try with null /bad params
        song.setName(null);
        ret = Solution.updateSongName(song);
        assertEquals(BAD_PARAMS, ret);
        song.setName("C");
        //should be ok
        ret = Solution.updateSongName(song);
        assertEquals(OK, ret);
        song_ret = Solution.getSong(1);
        assertEquals("C",song_ret.getName());
        Solution.dropTables();
    }

    @Test
    public void testSimplePlaylist()
    {
        Solution.createTables();
        ReturnValue ret;
        Playlist play = new Playlist();
        play.setId(-2);
        play.setGenre("A");
        play.setDescription(null);

        Playlist play2 = new Playlist();
        play2.setId(1);
        play2.setGenre("B");
        play2.setDescription("aaa");
        // try add bad params
        ret = Solution.addPlaylist(play);
        assertEquals(BAD_PARAMS, ret);
        play.setId(1);
        // still bad params
        ret = Solution.addPlaylist(play);
        assertEquals(BAD_PARAMS, ret);
        play.setDescription("kkkkk");
        // now ok
        ret = Solution.addPlaylist(play);
        assertEquals(OK, ret);
        // try add with same id
        ret = Solution.addPlaylist(play2);
        assertEquals(ALREADY_EXISTS, ret);
        //now ok
        play2.setId(2);
        ret = Solution.addPlaylist(play2);
        assertEquals(OK, ret);
        //get bad playlist
        Playlist play_ret = Solution.getPlaylist(3);
        assertEquals(-1, play_ret.getId());
        // good play
        play_ret = Solution.getPlaylist(2);
        assertEquals(play_ret, play2);
        //delete ok
        ret = Solution.deletePlaylist(play_ret);
        assertEquals(OK , ret );
        // delete not exist
        ret = Solution.deletePlaylist(play2);
        assertEquals(NOT_EXISTS, ret);
        //update null error
        play.setDescription(null);
        ret = Solution.updatePlaylist(play);
        assertEquals(BAD_PARAMS , ret);
        play.setDescription("new description");
        //now ok
        ret = Solution.updatePlaylist(play);
        assertEquals(OK, ret);
        play_ret = Solution.getPlaylist(1);
        assertEquals("new description", play_ret.getDescription());
        //clear tables
        Solution.clearTables();
        //should get bad playlist
        play_ret = Solution.getPlaylist(1);
        assertEquals(-1, play_ret.getId());

        Solution.dropTables();
    }
}


