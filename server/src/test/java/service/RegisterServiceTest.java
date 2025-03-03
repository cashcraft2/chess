package service;

import dataaccess.*;
import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class RegisterServiceTest {
    private RegisterService registerService;
    private UserDAO userDAO;
    private AuthDAO authDAO;

    @BeforeEach
    void setup(){
        userDAO = new MemoryUserDAO();
        authDAO = new MemoryAuthDAO();
        registerService = new RegisterService(userDAO, authDAO);
    }

    @Test
    void testRegisterSuccess() throws DataAccessException {
        RegisterService.RegisterRequest request = new RegisterService.RegisterRequest("cotter.ashcraft", "mypassword", "cotter@gmail.com");
        RegisterService.RegisterResult result = registerService.register(request);

        assertEquals(200, result.statusCode());
        assertEquals("cotter.ashcraft", result.username());
        assertNotNull(result.authToken());
        assertNull(result.message());

        UserData savedUser = userDAO.getUser("cotter.ashcraft");
        assertNotNull(savedUser);
        assertEquals("cotter.ashcraft", savedUser.username());
    }

    @Test
    void testRegisterAlreadyExists() throws DataAccessException {
        UserData testUser = new UserData("cotter.ashcraft", "mypassword", "cotter@gmail.com");
        userDAO.createUser(testUser);

        RegisterService.RegisterRequest registerRequest = new RegisterService.RegisterRequest("cotter.ashcraft", "pass", "email");
        RegisterService.RegisterResult result = registerService.register(registerRequest);

        assertEquals(403, result.statusCode());
        assertEquals("Error: already taken", result.message());
    }

    @Test
    void testRegisterBadRequest() throws DataAccessException {
        UserData testUser = new UserData("cotter.ashcraft", "mypassword", "email");
        userDAO.createUser(testUser);

        RegisterService.RegisterRequest registerRequest = new RegisterService.RegisterRequest(null, "password", "email");
        RegisterService.RegisterResult result = registerService.register(registerRequest);

        assertEquals(400, result.statusCode());
        assertEquals("Error: bad request", result.message());
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
