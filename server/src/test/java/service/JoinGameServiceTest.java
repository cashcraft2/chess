package service;

import chess.ChessGame;
import dataaccess.*;
import model.AuthData;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class JoinGameServiceTest {
    private UserDAO userDAO;
    private AuthDAO authDAO;
    private GameDAO gameDAO;
    private JoinGameService joinGameService;

    @BeforeEach
    void setup(){
        userDAO = new MemoryUserDAO();
        authDAO = new MemoryAuthDAO();
        gameDAO = new MemoryGameDAO();
        joinGameService = new JoinGameService(gameDAO, authDAO);
    }

    @Test
    void testJoinGame_Success() throws DataAccessException {
        gameDAO.createGame("gameName");
        int gameID = gameDAO.getGame("gameName").gameID();
        UserData userData = new UserData("user", "pass", "email");
        userDAO.createUser(userData);

        String authToken = "valid_token";
        authDAO.createAuthToken(new AuthData(authToken, "user"));

        JoinGameService.JoinGameRequest request = new JoinGameService.JoinGameRequest("BLACK", gameID);
        JoinGameService.JoinGameResult result = joinGameService.join(request, authToken);

        assertEquals(200, result.statusCode());
        assertNull(result.message());
    }

    @Test
    void testJoinGame_badRequest() throws DataAccessException {
        gameDAO.createGame("gameName");
        int gameID = gameDAO.getGame("gameName").gameID();
        UserData userData = new UserData("user", "pass", "email");
        userDAO.createUser(userData);

        String authToken = "valid_token";
        authDAO.createAuthToken(new AuthData(authToken, "user"));

        JoinGameService.JoinGameRequest request = new JoinGameService.JoinGameRequest(null, gameID);
        JoinGameService.JoinGameResult result = joinGameService.join(request, authToken);

        assertEquals(400, result.statusCode());
        assertEquals("Error: bad request", result.message());
    }

    @Test
    void testJoinGame_unauthorized() throws DataAccessException {
        gameDAO.createGame("gameName");
        int gameID = gameDAO.getGame("gameName").gameID();
        UserData userData = new UserData("user", "pass", "email");
        userDAO.createUser(userData);

        String authToken = "invalid_token";

        JoinGameService.JoinGameRequest request = new JoinGameService.JoinGameRequest(null, gameID);
        JoinGameService.JoinGameResult result = joinGameService.join(request, authToken);

        assertEquals(401, result.statusCode());
        assertEquals("Error: unauthorized", result.message());
    }

    @Test
    void testJoinGame_alreadyTaken() throws DataAccessException {
        gameDAO.createGame("gameName");
        int gameID = gameDAO.getGame("gameName").gameID();
        gameDAO.updateGame(gameID, "WHITE", null, "gameName", null);
        UserData userData = new UserData("user", "pass", "email");
        userDAO.createUser(userData);

        String authToken = "valid_token";
        authDAO.createAuthToken(new AuthData(authToken, "user"));

        JoinGameService.JoinGameRequest request = new JoinGameService.JoinGameRequest("WHITE", gameID);
        JoinGameService.JoinGameResult result = joinGameService.join(request, authToken);

        assertEquals(403, result.statusCode());
        assertEquals("Error: already taken", result.message());
    }

}
