package service;

import dataaccess.*;
import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class LoginServiceTest {
    private LoginService loginService;
    private UserDAO userDAO;
    private AuthDAO authDAO;

    @BeforeEach
    void setup(){
        userDAO = new MemoryUserDAO();
        authDAO = new MemoryAuthDAO();
        loginService = new LoginService(userDAO, authDAO);
    }

    @Test
    void testLoginSuccess() throws DataAccessException{
        UserData userData = new UserData("user", "pass", "email");
        userDAO.createUser(userData);

        LoginService.LoginRequest request = new LoginService.LoginRequest("user", "pass");
        LoginService.LoginResult result = loginService.login(request);

        assertEquals(200, result.statusCode());
        assertEquals("user", result.username());
        assertNotNull(result.authToken());
    }

    @Test
    void testLoginUnauthorized() throws DataAccessException {
        UserData userData = new UserData("user", "pass", "email");
        userDAO.createUser(userData);

        LoginService.LoginRequest request = new LoginService.LoginRequest("user", "pass2");
        LoginService.LoginResult result = loginService.login(request);

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
