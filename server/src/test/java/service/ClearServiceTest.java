package service;

import chess.ChessGame;
import dataaccess.*;
import model.AuthData;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ClearServiceTest {
    private ClearService clearService;
    private UserDAO userDAO;
    private AuthDAO authDAO;
    private GameDAO gameDAO;

    @BeforeEach
    void setup(){
        userDAO = new MemoryUserDAO();
        authDAO = new MemoryAuthDAO();
        gameDAO = new MemoryGameDAO();
        clearService = new ClearService(userDAO, gameDAO, authDAO);
    }

    @Test
    void testClearSuccess() throws DataAccessException {
        UserData user = new UserData("user", "pass", "email");
        userDAO.createUser(user);

        AuthData authData = new AuthData("agsht-45-hdhd-56", "user");
        authDAO.createAuthToken(authData);

        ChessGame game = new ChessGame();
        GameData gameData =
                new GameData(1234, "user1", "user2", "game", game);

        ClearService.ClearResult result = clearService.clear();

        assertEquals(200, result.statusCode());
    }
}
