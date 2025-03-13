package dataaccess;

import chess.ChessGame;
import handler.JsonHandler;
import model.GameData;
import org.junit.jupiter.api.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class MySqlGameDAOTest {
    private GameDAO gameDAO;

    @BeforeAll
    void setup() {
        gameDAO = new MySqlGameDAO();
    }

    @BeforeEach
    void clearDatabase() throws SQLException, DataAccessException {
        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement("DELETE FROM users")) {
            statement.executeUpdate();
        }
    }

    @Test
    void testCreateGameSuccess() throws Exception {
        String gameName = "testGame";
        gameDAO.createGame(gameName);

        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM games WHERE gameName = ?")) {
            statement.setString(1, gameName);
            ResultSet result = statement.executeQuery();

            assertTrue(result.next());
            assertEquals(gameName, result.getString("gameName"));
        }
    }

    @Test
    void testCreateGameFailure()  {
        assertThrows(DataAccessException.class, () -> gameDAO.createGame(null));
    }

    @Test
    void testGetGameSuccess() throws Exception {
        GameData gameData = new GameData(123, null, null, "testGame",
                new ChessGame());
        String gameName = "testGame";
        gameDAO.createGame(gameName);

        GameData newGameData = gameDAO.getGame("testGame");

        assertEquals(gameData.gameName(), newGameData.gameName());
    }

    @Test
    void testGetGameFailure() throws Exception {
        GameData gameData = gameDAO.getGame(null);
        assertNull(gameData);
    }

    @Test
    void testGetGameWithIdSuccess() throws Exception {
        String gameName = "testGame";

        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     "INSERT INTO games (gameID, gameName, game) VALUES (?, ?, ?)")) {
            statement.setInt(1, 123);
            statement.setString(2, gameName);
            statement.setString(3, JsonHandler.toJson(new ChessGame()));
            statement.executeUpdate();
        }

        GameData newGameData = gameDAO.getGameWithID(123);

        assertNotNull(newGameData);
        assertEquals(gameName, newGameData.gameName());
    }

    @Test
    void testGetGameWithIdFailure() throws Exception {
        int nonExistentGameID = 999;

        GameData result = gameDAO.getGameWithID(nonExistentGameID);

        assertNull(result);
    }

    @Test
    void testListGamesSuccess() throws Exception {
        String gameName = "testGame";
        gameDAO.createGame(gameName);

        Collection<GameData> games = gameDAO.listGames();

        assertFalse(games.isEmpty());
    }

    @Test
    void testListGamesEmpty() throws Exception {
        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement("DELETE FROM games")) {
            statement.executeUpdate();
        }

        Collection<GameData> games = gameDAO.listGames();

        assertNotNull(games);
        assertTrue(games.isEmpty());
    }


    @Test
    void testUpdateGameSuccess() throws Exception {
        String initialGameName = "InitialGame";
        gameDAO.createGame(initialGameName);

        GameData initialGame = gameDAO.listGames().stream()
                .filter(game -> game.gameName().equals(initialGameName))
                .findFirst()
                .orElseThrow(() -> new Exception("Game not found"));

        int gameID = initialGame.gameID();
        String updatedGameName = "UpdatedGame";

        gameDAO.updateGame(gameID, "newWhitePlayer", "newBlackPlayer", updatedGameName,
                new ChessGame());

        GameData updatedGame = gameDAO.getGameWithID(gameID);

        assertEquals(updatedGameName, updatedGame.gameName());
        assertEquals("newWhitePlayer", updatedGame.whiteUsername());
        assertEquals("newBlackPlayer", updatedGame.blackUsername());
    }

    @Test
    void testUpdateGameFailure() {
        ChessGame game = new ChessGame();
        int fakeGameID = 999;

        assertThrows(DataAccessException.class, () ->
                        gameDAO.updateGame(fakeGameID, "playerWhite", "playerBlack",
                                "Nonexistent Game", game));
    }




}
