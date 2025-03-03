package service;

import dataaccess.*;
import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class LogoutServiceTest {
    private LogoutService logoutService;
    private UserDAO userDAO;
    private AuthDAO authDAO;

    @BeforeEach
    void setup(){
        userDAO = new MemoryUserDAO();
        authDAO = new MemoryAuthDAO();
        logoutService = new LogoutService(authDAO);
    }

    @Test
    void testLogoutSuccess() throws DataAccessException{
        UserData userData = new UserData("user", "pass", "email");
        userDAO.createUser(userData);

        String authToken = "valid-token";
        authDAO.createAuthToken(new AuthData(authToken, "user"));

        LogoutService.LogoutResult result = logoutService.logout(authToken);

        assertEquals(200, result.statusCode());
        assertNull(result.message());
    }

    @Test
    void testLogoutUnauthorized() throws DataAccessException {
        String authToken = "fake-token";

        LogoutService.LogoutResult result = logoutService.logout(authToken);

        assertEquals(401, result.statusCode());
        assertEquals("Error: unauthorized", result.message());
    }

    @Test
    void testRegisterUniqueToken() {
        String dataToken = RegisterService.generateToken();
        String newToken = RegisterService.generateToken();
        assertNotNull(dataToken);
        assertNotNull(newToken);
        assertNotEquals(dataToken, newToken);
    }
}
