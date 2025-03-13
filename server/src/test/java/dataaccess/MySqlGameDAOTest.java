package dataaccess;

import chess.ChessGame;
import handler.JsonHandler;
import model.AuthData;
import model.GameData;
import model.UserData;
import org.eclipse.jetty.server.Authentication;
import org.junit.jupiter.api.*;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class MySqlGameDAOTest {
    private GameDAO gameDAO;
    private AuthDAO authDAO;
    private UserDAO userDAO;

    @BeforeAll
    void setup() {
        userDAO = new MySqlUserDAO();
        authDAO = new MySqlAuthDAO();
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
            assertNotNull(result.getInt("gameID"));
        }
    }

    @Test
    void testCreateGameFailure() throws Exception {
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

        // Manually insert a game with a known gameID = 123
        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     "INSERT INTO games (gameID, gameName, game) VALUES (?, ?, ?)")) {
            statement.setInt(1, 123); // Ensure gameID is explicitly set
            statement.setString(2, gameName);
            statement.setString(3, JsonHandler.toJson(new ChessGame())); // Serialize a new game
            statement.executeUpdate();
        }

        // Now we know gameID 123 exists, so we can fetch it
        GameData newGameData = gameDAO.getGameWithID(123);

        assertNotNull(newGameData);
        assertEquals(gameName, newGameData.gameName());
    }

    @Test
    void testGetGameWithIdFailure() throws Exception {
        int nonExistentGameID = 999; // A game ID that does not exist in the database

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
    void testListGamesFailure() throws Exception{
        Collection<GameData> games = gameDAO.listGames();
        assertTrue(games.isEmpty());
    }




}
