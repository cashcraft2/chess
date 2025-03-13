package dataaccess;

import model.UserData;
import org.junit.jupiter.api.*;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class MySqlUserDAOTest {
    private UserDAO userDAO;

    @BeforeAll
    void setup() {
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
    void testCreateUserSuccess() throws Exception {
        UserData user = new UserData("testUser", "testPass", "test@email.com");

        assertDoesNotThrow(() -> userDAO.createUser(user));

        try (Connection connection = DatabaseManager.getConnection();
        PreparedStatement statement = connection.prepareStatement("SELECT * FROM users WHERE username = ?")) {
            statement.setString(1, "testUser");
            var result = statement.executeQuery();
            assertTrue(result.next());
            assertEquals("test@email.com", result.getString("email"));
        }
    }

    @Test
    void testCreateUserFail() throws Exception {
        UserData user = new UserData("testUser", "testPass", "test@email.com");

        userDAO.createUser(user);

        DataAccessException thrown = assertThrows(DataAccessException.class, () -> userDAO.createUser(user));
        assertTrue(thrown.getMessage().contains("Error creating a new user"));
    }

    @Test
    void testVerifyUserSuccess() throws Exception {
        String raw = "testPass";
        String hashed = BCrypt.hashpw(raw, BCrypt.gensalt());
        UserData user = new UserData("testUser", hashed, "test@email.com");

        userDAO.createUser(user);

        boolean verified = userDAO.verifyUser("testUser", raw, user);

        assertTrue(verified);
    }

    @Test
    void testVerifyUserFail() throws Exception {
        boolean verified = userDAO.verifyUser("testUser", "test", null);

        assertFalse(verified);
    }

    @Test
    void testGetUserSuccess() throws Exception{
        UserData user = new UserData("testUser", "testPass", "test@email.com");

        userDAO.createUser(user);

        UserData newUser = userDAO.getUser("testUser");

        assertEquals(user.username(), newUser.username());
        assertEquals(user.email(), newUser.email());
    }

    @Test
    void testGetUserFail() throws Exception {
        UserData userData = userDAO.getUser(null);
        assertNull(userData);
    }

    @Test
    void testClearUserDataSuccess() throws Exception {
        UserData user = new UserData("testUser", "testPass", "test@email.com");
        userDAO.createUser(user);

        UserData userData = userDAO.getUser("testUser");
        assertNotNull(userData);

        userDAO.clearUserData();

        UserData deletedData = userDAO.getUser("testUser");
        assertNull(deletedData);
    }
}
