package dataaccess;
import model.AuthData;

public interface AuthDAO {
    void createAuthToken(AuthData authData) throws DataAccessException;

    AuthData getAuthData(String authToken) throws DataAccessException;

    AuthData getAuthToken(String username) throws DataAccessException;

    void deleteAuthToken(String username) throws DataAccessException;

    void clearAuthData() throws DataAccessException;
}
