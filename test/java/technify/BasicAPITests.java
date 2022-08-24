package technify;

import org.junit.Test;
import technify.business.*;
import static org.junit.Assert.assertEquals;
import static technify.business.ReturnValue.*;


public class BasicAPITests extends AbstractTest {
    @Test
    public void songPlayTest() {

        ReturnValue res;
        Song s = new Song();
        s.setId(1);
        s.setName("Despacito");
        s.setGenre("Latin");
        s.setCountry("Spain");

        res = Solution.addSong(s);
        assertEquals(OK, res);

        res = Solution.songPlay(1, 1);
        assertEquals(OK, res);

        res = Solution.songPlay(1, -3);
        assertEquals(BAD_PARAMS, res);
    }

    @Test
    public void followPlaylistTest() {

        ReturnValue res;
        Playlist p = new Playlist();
        p.setId(10);
        p.setGenre("Pop");
        p.setDescription("Best pop songs of 2018");

        res = Solution.addPlaylist(p);
        assertEquals(OK, res);

        User u = new User();
        u.setId(100);
        u.setName("Nir");
        u.setCountry("Israel");
        u.setPremium(false);

        res = Solution.addUser(u);
        assertEquals(OK, res);

        res = Solution.followPlaylist(100, 10);
        assertEquals(OK , res);

        res = Solution.followPlaylist(100, 10);
        assertEquals(ALREADY_EXISTS , res);

        res = Solution.followPlaylist(101, 10);
        assertEquals(NOT_EXISTS , res);
    }
}

