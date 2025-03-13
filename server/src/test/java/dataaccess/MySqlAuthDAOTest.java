package dataaccess;

import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.*;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class MySqlAuthDAOTest {
    private AuthDAO authDAO;
    private UserDAO userDAO;

    @BeforeAll
    void setup() {
        authDAO = new MySqlAuthDAO();
        userDAO = new MySqlUserDAO();
    }

    @BeforeEach
    void clearDatabase() throws SQLException, DataAccessException {
        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement("DELETE FROM users")) {
            statement.executeUpdate();
        }
    }

    @Test
    void testCreateAuthTokenSuccess() throws Exception {
        UserData user = new UserData("testUser", "test", "test@email.com");
        userDAO.createUser(user);
        AuthData authData = new AuthData("asd23-43-4234", "testUser");
        authDAO.createAuthToken(authData);

        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM authTokens")) {
            var result = statement.executeQuery();
            result.next();
            String authToken = result.getString("authToken");
            assertEquals("asd23-43-4234", authToken);
        }
    }

    @Test
    void testCreateAuthTokenFailure() throws Exception {
        UserData user = new UserData("testUser", "testPass", "test@email.com");
        userDAO.createUser(user);

        AuthData authData = new AuthData("asd23-43-4234", "testUser");
        authDAO.createAuthToken(authData);

        DataAccessException thrown = assertThrows(DataAccessException.class, () -> authDAO.createAuthToken(authData));
        assertTrue(thrown.getMessage().contains("Error adding authToken"));
    }

    @Test
    void testGetAuthSuccess() throws Exception {
        UserData user = new UserData("testUser", "testPass", "test@email.com");
        userDAO.createUser(user);

        AuthData authData = new AuthData("asd23-43-4234", "testUser");
        authDAO.createAuthToken(authData);

        AuthData newAuth = authDAO.getAuthData("asd23-43-4234");

        assertEquals(authData.authToken(), newAuth.authToken());
    }

    @Test
    void testGetAuthFail() throws Exception {
        AuthData authData = authDAO.getAuthData(null);
        assertNull(authData);
    }

    @Test
    void testGetAuthTokenSuccess() throws Exception {
        UserData user = new UserData("testUser", "testPass", "test@email.com");
        userDAO.createUser(user);

        AuthData authData = new AuthData("asd23-43-4234", "testUser");
        authDAO.createAuthToken(authData);

        AuthData newAuth = authDAO.getAuthToken("testUser");

        assertEquals(authData.authToken(), newAuth.authToken());
    }

    @Test
    void testGetAuthTokenFail() throws Exception {
        AuthData authData = authDAO.getAuthToken(null);
        assertNull(authData);
    }

    @Test
    void testDeleteAuthTokenSuccess() throws Exception {
        UserData user = new UserData("testUser", "testPass", "test@email.com");
        userDAO.createUser(user);

        AuthData authData = new AuthData("asd23-43-4234", "testUser");
        authDAO.createAuthToken(authData);

        authDAO.deleteAuthToken("asd23-43-4234");

        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement
                     ("SELECT * FROM authTokens WHERE authToken = ?")) {
            statement.setString(1, "asd23-43-4234");
            var result = statement.executeQuery();
            assertFalse(result.next());
        }
    }

    @Test
    void testDeleteAuthTokenFailure() throws Exception {
        UserData user = new UserData("testUser", "testPass", "test@email.com");
        userDAO.createUser(user);

        AuthData authData = new AuthData("asd23-43-4234", "testUser");
        authDAO.createAuthToken(authData);

        authDAO.deleteAuthToken("testUser");

        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement
                     ("SELECT * FROM authTokens WHERE authToken = ?")) {
            statement.setString(1, "asd23-43-4234");
            var result = statement.executeQuery();
            assertTrue(result.next());
        }
    }

    @Test
    void testClearAuthDataSuccess() throws Exception {
        UserData user = new UserData("testUser", "testPass", "test@email.com");
        userDAO.createUser(user);

        AuthData authData = new AuthData("asd23-43-4234", "testUser");
        authDAO.createAuthToken(authData);

        AuthData newAuthData = authDAO.getAuthToken("testUser");
        assertNotNull(newAuthData);

        authDAO.clearAuthData();

        AuthData deletedData = authDAO.getAuthToken("testUser");
        assertNull(deletedData);
    }
}

