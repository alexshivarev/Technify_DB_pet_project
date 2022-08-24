package technify;

import org.junit.Test;
import technify.business.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import static technify.business.ReturnValue.*;


public class testBasic extends  AbstractTest{

    @Test
    public void simpleSongPlaylistTest() {
        Solution.createTables();
        Song song = new Song();
        song.setId(1);
        song.setGenre("pop");
        song.setName("AA");
        song.setCountry("Japan");
        //add song
        ReturnValue ret = Solution.addSong(song);
        assertEquals(OK, ret);
        // try add counts no song
        ret = Solution.songPlay(2,3);
        assertEquals(NOT_EXISTS,ret);
        //try make count negative
        ret = Solution.songPlay(1,-1);
        assertEquals(BAD_PARAMS,ret);
        //check ok
        ret = Solution.songPlay(1,10);
        assertEquals(OK,ret);
        Song song_ret = Solution.getSong(1);
        assertEquals(10,song_ret.getPlayCount());

        Playlist playlist = new Playlist();
        playlist.setDescription("AAA");
        playlist.setGenre("A");
        playlist.setId(2);

        Playlist playlist2 = new Playlist();
        playlist2.setDescription("AAA");
        playlist2.setGenre("pop");
        playlist2.setId(3);
        //try add song to not exist playlist
        ret = Solution.addSongToPlaylist(1,1);
        assertEquals(BAD_PARAMS, ret);
        // add playlist
        ret = Solution.addPlaylist(playlist);
        assertEquals(OK, ret);
        //no such song
        ret = Solution.addSongToPlaylist(2,2);
        assertEquals(BAD_PARAMS, ret);
        //not same genre
        ret = Solution.addSongToPlaylist(1,2);
        assertEquals(BAD_PARAMS, ret);
        //add playlist 2
        ret = Solution.addPlaylist(playlist2);
        assertEquals(OK, ret);
        //now ok
        ret = Solution.addSongToPlaylist(1,3);
        assertEquals(OK, ret);
        //alredy exist
        ret = Solution.addSongToPlaylist(1,3);
        assertEquals(ALREADY_EXISTS, ret);
        // remove not exist
        ret = Solution.removeSongFromPlaylist(1,1);
        assertEquals(NOT_EXISTS, ret);
        ret = Solution.removeSongFromPlaylist(2,2);
        assertEquals(NOT_EXISTS, ret);
        // now ok
        ret = Solution.removeSongFromPlaylist(1,3);
        assertEquals(OK, ret);
        //check if deleted
        ret = Solution.removeSongFromPlaylist(1,3);
        assertEquals(NOT_EXISTS, ret);
        Solution.dropTables();
    }

    @Test
    public void simpleFollowTest()
    {
        Solution.createTables();
        User v = new User();
        v.setId(1);
        v.setName("Dani");
        v.setCountry("Israel");
        v.setPremium(true);

        //add dani
        ReturnValue ret = Solution.addUser(v);
        assertEquals(OK, ret);

        Playlist playlist = new Playlist();
        playlist.setDescription("AAA");
        playlist.setGenre("A");
        playlist.setId(2);
        //add playlist
        ret = Solution.addPlaylist(playlist);
        assertEquals(OK, ret);
        //try follow with no exist
        ret = Solution.followPlaylist(2,2);
        assertEquals(NOT_EXISTS, ret);
        ret = Solution.followPlaylist(1,1);
        assertEquals(NOT_EXISTS, ret);
        //now ok
        ret = Solution.followPlaylist(1,2);
        assertEquals(OK, ret);
        //already exist
        ret = Solution.followPlaylist(1,2);
        assertEquals(ALREADY_EXISTS, ret);
        //try delete not exist
        ret = Solution.stopFollowPlaylist(2,2);
        assertEquals(NOT_EXISTS, ret);
        ret = Solution.stopFollowPlaylist(1,1);
        assertEquals(NOT_EXISTS, ret);
        //now ok
        ret = Solution.stopFollowPlaylist(1,2);
        assertEquals(OK, ret);
        //check if deleted
        ret = Solution.stopFollowPlaylist(1,2);
        assertEquals(NOT_EXISTS, ret);
        Solution.dropTables();
    }
}