package dataaccess;

import exception.ResponseException;
import model.UserData;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class MySqlUserDAO implements UserDAO{

    public MySqlUserDAO() {
        try {
            new DatabaseManager().configureDatabase();
        } catch (ResponseException ex) {
            System.out.println(ex.getMessage());
        }

    }

    @Override
    public void createUser(UserData user) throws DataAccessException {
        String sql = "INSERT INTO users (username, password, email) VALUES (?, ?, ?)";
        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            String hashedPassword = BCrypt.hashpw(user.password(), BCrypt.gensalt());

            statement.setString(1, user.username());
            statement.setString(2, hashedPassword);
            statement.setString(3, user.email());

            statement.executeUpdate();
        }
        catch (SQLException ex) {
            throw new DataAccessException("Error creating a new user in the database: " + ex.getMessage());
        }
    }

    @Override
    public boolean verifyUser(String username, String password, UserData user) {
        if (username == null) {
            return false;
        }
        var hashedPassword = user.password();
        return BCrypt.checkpw(password, hashedPassword);
    }

    @Override
    public UserData getUser(String username) throws DataAccessException {
        String sql = "SELECT username, password, email FROM users WHERE username = ?";
        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, username);
            try (ResultSet result = statement.executeQuery()){
                if(result.next()){
                    String password = result.getString("password");
                    String email = result.getString("email");
                    return new UserData(username, password, email);
                }
            }
        }
        catch (SQLException ex) {
            throw new DataAccessException("Error getting userData from the database: " + ex.getMessage());
        }
        return null;
    }

    @Override
    public void clearUserData() throws DataAccessException {
        String sql = "DROP TABLE users";
        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.executeUpdate();
        }
        catch (SQLException ex) {
            throw new DataAccessException("Error dropping users table from database: " + ex.getMessage());
        }
    }
}
