package technify;

import org.junit.Test;
import technify.business.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import static technify.business.ReturnValue.*;

import java.util.ArrayList;




public class advancedTest extends AbstractTest{

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


    @Test
    public void hottestPlayListOnTechnifyTest() {
        Solution.createTables();
        Playlist[] playLists = new Playlist[20];
        Song[] songs = new Song[40];
        ReturnValue ret;
        assertEquals(new ArrayList<Integer>(), Solution.hottestPlaylistsOnTechnify());
        String desc = "d";
        String gen = "g";
        for (int i = 1; i < playLists.length; ++i) {
            playLists[i] = createPlayList(i,gen, desc);
            assertOK(Solution.addPlaylist(playLists[i]));
            desc = desc +"d";
        }
        String name = "A";
        for (int i = 1; i < songs.length; ++i) {
            songs[i] = createSong(i, name, gen, "Ukraine", 0);
            assertOK(Solution.addSong(songs[i]));
            assertOK(Solution.songPlay(i, i));
            name = name + "A";
        }
        //should return empty list
        assertEquals(new ArrayList<Integer>(), Solution.hottestPlaylistsOnTechnify());
        //add songs 1,3,5 to playlist 1
        assertOK(Solution.addSongToPlaylist(1,1));
        assertOK(Solution.addSongToPlaylist(3,1));
        assertOK(Solution.addSongToPlaylist(5,1));
        //add songs 2,4 to playlist 2
        assertOK(Solution.addSongToPlaylist(2,2));
        assertOK(Solution.addSongToPlaylist(4,2));
        //same average should return 1 and then 2 /same average, ascending order
        assertEquals(new ArrayList<Integer>(){{
            add(1);
            add(2);
        }}, Solution.hottestPlaylistsOnTechnify());
        //add song 10 to playlist 4
        assertOK(Solution.addSongToPlaylist(10,4));
        assertEquals(new ArrayList<Integer>(){{
            add(4);
            add(1);
            add(2);
        }}, Solution.hottestPlaylistsOnTechnify());
        //add songs 9,11 to playlist 3
        assertOK(Solution.addSongToPlaylist(9,3));
        assertOK(Solution.addSongToPlaylist(11,3));
        // now 3 and 4 has same rating
        assertEquals(new ArrayList<Integer>(){{
            add(3);
            add(4);
            add(1);
            add(2);
        }}, Solution.hottestPlaylistsOnTechnify());
        //add counts to song 2 of playlist 2
        assertOK(Solution.songPlay(2,2));
        //now playlist 2 has more rating than 1
        assertEquals(new ArrayList<Integer>(){{
            add(3);
            add(4);
            add(2);
            add(1);
        }}, Solution.hottestPlaylistsOnTechnify());
        //add songs 11-19 to playlist 11-20
        for (int i=11 ; i<20 ; i++){
            assertOK(Solution.addSongToPlaylist(i,i));
        }
        //check if gets only 10
        assertEquals(new ArrayList<Integer>(){{
            add(19);
            add(18);
            add(17);
            add(16);
            add(15);
            add(14);
            add(13);
            add(12);
            add(11);
            add(3); // Here was 10->3 since 10 has no songs and 3 has 20 plays / 2 songs = 10
        }}, Solution.hottestPlaylistsOnTechnify());
        Solution.dropTables();
    }

    @Test
    public void getSimilarUsersTest() {
        Solution.createTables();
        Playlist[] playLists = new Playlist[20];
        User[] user = new User[20];

        assertEquals(new ArrayList<Integer>(), Solution.getSimilarUsers(1));
        String desc = "d";
        String gen = "g";
        for (int i = 1; i < playLists.length; ++i) {
            playLists[i] = createPlayList(i, gen, desc);
            assertOK(Solution.addPlaylist(playLists[i]));
            desc = desc + "d";
        }
        String name = "A";
        for (int i = 1; i < user.length; ++i) {
            user[i] = createUser(i, name, "JAPAN", false);
            assertOK(Solution.addUser(user[i]));
            name = name + "A";
        }
        //users 1-14 follow playlist 1
        for (int i =1 ; i <=14 ; i++){//Here was typo 12->14
            assertOK(Solution.followPlaylist(i,1));
        }
        //should get first 10 in ascending order without 1
        assertEquals(new ArrayList<Integer>(){{
            add(2);
            add(3);
            add(4);
            add(5);
            add(6);
            add(7);
            add(8);
            add(9);
            add(10);
            add(11);
        }}, Solution.getSimilarUsers(1));
        //only even 1-14 also follow playlist 2
        for (int i =1 ; i <=14 ; i+=2){//Here was typo 12->14
            assertOK(Solution.followPlaylist(i,2)); //Here was typo 1->2
        }
        //should get now only odd from 1-14
        assertEquals(new ArrayList<Integer>(){{
            add(3);
            add(5);
            add(7);
            add(9);
            add(11);
            add(13);
        }}, Solution.getSimilarUsers(1));
        //same different user
        assertEquals(new ArrayList<Integer>(){{
            add(1);
            add(3);
            add(5);
            add(9);
            add(11);
            add(13);
        }}, Solution.getSimilarUsers(7));
        Solution.dropTables();
    }

    @Test
    public void getPlaylistRecTest() {
        Solution.createTables();
        Playlist[] playLists = new Playlist[20];
        User[] user = new User[20];

        String desc = "d";
        String gen = "g";
        for (int i = 1; i < playLists.length; ++i) {
            playLists[i] = createPlayList(i, gen, desc);
            assertOK(Solution.addPlaylist(playLists[i]));
            desc = desc + "d";
        }
        String name = "A";
        for (int i = 1; i < user.length; ++i) {
            user[i] = createUser(i, name, "JAPAN", false);
            assertOK(Solution.addUser(user[i]));
            name = name + "A";
        }
        //users 1-10 follow playlist 1,2,3,4,5
        for (int i = 1; i <= 10; i++) {
            assertOK(Solution.followPlaylist(i, 1));
            assertOK(Solution.followPlaylist(i, 2));
            assertOK(Solution.followPlaylist(i, 3));
            assertOK(Solution.followPlaylist(i, 4));
            assertOK(Solution.followPlaylist(i, 5));
        }
        //users 2,3,4 follow playlist 7
        assertOK(Solution.followPlaylist(2, 7));
        assertOK(Solution.followPlaylist(3, 7));
        assertOK(Solution.followPlaylist(4, 7));
        //users 5,6 follow playlist 8
        assertOK(Solution.followPlaylist(5, 8));
        assertOK(Solution.followPlaylist(6, 8));
        //user 7 follow playlist 9
        assertOK(Solution.followPlaylist(7, 9));
        //user 8 follow playlist 6
        assertOK(Solution.followPlaylist(8, 6));
        /*users 2-10 are similar to 1 (2-8 follow 5/6, 9-10 follows 100%)
        than 6,7,8,9 not followed by user 1, and should return
        7,8,6,9
        * */
        assertEquals(new ArrayList<Integer>(){{
            add(7);
            add(8);
            add(6);
            add(9);
        }}, Solution.getPlaylistRecommendation(1));
        //make 9,10 follow 10,11
        assertOK(Solution.followPlaylist(9, 10));
        assertOK(Solution.followPlaylist(10, 11));
        //check if get only 5
        assertEquals(new ArrayList<Integer>(){{
            add(7);
            add(8);
            add(6);
            add(9);
            add(10);
        }}, Solution.getPlaylistRecommendation(1));
        Solution.dropTables();
    }

    @Test
    public void getTopCountryTest() {
        Solution.createTables();
        Playlist[] playLists = new Playlist[30];
        User user;
        Song[] songs = new Song[30];

        String desc = "d";
        String gen = "g";
        for (int i = 1; i < playLists.length; ++i) {
            playLists[i] = createPlayList(i, gen, desc);
            assertOK(Solution.addPlaylist(playLists[i]));
            desc = desc + "d";
        }
        String name = "A";
        user = createUser(1, name, "JAPAN", false);
        assertOK(Solution.addUser(user));
        name = "B";
        for (int i = 1; i < 10; ++i) {
            songs[i] = createSong(i, name, gen, "USA", 0);
            assertOK(Solution.addSong(songs[i]));
            assertOK(Solution.songPlay(i, i+100));
            name = name + "B";
        }
        for (int i = 10; i < songs.length; ++i) {
            songs[i] = createSong(i, name, gen, "JAPAN", 0);
            assertOK(Solution.addSong(songs[i]));
            assertOK(Solution.songPlay(i, i));
            assertOK(Solution.addSongToPlaylist(i,i));
            name = name + "B";
        }
        //should fail no premium
        assertEquals(new ArrayList<Integer>(), Solution.getTopCountryPlaylists(1));

        assertOK(Solution.updateUserPremium(1));
        /*songs 10-29 belongs to playlist 10-29 with counts 10-29 and
        * country japan like user
        * should get 20-29  */
        assertEquals(new ArrayList<Integer>(){{
            add(29);
            add(28);
            add(27);
            add(26);
            add(25);
            add(24);
            add(23);
            add(22);
            add(21);
            add(20);
        }}, Solution.getTopCountryPlaylists(1));

        //revert 20-25 by making counts equal
        assertOK(Solution.songPlay(20, 5));
        assertOK(Solution.songPlay(21, 4));
        assertOK(Solution.songPlay(22, 3));
        assertOK(Solution.songPlay(23, 2));
        assertOK(Solution.songPlay(24, 1));
        assertEquals(new ArrayList<Integer>(){{
            add(29);
            add(28);
            add(27);
            add(26);
            add(20);
            add(21);
            add(22);
            add(23);
            add(24);
            add(25);
        }}, Solution.getTopCountryPlaylists(1));
        //make 26 first
        assertOK(Solution.songPlay(26,100));
        assertEquals(new ArrayList<Integer>(){{
            add(26);
            add(29);
            add(28);
            add(27);
            add(20);
            add(21);
            add(22);
            add(23);
            add(24);
            add(25);
        }}, Solution.getTopCountryPlaylists(1));
        Solution.dropTables();
    }

    @Test
    public void getTopGenreTest() {
        Solution.createTables();
        Playlist playlist1, playlist2;
        User user1, user2;
        Song[] songs = new Song[20];

        playlist1 = createPlayList(1,"POP","a"); // Here was type I -> POP
        playlist2 = createPlayList(2,"ROCK","a");
        user1 = createUser(1, "user1", "JAPAN", false);
        user2 = createUser(2, "user2", "JAPAN", false);
        assertOK(Solution.addUser(user1));
        assertOK(Solution.addUser(user2));
        assertOK(Solution.addPlaylist(playlist1)); // Here was missing line
        assertOK(Solution.addPlaylist(playlist2)); // Here was missing line
        //user1 follow playlist 1 , user2 follow playlist 2
        assertOK(Solution.followPlaylist(1,1));
        assertOK(Solution.followPlaylist(2,2));
        String name = "B";
        for (int i = 1; i < songs.length; ++i) {
            songs[i] = createSong(i, name, "POP", "USA", 0);
            assertOK(Solution.addSong(songs[i]));
            assertOK(Solution.songPlay(i, i));
            assertOK(Solution.addSongToPlaylist(i,1));
            name = name + "B";
        }
        //all songs belong to playlist 1 followed by user 1

        //should return empty
        assertEquals(new ArrayList<Integer>(), Solution.getSongsRecommendationByGenre(1, "POP"));

        //should return 10-19 descending, user2 follow empty playlist thus all songs not belong
        assertEquals(new ArrayList<Integer>(){{
            add(19);
            add(18);
            add(17);
            add(16);
            add(15);
            add(14);
            add(13);
            add(12);
            add(11);
            add(10);
        }}, Solution.getSongsRecommendationByGenre(2, "POP"));
        Solution.dropTables();
    }

}
