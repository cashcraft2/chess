package service;

import dataaccess.*;
import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ListGamesServiceTest {
    private UserDAO userDAO;
    private AuthDAO authDAO;
    private GameDAO gameDAO;
    private ListGamesService listGamesService;

    @BeforeEach
    void setup(){
        userDAO = new MemoryUserDAO();
        authDAO = new MemoryAuthDAO();
        gameDAO = new MemoryGameDAO();
        listGamesService = new ListGamesService(gameDAO, authDAO);
    }

    @Test
    void testListGames_Success() throws DataAccessException {
        gameDAO.createGame("game");
        UserData userData = new UserData("user", "pass", "email");
        userDAO.createUser(userData);

        String authToken = "valid_token";
        authDAO.createAuthToken(new AuthData(authToken, "user"));

        ListGamesService.ListGamesResult result = listGamesService.list(authToken);

        assertEquals(200, result.statusCode());
        assertNull(result.message());
    }

    @Test
    void testListGames_unauthorized() throws DataAccessException {
        String authToken = "invalid-token";

        ListGamesService.ListGamesResult result = listGamesService.list(authToken);

        assertEquals(401, result.statusCode());
        assertEquals("Error: unauthorized", result.message());
    }

    @Test
    void testRegister_uniqueToken() {
        String data_token = RegisterService.generateToken();
        String new_token = RegisterService.generateToken();
        assertNotNull(data_token);
        assertNotNull(new_token);
        assertNotEquals(data_token, new_token);
    }
}
