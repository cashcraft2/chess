package service;

import dataaccess.*;
import model.AuthData;
import model.UserData;
import org.eclipse.jetty.server.Authentication;
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
    void testLogout_Success() throws DataAccessException{
        UserData userData = new UserData("user", "pass", "email");
        userDAO.createUser(userData);

        String authToken = "valid-token";
        authDAO.createAuthToken(new AuthData(authToken, "user"));

        LogoutService.LogoutResult result = logoutService.logout(authToken);

        assertEquals(200, result.statusCode());
        assertNull(result.message());
    }

    @Test
    void testLogout_unauthorized() throws DataAccessException {
        String authToken = "fake-token";

        LogoutService.LogoutResult result = logoutService.logout(authToken);

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
