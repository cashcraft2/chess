package dataaccess;

import exception.ResponseException;
import model.AuthData;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class MySqlAuthDAO implements AuthDAO{

    public MySqlAuthDAO() {
        try {
            new DatabaseManager().configureDatabase();
        } catch (ResponseException ex) {
            System.out.println(ex.getMessage());
        }
    }

    @Override
    public void createAuthToken(AuthData authData) throws DataAccessException {
        String sql = "INSERT INTO authTokens (authToken) VALUES (?)";
        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, authData.authToken());
            statement.executeUpdate();
        }
        catch(SQLException ex) {
            throw new DataAccessException("Error adding authToken to the database: " + ex.getMessage());
        }

    }

    @Override
    public AuthData getAuthData(String authToken) throws DataAccessException {
        String sql = """
            SELECT authTokens.authToken, users.username 
            FROM authTokens 
            JOIN users ON authTokens.id = users.id
            WHERE authTokens.authToken = ?;
        """;

        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, authToken);
            try (ResultSet result = statement.executeQuery()) {
                if (result.next()) {
                    String username = result.getString("username");
                    return new AuthData(authToken, username);
                }
            }
        }
        catch (SQLException ex) {
            throw new DataAccessException("Error retrieving the authData from the database: " + ex.getMessage());
        }
        return null;
    }

    @Override
    public AuthData getAuthToken(String username) throws DataAccessException {
        String sql = """
            SELECT authTokens.authToken, users.username 
            FROM authTokens 
            JOIN users ON authTokens.id = users.id
            WHERE users.username = ?;
        """;

        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, username);
            try (ResultSet result = statement.executeQuery()) {
                if (result.next()) {
                    String authToken = result.getString("authToken");
                    return new AuthData(authToken, username);
                }
            }
        }
        catch (SQLException ex) {
            throw new DataAccessException("Error retrieving the authData from the database: " + ex.getMessage());
        }
        return null;
    }

    @Override
    public void deleteAuthToken(String username) throws DataAccessException {
        String sql = """
                DELETE authTokens
                FROM authTokens
                JOIN users ON authTokens.id = users.id
                WHERE users.username = ?
                """;

        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, username);
            statement.executeUpdate();
        }
        catch (SQLException ex) {
            throw new DataAccessException("Error trying to delete authData from database: " + ex.getMessage());
        }
    }

    @Override
    public void clearAuthData() throws DataAccessException {
        String sql = "DROP TABLE authTokens";
        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.executeUpdate();
        }
        catch (SQLException ex) {
            throw new DataAccessException("Error dropping the authTokens table in database: " + ex.getMessage());
        }
    }
}
