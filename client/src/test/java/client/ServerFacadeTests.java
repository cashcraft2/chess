package client;

import dataaccess.DatabaseManager;
import exception.ResponseException;
import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.*;
import server.Server;
import server.ServerFacade;

import java.sql.Connection;
import java.sql.PreparedStatement;

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
    public void testRegisterUserFailure() throws Exception {
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
}
