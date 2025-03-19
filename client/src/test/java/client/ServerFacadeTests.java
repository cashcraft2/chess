package client;

import chess.ChessGame;
import dataaccess.DatabaseManager;
import exception.ResponseException;
import model.AuthData;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.*;
import server.Server;
import server.ServerFacade;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

public class ServerFacadeTests {

    private static Server server;
    static ServerFacade facade;

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(8080);
        System.out.println("Started test HTTP server on " + port);
        facade = new ServerFacade("http://localhost:8080");
    }

    @BeforeEach
    void clearDatabase() throws ResponseException {
        facade.clearDatabase();
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }


    @Test
    public void testClearDatabaseSuccess() throws Exception {
        facade.registerUser(new UserData("username", "password", "email"));

        facade.clearDatabase();

        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM authTokens")) {
            var result = statement.executeQuery();
            assertFalse(result.next());
        }
        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM users")) {
            var result = statement.executeQuery();
            assertFalse(result.next());
        }
    }

    @Test
    public void testRegisterUserSuccess() throws Exception {
        var authData = facade.registerUser(new UserData("username", "password", "email"));
        assertNotNull(authData.authToken());
        assertEquals("username", authData.username());
    }

    @Test
    public void testRegisterUserFailure() {
        UserData userData = new UserData("username", "password", null);

        assertThrows(ResponseException.class, () -> {
            facade.registerUser(userData);
        });
    }

    @Test
    public void testLoginUserSuccess() throws Exception {
        UserData userData = new UserData("username", "password", "email");
        facade.registerUser(userData);
        AuthData authData = facade.loginUser(userData);

        assertNotNull(authData.authToken());
        assertEquals("username", authData.username());
    }

    @Test
    public void testLoginUserFailure() {
        UserData userData = new UserData("username", "password", null);

        assertThrows(ResponseException.class, () -> {
            facade.loginUser(userData);
        });
    }

    @Test
    public void testLogoutUserSuccess() throws Exception {
        UserData userData = new UserData("username", "password", "email");
        facade.registerUser(userData);

        AuthData authData = facade.loginUser(userData);
        String authToken = authData.authToken();
        assertNotNull(authToken);

        facade.logoutUser(authToken);

        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM authTokens")) {
            var result = statement.executeQuery();
            result.next();
            assertFalse(result.next());
        }
    }

    @Test
    public void testLogoutUserFailure() throws Exception {
        UserData userData = new UserData("username", "password", "email");
        facade.registerUser(userData);

        AuthData authData = facade.loginUser(userData);

        assertThrows(ResponseException.class, () -> {
            facade.logoutUser("invalidAuthToken");
        });
    }

    @Test
    public void testListGamesSuccess() throws Exception {
        UserData userData = new UserData("username", "password", "email");
        facade.registerUser(userData);

        AuthData authData = facade.loginUser(userData);
        String authToken = authData.authToken();
        assertNotNull(authToken);

        GameData game = new GameData
                (123, null, null, "gameName", new ChessGame());
        facade.createGame(game, authToken);
        Collection<GameData> games = facade.listGames(authToken);

        assertNotNull(games);
        assertFalse(games.isEmpty());
    }

    @Test
    public void testListGamesFailure() throws Exception {
        UserData userData = new UserData("username", "password", "email");
        facade.registerUser(userData);

        AuthData authData = facade.loginUser(userData);
        String authToken = authData.authToken();
        assertNotNull(authToken);

        GameData game = new GameData
                (123, null, null, "gameName", new ChessGame());
        facade.createGame(game, authToken);

        assertThrows(ResponseException.class, () -> {
            facade.listGames("invalidAuthToken");
        });
    }

    @Test
    public void testCreateGameSuccess() throws Exception {
        UserData userData = new UserData("username", "password", "email");
        facade.registerUser(userData);

        AuthData authData = facade.loginUser(userData);
        String authToken = authData.authToken();
        assertNotNull(authToken);

        GameData game = new GameData
                (123, null, null, "gameName", new ChessGame());
        facade.createGame(game, authToken);

        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM games")) {
            var result = statement.executeQuery();
            assertTrue(result.next());
        }
    }

    @Test
    public void testCreateGameFailure() throws Exception {
        UserData userData = new UserData("username", "password", "email");
        facade.registerUser(userData);

        AuthData authData = facade.loginUser(userData);
        String authToken = authData.authToken();
        assertNotNull(authToken);

        GameData game = new GameData
                (123, null, null, null, new ChessGame());

        assertThrows(ResponseException.class, () -> {
            facade.createGame(game, authToken);
        });
    }

}
