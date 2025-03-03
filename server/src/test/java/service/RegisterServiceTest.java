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
    void testRegister_Success() throws DataAccessException {
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
    void testRegister_alreadyExists() throws DataAccessException {
        UserData testUser = new UserData("cotter.ashcraft", "mypassword", "cotter@gmail.com");
        userDAO.createUser(testUser);

        RegisterService.RegisterRequest registerRequest = new RegisterService.RegisterRequest("cotter.ashcraft", "pass", "email");
        RegisterService.RegisterResult result = registerService.register(registerRequest);

        assertEquals(403, result.statusCode());
        assertEquals("Error: already taken", result.message());
    }

    @Test
    void testRegister_badRequest() throws DataAccessException {
        UserData testUser = new UserData("cotter.ashcraft", "mypassword", "email");
        userDAO.createUser(testUser);

        RegisterService.RegisterRequest registerRequest = new RegisterService.RegisterRequest(null, "password", "email");
        RegisterService.RegisterResult result = registerService.register(registerRequest);

        assertEquals(400, result.statusCode());
        assertEquals("Error: bad request", result.message());
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
