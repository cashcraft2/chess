package service;

import dataaccess.*;
import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class CreateGameServiceTest {
    private UserDAO userDAO;
    private AuthDAO authDAO;
    private GameDAO gameDAO;
    private CreateGameService createGameService;

    @BeforeEach
    void setup(){
        userDAO = new MemoryUserDAO();
        authDAO = new MemoryAuthDAO();
        gameDAO = new MemoryGameDAO();
        createGameService = new CreateGameService(gameDAO, authDAO);
    }

    @Test
    void testCreateGameSuccess() throws DataAccessException {
        UserData userData = new UserData("user", "pass", "email");
        userDAO.createUser(userData);

        String authToken = "valid_token";
        authDAO.createAuthToken(new AuthData(authToken, "user"));

        CreateGameService.CreateGameRequest request = new CreateGameService.CreateGameRequest("gameName");
        CreateGameService.CreateGameResult result = createGameService.create(request, authToken);

        int gameID = gameDAO.getGame("gameName").gameID();
        assertEquals(200, result.statusCode());
        assertEquals(gameID, result.gameID());
    }
    @Test
    void testCreateGameBadRequest() throws DataAccessException {
        UserData userData = new UserData("user", "pass", "email");
        userDAO.createUser(userData);

        String authToken = "valid_token";
        authDAO.createAuthToken(new AuthData(authToken, "user"));

        CreateGameService.CreateGameRequest request = new CreateGameService.CreateGameRequest(null);
        CreateGameService.CreateGameResult result = createGameService.create(request, authToken);

        assertEquals(400, result.statusCode());
        assertEquals("Error: bad request", result.message());
    }
    @Test
    void testCreateGameUnauthorized() {
        String authToken = "invalid_token";

        CreateGameService.CreateGameRequest request = new CreateGameService.CreateGameRequest("gameName");
        CreateGameService.CreateGameResult result = createGameService.create(request, authToken);

        assertEquals(401, result.statusCode());
        assertEquals("Error: unauthorized", result.message());
    }
}
