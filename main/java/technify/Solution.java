package technify;

import javafx.util.Pair;
import technify.business.Playlist;
import technify.business.ReturnValue;
import technify.business.Song;
import technify.business.User;
import technify.data.DBConnector;
import technify.data.PostgreSQLErrorCodes;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;

import static technify.business.ReturnValue.*;

public class Solution {

    public static void createTables() {
        createUsersTable();
        createSongsTable();
        createPlayListsTable();
        createBelongsTable();
        createFollowsTable();

        createSongInPlayListView();
        createPlayListStatisticsView();
        createSimilarUsersView();
    }


    public static void clearTables() {
        clearTable(FOLLOWS_TABLE_NAME);
        clearTable(BELONGS_TABLE_NAME);
        clearTable(PLAYLIST_TABLE_NAME);
        clearTable(SONG_TABLE_NAME);
        clearTable(USER_TABLE_NAME);
    }


    public static void dropTables() {
        dropView(SIMILAR_USERS_VIEW_NAME);
        dropView(PLAY_LIST_STATISTICS_VIEW_NAME);
        dropView(SONG_IN_PLAYLIST_VIEW_NAME);

        dropTable(FOLLOWS_TABLE_NAME);
        dropTable(BELONGS_TABLE_NAME);
        dropTable(PLAYLIST_TABLE_NAME);
        dropTable(SONG_TABLE_NAME);
        dropTable(USER_TABLE_NAME);
    }

    // TODO: unite code with other add functions?
    public static ReturnValue addUser(User user)
    {
        ReturnValue returnValue = OK;

        try {
            Solution.insert(
                    USER_TABLE_NAME,
                    user.getId(),
                    user.getName(),
                    user.getCountry(),
                    user.getPremium()
            );
        } catch (SQLException e) {
            returnValue = getInsertReturnValue(e);
        }

        return returnValue;

    }

    // TODO: unite code with other get functions?
    public static User getUserProfile(Integer userId) {
        User user = User.badUser();

        Object[] row = selectSingleRow(
                USER_TABLE_NAME,
                new String[]{
                        USER_ID_COLUMN_NAME,
                        USER_NAME_COLUMN_NAME,
                        USER_COUNTRY_COLUMN_NAME,
                        USER_PREMIUM_COLUMN_NAME,
                },
                USER_ID_COLUMN_NAME,
                userId
        );

        if (null != row) {
            user.setId((Integer) row[0]);
            user.setName((String) row[1]);
            user.setCountry((String) row[2]);
            user.setPremium((Boolean) row[3]);
        }

        return user;
    }

    public static ReturnValue deleteUser(User user)
    {
        return deleteAndReturnValue(
                USER_TABLE_NAME,
                new Pair<>(USER_ID_COLUMN_NAME, user.getId())
        );
    }

    public static ReturnValue updateUserPremium(Integer userId)
    {
        return changeUserPremium(userId, true);
    }

    public static ReturnValue updateUserNotPremium(Integer userId)
    {
        return changeUserPremium(userId, false);
    }

    // TODO: unite code with other add functions?
    public static ReturnValue addSong(Song song)
    {
        ReturnValue return_value = OK;

        try {
            Solution.insert(
                    SONG_TABLE_NAME,
                    song.getId(),
                    song.getName(),
                    song.getGenre(),
                    song.getCountry()
            );
        } catch (SQLException e) {
            return_value = getInsertReturnValue(e);
        }

        return return_value;
    }

    // TODO: unite code with other get functions?
    public static Song getSong(Integer songId)
    {
        Song song = Song.badSong();

        Object[] row = selectSingleRow(
                SONG_TABLE_NAME,
                new String[]{
                        SONG_ID_COLUMN_NAME,
                        SONG_NAME_COLUMN_NAME,
                        SONG_GENRE_COLUMN_NAME,
                        SONG_COUNTRY_COLUMN_NAME,
                        SONG_PLAY_COUNT_COLUMN_NAME,
                },
                SONG_ID_COLUMN_NAME,
                songId
        );

        if (null != row) {
            song.setId((Integer) row[0]);
            song.setName((String) row[1]);
            song.setGenre((String) row[2]);
            song.setCountry((String) row[3]);
            song.setPlayCount((Integer) row[4]);
        }

        return song;
    }

    public static ReturnValue deleteSong(Song song)
    {
        return deleteAndReturnValue(
                SONG_TABLE_NAME,
                new Pair<>(SONG_ID_COLUMN_NAME, song.getId())
        );
    }

    public static ReturnValue updateSongName(Song song)
    {
        Integer numberOfRenamedSongs = null;
        SQLException exception = null;

        try {
            numberOfRenamedSongs = update(
                    SONG_TABLE_NAME,
                    SONG_ID_COLUMN_NAME, song.getId(),
                    SONG_NAME_COLUMN_NAME, song.getName()
            );
        } catch (SQLException e) {
            exception = e;
        }

        return getUpdateAndDeleteReturnValue(numberOfRenamedSongs, exception);
    }

    // TODO: unite code with other add functions?
    public static ReturnValue addPlaylist(Playlist playlist)
    {
        ReturnValue returnValue = OK;

        try {
            Solution.insert(
                    PLAYLIST_TABLE_NAME,
                    playlist.getId(),
                    playlist.getGenre(),
                    playlist.getDescription()
            );
        } catch (SQLException e) {
            returnValue = getInsertReturnValue(e);
        }

        return returnValue;
    }

    public static Playlist getPlaylist(Integer playlistId)
    {
        Playlist playlist = Playlist.badPlaylist();

        Object[] row = selectSingleRow(
                PLAYLIST_TABLE_NAME,
                new String[]{
                        PLAYLIST_ID_COLUMN_NAME,
                        PLAYLIST_GENRE_COLUMN_NAME,
                        PLAYLIST_DESCRIPTION_COLUMN_NAME,
                },
                PLAYLIST_ID_COLUMN_NAME,
                playlistId
        );

        if (null != row) {
            playlist.setId((Integer) row[0]);
            playlist.setGenre((String) row[1]);
            playlist.setDescription((String) row[2]);
        }

        return playlist;
    }

    public static ReturnValue deletePlaylist(Playlist playlist)
    {
        return deleteAndReturnValue(
                PLAYLIST_TABLE_NAME,
                new Pair<>(PLAYLIST_ID_COLUMN_NAME, playlist.getId())
        );
    }

    public static ReturnValue updatePlaylist(Playlist playlist)
    {
        Integer numberOfChangedPlayLists = null;
        SQLException exception = null;

        try {
            numberOfChangedPlayLists = update(
                    PLAYLIST_TABLE_NAME,
                    // Ta told id only
                    PLAYLIST_ID_COLUMN_NAME, playlist.getId(),
                    // TA told description only
                    PLAYLIST_DESCRIPTION_COLUMN_NAME, playlist.getDescription()
            );



        } catch (SQLException e) {
            exception = e;
        }

        return getUpdateAndDeleteReturnValue(numberOfChangedPlayLists, exception);
    }

    public static ReturnValue addSongToPlaylist(Integer songid, Integer playlistId) {
        ReturnValue returnValue = OK;
        String sql = String.format(
                "INSERT INTO %s \n" +
                        "SELECT ?, ? \n" + // Add genre if you want createBelowsTable automatically check genre
                        "FROM %s, %s\n" +
                        "WHERE %s.%s=?" +
                        "AND %s.%s=?" +
                        "AND %s.%s=%s.%s",
                BELONGS_TABLE_NAME,
                SONG_TABLE_NAME, PLAYLIST_TABLE_NAME,
                SONG_TABLE_NAME, SONG_ID_COLUMN_NAME,
                PLAYLIST_TABLE_NAME, PLAYLIST_ID_COLUMN_NAME,
                SONG_TABLE_NAME, SONG_GENRE_COLUMN_NAME, PLAYLIST_TABLE_NAME, PLAYLIST_GENRE_COLUMN_NAME
        );
        Connection connection = DBConnector.getConnection();
        Integer numberOfInsertedRecords = null;
        SQLException exception = null;

        try {
            PreparedStatement pstmt = connection.prepareStatement(sql);
            pstmt.setObject(1, songid);
            pstmt.setObject(2, playlistId);
            pstmt.setObject(3, songid);
            pstmt.setObject(4, playlistId);
            numberOfInsertedRecords = pstmt.executeUpdate();
            assert numberOfInsertedRecords <= 1;
        } catch (SQLException e) {
            exception = e;
        }

        if (null != numberOfInsertedRecords && 0 == numberOfInsertedRecords) {
            returnValue = BAD_PARAMS;
        }
        else if (null != exception) {
            returnValue = getInsertReturnValue(exception);
        }

        return returnValue;
    }

    public static ReturnValue removeSongFromPlaylist(Integer songid, Integer playlistId){
        return deleteAndReturnValue(
                    BELONGS_TABLE_NAME,
                    new Pair<>(BELONGS_SONG_ID_COLUMN_NAME, songid),
                    new Pair<>(BELONGS_PLAYLIST_ID_COLUMN_NAME, playlistId)
        );
    }

    public static ReturnValue followPlaylist(Integer userId, Integer playlistId){
        ReturnValue returnValue = OK;

        try {
            insert(
                    FOLLOWS_TABLE_NAME,
                    userId, playlistId
            );
        } catch (SQLException e) {
            returnValue = getInsertReturnValue(e);

            // In case of null, checking primary key and receiving BAD_PARAMS instead of NOT_EXISTS
            if (BAD_PARAMS == returnValue) {
                assert null == userId || null == playlistId;
                returnValue = NOT_EXISTS;
            }
        }

        return returnValue;

    }

    public static ReturnValue stopFollowPlaylist(Integer userId, Integer playlistId){
        return deleteAndReturnValue(
                    FOLLOWS_TABLE_NAME,
                    new Pair<>(FOLLOWS_USER_ID_COLUMN_NAME, userId),
                    new Pair<>(FOLLOWS_PLAYLIST_ID_COLUMN_NAME, playlistId)
        );
    }

    public static ReturnValue songPlay(Integer songId, Integer times){
        Integer numberOfUpdatedSongs = null;
        SQLException exception = null;
        String sql = String.format(
                "UPDATE %s " +
                        "SET %s = %s + ? " +
                        "where %s = ?",
                SONG_TABLE_NAME,
                SONG_PLAY_COUNT_COLUMN_NAME, SONG_PLAY_COUNT_COLUMN_NAME,
                SONG_ID_COLUMN_NAME
        );

        try (Connection connection = DBConnector.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql)){
            pstmt.setObject(1, times);
            pstmt.setObject(2, songId);

            numberOfUpdatedSongs = pstmt.executeUpdate();
        }
        catch (SQLException e) {
            exception = e;
        }

        return getUpdateAndDeleteReturnValue(numberOfUpdatedSongs, exception);
    }

    public static Integer getPlaylistTotalPlayCount(Integer playlistId){
        // Abusing the function to sum single value
        return selectAndAggregate(
                SUM_AGGREGATOR,
                PLAY_LIST_STATISTICS_VIEW_NAME,
                PLAY_LIST_STATISTICS_PLAY_COUNT_COLUMN_NAME,
                PLAY_LIST_STATISTICS_ID_COLUMN_NAME, playlistId
        );
    }

    public static Integer getPlaylistFollowersCount(Integer playlistId){
        return selectAndAggregate(
                COUNT_AGGREGATOR,
                FOLLOWS_TABLE_NAME,
                FOLLOWS_USER_ID_COLUMN_NAME,
                FOLLOWS_PLAYLIST_ID_COLUMN_NAME, playlistId
        );
    }

    public static String getMostPopularSong(){
        return selectMost(
                SONG_IN_PLAYLIST_VIEW_NAME,
                String.format(
                        "%s.%s",
                        SONG_IN_PLAYLIST_VIEW_NAME,
                        SONG_IN_PLAYLIST_NAME_COLUMN_NAME
                ),
                String.format(
                        "%s, %s.%s",
                        SONG_IN_PLAYLIST_SONG_ID_COLUMN_NAME,
                        SONG_IN_PLAYLIST_VIEW_NAME, SONG_IN_PLAYLIST_NAME_COLUMN_NAME // Needed for select
                ),
                String.format(
                        "(%s(%s), %s) DESC",
                        COUNT_AGGREGATOR,
                        SONG_IN_PLAYLIST_SONG_ID_COLUMN_NAME,
                        SONG_IN_PLAYLIST_SONG_ID_COLUMN_NAME
                ),
                NO_SONGS_MESSAGE,
                true
        );
    }

    public static Integer getMostPopularPlaylist(){
        return selectMost(
                PLAY_LIST_STATISTICS_VIEW_NAME,
                PLAY_LIST_STATISTICS_ID_COLUMN_NAME,
                PLAY_LIST_STATISTICS_ID_COLUMN_NAME,
                String.format(
                        "(%s(%s), %s) DESC",
                        SUM_AGGREGATOR,
                        PLAY_LIST_STATISTICS_PLAY_COUNT_COLUMN_NAME,
                        PLAY_LIST_STATISTICS_ID_COLUMN_NAME
                ),
                0,
                false
            );

    }

    public static ArrayList<Integer> hottestPlaylistsOnTechnify(){
        return selectTop(
                SONG_IN_PLAYLIST_VIEW_NAME,
                SONG_IN_PLAYLIST_PLAYLIST_ID_COLUMN_NAME,
                null,
                SONG_IN_PLAYLIST_PLAYLIST_ID_COLUMN_NAME,
                null,
                String.format(
                        "(SUM(%s)/COUNT(%s), -%s) DESC",
                        SONG_IN_PLAYLIST_PLAY_COUNT_COLUMN_NAME,
                        SONG_IN_PLAYLIST_SONG_ID_COLUMN_NAME,
                        SONG_IN_PLAYLIST_PLAYLIST_ID_COLUMN_NAME
                ),
                10,
                false
        );
    }

    public static ArrayList<Integer> getSimilarUsers(Integer userId){
        return selectTop(
                SIMILAR_USERS_VIEW_NAME,
                SIMILAR_USERS_SECOND_ID,
                String.format("%s=?", SIMILAR_USERS_FIRST_ID),
                null,
                null,
                String.format("%s ASC", SIMILAR_USERS_SECOND_ID),
                10,
                false,
                userId
        );
    }

    public static ArrayList<Integer> getTopCountryPlaylists(Integer userId) {
        String isUserPremiumSQL = String.format(
                "SELECT %s=true \n" +
                "FROM %s \n" +
                "WHERE %s=?",
                USER_PREMIUM_COLUMN_NAME,
                USER_TABLE_NAME,
                USER_ID_COLUMN_NAME
        );
        String userCountrySQL = String.format(
                "SELECT %s \n" +
                "FROM %s \n" +
                "WHERE %s=?",
                USER_COUNTRY_COLUMN_NAME,
                USER_TABLE_NAME,
                USER_ID_COLUMN_NAME
        );
        String playListsWithSongsInUserCountrySQL = String.format(
                "SELECT %s \n" +
                "FROM %s \n" +
                "WHERE %s=(%s)",
                SONG_IN_PLAYLIST_PLAYLIST_ID_COLUMN_NAME,
                SONG_IN_PLAYLIST_VIEW_NAME,
                SONG_IN_PLAYLIST_COUNTRY_COLUMN_NAME, userCountrySQL
        );

        return selectTop(
                PLAY_LIST_STATISTICS_VIEW_NAME,
                PLAY_LIST_STATISTICS_ID_COLUMN_NAME,
                String.format(
                        "%s IN (%s) " +
                        "AND (%s) ",
                        PLAY_LIST_STATISTICS_ID_COLUMN_NAME, playListsWithSongsInUserCountrySQL,
                        isUserPremiumSQL
                ),
                null,
                null,
                String.format(
                        "(%s, -%s) DESC",
                        PLAY_LIST_STATISTICS_PLAY_COUNT_COLUMN_NAME, PLAY_LIST_STATISTICS_ID_COLUMN_NAME
                ),
                10,
                false,
                userId,
                userId
        );
    }

    public static ArrayList<Integer> getPlaylistRecommendation (Integer userId){
        String similarUsersSQL = String.format(
                "SELECT %s \n" +
                "FROM %s \n" +
                "WHERE %s.%s=?",
                SIMILAR_USERS_SECOND_ID,
                SIMILAR_USERS_VIEW_NAME,
                SIMILAR_USERS_VIEW_NAME, SIMILAR_USERS_FIRST_ID
        );
        String userPlayListsSQL = String.format(
                "SELECT %s \n" +
                "FROM %s " +
                "WHERE %s.%s=?",
                FOLLOWS_PLAYLIST_ID_COLUMN_NAME,
                FOLLOWS_TABLE_NAME,
                FOLLOWS_TABLE_NAME, FOLLOWS_USER_ID_COLUMN_NAME
        );

        return selectTop(
                FOLLOWS_TABLE_NAME,
                FOLLOWS_PLAYLIST_ID_COLUMN_NAME,
                String.format(
                        "%s IN (%s)",
                        FOLLOWS_USER_ID_COLUMN_NAME,
                        similarUsersSQL
                ),
                FOLLOWS_PLAYLIST_ID_COLUMN_NAME,
                String.format(
                        "%s NOT IN (%s)",
                        FOLLOWS_PLAYLIST_ID_COLUMN_NAME,
                        userPlayListsSQL
                ),
                String.format(
                        "(COUNT(%s), -%s) DESC",
                        FOLLOWS_USER_ID_COLUMN_NAME,
                        FOLLOWS_PLAYLIST_ID_COLUMN_NAME
                ),
                5,
                false,
                userId,
                userId
        );
    }

    public static ArrayList<Integer> getSongsRecommendationByGenre(Integer userId, String genre){
        String songsInPlayListsUserFollowsSQL = String.format(
                "SELECT %s.%s \n" +
                "FROM %s \n" +
                "JOIN %s ON %s.%s=%s.%s \n" +
                "WHERE %s.%s=?",
                BELONGS_TABLE_NAME, BELONGS_SONG_ID_COLUMN_NAME,
                BELONGS_TABLE_NAME,
                FOLLOWS_TABLE_NAME, FOLLOWS_TABLE_NAME, FOLLOWS_PLAYLIST_ID_COLUMN_NAME, BELONGS_TABLE_NAME, BELONGS_PLAYLIST_ID_COLUMN_NAME,
                FOLLOWS_TABLE_NAME, FOLLOWS_USER_ID_COLUMN_NAME
        );

        return selectTop(
                SONG_TABLE_NAME,
                SONG_ID_COLUMN_NAME,
                String.format(
                        "%s = ?" +
                        "AND %s NOT IN (%s)",
                        SONG_GENRE_COLUMN_NAME,
                        SONG_ID_COLUMN_NAME, songsInPlayListsUserFollowsSQL
                ),
                null,
                null,
                String.format(
                        "(%s, -%s) DESC",
                        SONG_PLAY_COUNT_COLUMN_NAME, SONG_ID_COLUMN_NAME
                ),
                10,
                false,
                genre,
                userId
        );
    }

    private static final String USER_TABLE_NAME = "users";
    private static final String SONG_TABLE_NAME = "songs";
    private static final String PLAYLIST_TABLE_NAME = "playlists";
    private static final String BELONGS_TABLE_NAME = "belongs";
    private static final String FOLLOWS_TABLE_NAME = "follows";

    private static final String SONG_IN_PLAYLIST_VIEW_NAME = "song_in_playlist";
    private static final String SIMILAR_USERS_VIEW_NAME = "similar_users";

    // TODO: consider converting to Enums
    private static final String USER_ID_COLUMN_NAME = "id";
    private static final String USER_NAME_COLUMN_NAME = "name";
    private static final String USER_COUNTRY_COLUMN_NAME = "country";
    private static final String USER_PREMIUM_COLUMN_NAME = "premium";

    // TODO: consider converting to Enums
    private static final String SONG_ID_COLUMN_NAME = "id";
    private static final String SONG_NAME_COLUMN_NAME = "name";
    private static final String SONG_GENRE_COLUMN_NAME = "genre";
    private static final String SONG_COUNTRY_COLUMN_NAME = "country";
    private static final String SONG_PLAY_COUNT_COLUMN_NAME = "play_count";

    // TODO: consider converting to Enums
    private static final String PLAYLIST_ID_COLUMN_NAME = "id";
    private static final String PLAYLIST_GENRE_COLUMN_NAME = "genre";
    private static final String PLAYLIST_DESCRIPTION_COLUMN_NAME = "description";

    // TODO: consider converting to Enums
    private static final String BELONGS_SONG_ID_COLUMN_NAME = "song_id";
    private static final String BELONGS_PLAYLIST_ID_COLUMN_NAME = "playlist_id";
//    private static final String BELONGS_GENRE_COLUMN_NAME = "genre"; Uncomment it if you want to make createBelowsTable automatically check genre

    // TODO: consider converting to Enums
    private static final String FOLLOWS_USER_ID_COLUMN_NAME = "user_id";
    private static final String FOLLOWS_PLAYLIST_ID_COLUMN_NAME = "playlist_id";

    // TODO: consider converting to Enums
    private static final String SONG_IN_PLAYLIST_PLAY_COUNT_COLUMN_NAME = "play_count";
    private static final String SONG_IN_PLAYLIST_PLAYLIST_ID_COLUMN_NAME = "playlist_id";
    private static final String SONG_IN_PLAYLIST_NAME_COLUMN_NAME = "name";
    private static final String SONG_IN_PLAYLIST_SONG_ID_COLUMN_NAME = "song_id";
    private static final String SONG_IN_PLAYLIST_COUNTRY_COLUMN_NAME = "country";
    private static final String SONG_IN_PLAYLIST_GENRE_COLUMN_NAME = "genre";

    private static final String PLAY_LIST_STATISTICS_VIEW_NAME = "playlist_statistics";
    private static final String PLAY_LIST_STATISTICS_ID_COLUMN_NAME = "id";
    private static final String PLAY_LIST_STATISTICS_PLAY_COUNT_COLUMN_NAME = "play_count";

    private static final String SIMILAR_USERS_FIRST_ID = "user_1_id";
    private static final String SIMILAR_USERS_SECOND_ID = "user_2_id";

    private static final String SUM_AGGREGATOR = "SUM";
    private static final String COUNT_AGGREGATOR = "COUNT";

    private static final String NO_SONGS_MESSAGE = "No songs";

    private static void insert(String tableName, Object... arguments) throws SQLException {
        String[] question_marks = new String[arguments.length];
        Arrays.fill(question_marks, "?");

        try(Connection connection = DBConnector.getConnection();
            PreparedStatement pstmt = connection.prepareStatement(
                    "INSERT INTO " + tableName +
                        " VALUES (" + String.join(", ",question_marks) + ")")) {



            for (int index = 0; index < arguments.length; ++index) {
                pstmt.setObject(1 + index, arguments[index]);
            }

            pstmt.execute();

        }
    }

    private static ArrayList<Object[]> select(
            String tableName,
            String []columnNames,
            String fieldName, Object fieldValue
    ) throws SQLException {
        try(Connection connection = DBConnector.getConnection();
            PreparedStatement pstmt = connection.prepareStatement("SELECT " + String.join(", ", columnNames)+ " FROM " + tableName +
                " WHERE " + fieldName + " = " + fieldValue)) {
            ResultSet results = pstmt.executeQuery();
            ArrayList<Object[]> objects = new ArrayList<>();

            while (results.next()) {
                Object[] row = new Object[columnNames.length];
                for (int column_index = 0; column_index < columnNames.length; ++column_index) {
                    row[column_index] = results.getObject(1 + column_index);
                }
                objects.add(row);
            }

            //DBConnector.printResults(results);
            results.close();

            return objects;

        }
    }

    private static Object[] selectSingleRow(
            String tableName,
            String[] columnNames,
            String fieldName, Object fieldValue
    ) {
        Object[] row = null;

        try {
            ArrayList<Object[]> objects = Solution.select(
                    tableName,
                    columnNames,
                    fieldName,
                    fieldValue
            );
            assert objects.size() <= 1;

            if (1 == objects.size()) {
                row = objects.get(0);
                assert (int) row[0] == (int)fieldValue;
            }
        } catch (SQLException e) {
            //e.printStackTrace();
        }

        return row;
    }

    private static Integer selectAndAggregate(
            String aggregator,
            String tableName,
            String aggregatingFieldName,
            String whereName, Object whereValue
    ) {
        String sql = String.format(
                "SELECT %s(%s) \n" +
                        "FROM %s \n" +
                        "WHERE %s=?",
                aggregator,
                aggregatingFieldName,
                tableName,
                whereName
        );

        try(Connection connection = DBConnector.getConnection();
            PreparedStatement pstmt = connection.prepareStatement(sql)){
            pstmt.setObject(1, whereValue);
            ResultSet results = pstmt.executeQuery();
            results.next();
            assert results.isLast();
            Integer returnValue = results.getInt(1);
            results.close();

            return returnValue;

        } catch (SQLException e) {
            //e.printStackTrace();
        }

        return 0;
    }

    private static <T> T selectMost(
            String tableName,
            String fieldName,
            String groupBy,
            String orderBy,
            T emptyResult,
            boolean returnNullInCaseOfFailure
    ){
        ArrayList<T> singleton = selectTop(
                tableName,
                fieldName,
                null,
                groupBy,
                null,
                orderBy,
                1,
                returnNullInCaseOfFailure
        );
        T returnValue = null;

        if (null != singleton) {
            if (singleton.isEmpty()) {
                returnValue = emptyResult;
            } else {
                returnValue = singleton.get(0);
            }
        }

        return returnValue;
    }


    private static <T> ArrayList<T> selectTop(
            String tableName,
            String fieldName,
            String where,
            String groupBy,
            String having,
            String orderBy,
            Integer limit,
            boolean returnNullInCaseOfFailure,
            Object... arguments
    ){
        ArrayList<T> returnValue = new ArrayList<>();
        String sql = String.format(
                "SELECT %s \n" +
                        "FROM %s \n" +
                        (null == where ? "" : String.format("WHERE %s \n", where)) +
                        (null == groupBy ? "" : String.format("GROUP BY %s \n", groupBy)) +
                        (null == having ? "" : String.format("HAVING %s \n", having)) +
                        "ORDER BY %s \n" +
                        "LIMIT %d",
                fieldName,
                tableName,
                orderBy,
                limit
        );

        try(Connection connection = DBConnector.getConnection();
            PreparedStatement pstmt = connection.prepareStatement(sql)) {
            for (int i = 0; i < arguments.length; ++i) {
                pstmt.setObject(1 + i, arguments[i]);
            }

            ResultSet results = pstmt.executeQuery();

            while (results.next()) {
                Object current = results.getObject(1);
                returnValue.add((T)current);
            }

            results.close();

        }
        catch (SQLException e) {
            if (returnNullInCaseOfFailure) {
                returnValue = null;
            }
        }

        return returnValue;
    }

    @SafeVarargs
    private static ReturnValue deleteAndReturnValue(String tableName, Pair < String, Object >... columnNameToColumnValue) {
        Integer numberOfDeletedObjects = null;
        SQLException exception = null;

        try {
            numberOfDeletedObjects = delete(
                    tableName,
                    // TA told id only
                    columnNameToColumnValue
            );
        } catch (SQLException e) {
            exception = e;
        }

        return getUpdateAndDeleteReturnValue(numberOfDeletedObjects, exception);
    }

    @SafeVarargs
    private static Integer delete(String tableName, Pair< String, Object >... fieldNamesAndValues) throws SQLException {
        String[] conditions = new String[fieldNamesAndValues.length];

        for (int index = 0; index < fieldNamesAndValues.length; ++index) {
            conditions[index] = String.format("%s=?", fieldNamesAndValues[index].getKey());
        }

        String sql = String.format(
                "DELETE FROM %s " +
                        "WHERE %s ",
                tableName,
                String.join(" AND ", conditions)
        );

        try(Connection connection = DBConnector.getConnection();
            PreparedStatement pstmt = connection.prepareStatement(sql)
        ) {
            for (int index = 0; index < fieldNamesAndValues.length; ++index) {
                pstmt.setObject(1 + index, fieldNamesAndValues[index].getValue());
            }

            return pstmt.executeUpdate();
        }
    }

    /**
     * Execure sql and ignore all exceptions
     *
     * @param sql - query to execute
     */
    private static void execute(String sql) {
        try(Connection connection = DBConnector.getConnection();
            PreparedStatement pstmt = connection.prepareStatement(sql)){
            pstmt.execute();
        } catch (SQLException e) {
            //e.printStackTrace();
        }
    }

    private static int update(
            String tableName,
            String whereFieldName, Object whereFieldValue,
            String setFieldName, Object setFieldValue
    ) throws SQLException {
        String sql = String.format(
                "UPDATE %s " +
                        "SET %s = ? " +
                        "where %s = ?",
                tableName,
                setFieldName,
                whereFieldName
        );

        try(Connection connection = DBConnector.getConnection();
            PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setObject(1,setFieldValue);
            pstmt.setObject(2, whereFieldValue);
            return pstmt.executeUpdate();
        }
    }

    private static void createUsersTable() {
        String sql = String.format(
                "CREATE TABLE %s\n" +
                "(\n" +
                    "%s integer,\n" +
                    "%s text NOT NULL,\n" +
                    "%s text NOT NULL,\n" +
                    "%s boolean NOT NULL,\n" +
                    "PRIMARY KEY (%s),\n" +
                    "CHECK (%s > 0)\n" +
                ")",
                USER_TABLE_NAME,
                USER_ID_COLUMN_NAME,
                USER_NAME_COLUMN_NAME,
                USER_COUNTRY_COLUMN_NAME,
                USER_PREMIUM_COLUMN_NAME,
                USER_ID_COLUMN_NAME,
                USER_ID_COLUMN_NAME
        );
        execute(sql);
    }

    private static void createSongsTable() {
        String sql = String.format("CREATE TABLE %s\n" +
                        "(\n" +
                        "%s integer,\n" +
                        "%s text NOT NULL,\n" +
                        "%s text NOT NULL,\n" +
                        "%s text,\n" + // Country could be null
                        "%s integer DEFAULT(0),\n" +
                        "PRIMARY KEY (%s)," +
                        // "UNIQUE (%s,%s),\n" + Uncomment it if you want to make createBelowsTable automatically check genre
                        "CHECK (%s > 0),\n" +
                        "CHECK (%s >= 0)\n" +
                        ")",
                SONG_TABLE_NAME,
                SONG_ID_COLUMN_NAME,
                SONG_NAME_COLUMN_NAME,
                SONG_GENRE_COLUMN_NAME,
                SONG_COUNTRY_COLUMN_NAME,
                SONG_PLAY_COUNT_COLUMN_NAME,
                SONG_ID_COLUMN_NAME,
//                SONG_ID_COLUMN_NAME, SONG_GENRE_COLUMN_NAME, Uncomment it if you want to make createBelowsTable automatically check genre
                SONG_ID_COLUMN_NAME,
                SONG_PLAY_COUNT_COLUMN_NAME
        );
        execute(sql);

    }

    private static void createPlayListsTable() {
        String sql = String.format("CREATE TABLE %s\n" +
                "(\n" +
                "%s integer,\n" +
                "%s text NOT NULL,\n" +
                "%s text NOT NULL,\n" +
                "PRIMARY KEY (%s),\n" +
//                "UNIQUE (%s,%s),\n" + Uncomment it if you want to make createBelowsTable automatically check genre
                "CHECK (%s > 0)\n" +
                ")",
                PLAYLIST_TABLE_NAME,
                PLAYLIST_ID_COLUMN_NAME,
                PLAYLIST_GENRE_COLUMN_NAME,
                PLAYLIST_DESCRIPTION_COLUMN_NAME,
                PLAYLIST_ID_COLUMN_NAME,
//                PLAYLIST_ID_COLUMN_NAME, PLAYLIST_GENRE_COLUMN_NAME,  Uncomment it if you want to make createBelowsTable automatically check genre
                PLAYLIST_ID_COLUMN_NAME
        );
        execute(sql);
    }

    private static void createBelongsTable() {
        String sql = String.format(
                "CREATE TABLE %s\n" +
                "(\n" +
                    "%s integer,\n" +
                    "%s integer,\n" +
//                    "%s text,\n" + Uncomment it if you want to make createBelowsTable automatically check genre
                    "PRIMARY KEY (%s,%s),\n" +
                    "FOREIGN KEY (%s) REFERENCES %s(%s) ON DELETE CASCADE,\n" +
                    "FOREIGN KEY (%s) REFERENCES %s(%s) ON DELETE CASCADE\n" +
//                    "FOREIGN KEY (%s,%s) REFERENCES %s(%s,%s) ON DELETE CASCADE,\n" + Uncomment it if you want to make createBelowsTable automatically check genre
//                    "FOREIGN KEY (%s,%s) REFERENCES %s(%s,%s) ON DELETE CASCADE\n" + Uncomment it if you want to make createBelowsTable automatically check genre
                ")",
                BELONGS_TABLE_NAME,
                BELONGS_SONG_ID_COLUMN_NAME,
                BELONGS_PLAYLIST_ID_COLUMN_NAME,
//                BELONGS_GENRE_COLUMN_NAME, Uncomment it if you want to make createBelowsTable automatically check genre
                BELONGS_SONG_ID_COLUMN_NAME, BELONGS_PLAYLIST_ID_COLUMN_NAME,
                BELONGS_SONG_ID_COLUMN_NAME, SONG_TABLE_NAME, SONG_ID_COLUMN_NAME,
                BELONGS_PLAYLIST_ID_COLUMN_NAME, PLAYLIST_TABLE_NAME, PLAYLIST_ID_COLUMN_NAME
//                BELONGS_SONG_ID_COLUMN_NAME, BELONGS_GENRE_COLUMN_NAME, SONG_TABLE_NAME, SONG_ID_COLUMN_NAME, SONG_GENRE_COLUMN_NAME, Uncomment it if you want to make createBelowsTable automatically check genre
//                BELONGS_PLAYLIST_ID_COLUMN_NAME, BELONGS_GENRE_COLUMN_NAME, PLAYLIST_TABLE_NAME, PLAYLIST_ID_COLUMN_NAME, PLAYLIST_GENRE_COLUMN_NAME Uncomment it if you want to make createBelowsTable automatically check genre
        );

        execute(sql);
    }

    private static void createFollowsTable() {
        String sql = String.format(
                "CREATE TABLE %s\n" +
                "(\n" +
                    "%s integer,\n" +
                    "%s integer,\n" +
                    "FOREIGN KEY (%s) REFERENCES %s(%s) ON DELETE CASCADE,\n" +
                    "FOREIGN KEY (%s) REFERENCES %s(%s) ON DELETE CASCADE,\n" +
                    "PRIMARY KEY (%s,%s)\n" +
                ")",
                FOLLOWS_TABLE_NAME,
                FOLLOWS_USER_ID_COLUMN_NAME,
                FOLLOWS_PLAYLIST_ID_COLUMN_NAME,
                FOLLOWS_USER_ID_COLUMN_NAME, USER_TABLE_NAME, USER_ID_COLUMN_NAME,
                FOLLOWS_PLAYLIST_ID_COLUMN_NAME, PLAYLIST_TABLE_NAME, PLAYLIST_ID_COLUMN_NAME,
                FOLLOWS_USER_ID_COLUMN_NAME, FOLLOWS_PLAYLIST_ID_COLUMN_NAME
        );

        execute(sql);
    }

    private static void createSongInPlayListView() {
        String sql = String.format(
                "CREATE VIEW %s AS \n" +
                "SELECT " +
                        "%s.%s AS %s, " +
                        "%s.%s AS %s, " +
                        "%s.%s AS %s, " +
                        "%s.%s AS %s, " +
                        "%s.%s AS %s, " +
                        "%s.%s AS %s " +
                "FROM %s, %s, %s \n" +
                "WHERE " +
                        "%s.%s=%s.%s " +
                        "AND %s.%s=%s.%s\n",
                SONG_IN_PLAYLIST_VIEW_NAME,

                SONG_TABLE_NAME, SONG_ID_COLUMN_NAME, SONG_IN_PLAYLIST_SONG_ID_COLUMN_NAME,
                SONG_TABLE_NAME, SONG_NAME_COLUMN_NAME, SONG_IN_PLAYLIST_NAME_COLUMN_NAME,
                SONG_TABLE_NAME, SONG_PLAY_COUNT_COLUMN_NAME, SONG_IN_PLAYLIST_PLAY_COUNT_COLUMN_NAME,
                SONG_TABLE_NAME, SONG_COUNTRY_COLUMN_NAME, SONG_IN_PLAYLIST_COUNTRY_COLUMN_NAME,
                SONG_TABLE_NAME, SONG_GENRE_COLUMN_NAME, SONG_IN_PLAYLIST_GENRE_COLUMN_NAME,
                PLAYLIST_TABLE_NAME, PLAYLIST_ID_COLUMN_NAME, SONG_IN_PLAYLIST_PLAYLIST_ID_COLUMN_NAME,

                SONG_TABLE_NAME, BELONGS_TABLE_NAME, PLAYLIST_TABLE_NAME,
                SONG_TABLE_NAME, SONG_ID_COLUMN_NAME, BELONGS_TABLE_NAME, BELONGS_SONG_ID_COLUMN_NAME,
                BELONGS_TABLE_NAME, BELONGS_PLAYLIST_ID_COLUMN_NAME, PLAYLIST_TABLE_NAME, PLAYLIST_ID_COLUMN_NAME
        );

        execute(sql);
    }

    private static void createPlayListStatisticsView() {
        String sql = String.format(
                "CREATE VIEW %s AS \n" +
                "SELECT " +
                        "%s.%s AS %s, " +
                        "COALESCE(SUM(%s.%s), 0) AS %s " +
                "FROM " +
                        "%s " +
                        "LEFT JOIN %s " +
                            "ON %s.%s " +
                            "= %s.%s " +
                "GROUP BY %s.%s",
                PLAY_LIST_STATISTICS_VIEW_NAME,
                PLAYLIST_TABLE_NAME, PLAYLIST_ID_COLUMN_NAME, PLAY_LIST_STATISTICS_ID_COLUMN_NAME,
                SONG_IN_PLAYLIST_VIEW_NAME, SONG_IN_PLAYLIST_PLAY_COUNT_COLUMN_NAME, PLAY_LIST_STATISTICS_PLAY_COUNT_COLUMN_NAME,
                PLAYLIST_TABLE_NAME,
                SONG_IN_PLAYLIST_VIEW_NAME,
                    PLAYLIST_TABLE_NAME, PLAYLIST_ID_COLUMN_NAME,
                    SONG_IN_PLAYLIST_VIEW_NAME, SONG_IN_PLAYLIST_PLAYLIST_ID_COLUMN_NAME,
                PLAYLIST_TABLE_NAME, PLAYLIST_ID_COLUMN_NAME
        );

        execute(sql);
    }

    private static void createSimilarUsersView() {
        // Should return 0 in case one of users is subscribed to nothing
        String numberOfCommonPlayListsSQL = String.format(
                "SELECT COUNT(*) \n" +
                "FROM %s Follows1 INNER JOIN %s Follows2 " +
                        "ON Follows1.%s=Follows2.%s " +
                        "AND Follows1.%s!=Follows2.%s \n" +
                "WHERE Follows1.%s=User1.%s " +
                        "AND Follows2.%s=User2.%s",
                FOLLOWS_TABLE_NAME, FOLLOWS_TABLE_NAME,
                    FOLLOWS_PLAYLIST_ID_COLUMN_NAME, FOLLOWS_PLAYLIST_ID_COLUMN_NAME,
                    FOLLOWS_USER_ID_COLUMN_NAME, FOLLOWS_USER_ID_COLUMN_NAME,
                FOLLOWS_USER_ID_COLUMN_NAME, USER_ID_COLUMN_NAME,
                FOLLOWS_USER_ID_COLUMN_NAME, USER_ID_COLUMN_NAME
        );
        String numberOfUser1PlayListsSQL = String.format(
                "SELECT COUNT(*) \n" +
                "FROM %s \n" +
                "WHERE %s.%s=User1.%s",
                FOLLOWS_TABLE_NAME,
                FOLLOWS_TABLE_NAME, FOLLOWS_USER_ID_COLUMN_NAME, USER_ID_COLUMN_NAME
        );
        String sql = String.format(
                "CREATE VIEW %s AS \n" +
                        "SELECT " +
                            "User1.%s AS %s, " +
                            "User2.%s AS %s " +
                        "FROM %s User1, %s User2 \n" +
                 "WHERE User1.%s != User2.%s AND " +
                        "100 * (%s) " +
                        ">=" +
                        " 75 * (%s) AND " +
                        "(%s) > 0"
                ,
                SIMILAR_USERS_VIEW_NAME,
                USER_ID_COLUMN_NAME, SIMILAR_USERS_FIRST_ID,
                USER_ID_COLUMN_NAME, SIMILAR_USERS_SECOND_ID,
                USER_TABLE_NAME, USER_TABLE_NAME,
                USER_ID_COLUMN_NAME, USER_ID_COLUMN_NAME,
                numberOfCommonPlayListsSQL,
                numberOfUser1PlayListsSQL,
                numberOfCommonPlayListsSQL
        );

        execute(sql);
    }

    private static ReturnValue changeUserPremium(Integer userId, boolean premium)
    {
        ReturnValue returnValue;
        User user = getUserProfile(userId);

        if (User.badUser().equals(user)) {
            returnValue = NOT_EXISTS;
        }else if (user.getPremium() == premium) {
            returnValue = ALREADY_EXISTS;
        } else {
            Integer numberOfUpdatedUsers = null;
            SQLException exception = null;

            try {
                numberOfUpdatedUsers = update(
                        USER_TABLE_NAME,
                        USER_ID_COLUMN_NAME,
                        userId,
                        USER_PREMIUM_COLUMN_NAME,
                        premium
                );

            } catch (SQLException e) {
                exception = e;
            }

            returnValue = getUpdateAndDeleteReturnValue(numberOfUpdatedUsers, exception);
        }

        return returnValue;
    }

    /**
     * Removes all tuples from table tableName
     *
     * @param tableName
     */
    private static void clearTable(String tableName) {
        try (Connection connection = DBConnector.getConnection();
             PreparedStatement pstmt = connection.prepareStatement("DELETE FROM " + tableName)){
            pstmt.execute();
        } catch (SQLException e) {
            //e.printStackTrace()();
        }
    }

    /**
     *
     * @param tableName - name of table to drop
     */
    private static void dropTable(String tableName)
    {
        String sql = "DROP TABLE IF EXISTS " + tableName;

        try(Connection connection = DBConnector.getConnection();
            PreparedStatement pstmt = connection.prepareStatement(sql)){

            pstmt.execute();
        } catch (SQLException e) {
            //e.printStackTrace()();
        }
    }

    /**
     *
     * @param viewName - name of view to drop
     */
    private static void dropView(String viewName)
    {
        String sql = "DROP VIEW IF EXISTS " + viewName;

        try (Connection connection = DBConnector.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.execute();
        } catch (SQLException e) {
            //e.printStackTrace()();
        }
    }

    private static ReturnValue getInsertReturnValue(SQLException e) {
        ReturnValue returnValue;
        int sql_state = Integer.valueOf(e.getSQLState());

        if (sql_state == PostgreSQLErrorCodes.FOREIGN_KEY_VIOLATION.getValue()) {
            returnValue = NOT_EXISTS;
        }
        else if (sql_state == PostgreSQLErrorCodes.CHECK_VIOLATION.getValue() ||
                sql_state == PostgreSQLErrorCodes.NOT_NULL_VIOLATION.getValue()) {
            returnValue = BAD_PARAMS;
        }
        else if (sql_state == PostgreSQLErrorCodes.UNIQUE_VIOLATION.getValue()) {
            returnValue = ALREADY_EXISTS;
        }
        else {
            returnValue = ERROR;
        }

        return returnValue;
    }

    private static ReturnValue getUpdateAndDeleteReturnValue(Integer numberOfRecords, SQLException e) {
        ReturnValue returnValue;

        if (null == numberOfRecords) {
            returnValue = getInsertReturnValue(e);
        }
        else
        {
            assert 0 <= numberOfRecords && numberOfRecords <= 1;

            if (0 == numberOfRecords)
            {
                returnValue = NOT_EXISTS;
            }
            else
            {
                returnValue = OK;
            }
        }

        return returnValue;
    }
}

