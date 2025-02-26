package dataaccess;
import model.AuthData;
import model.UserData;

public interface AuthDAO {
    void createAuthToken(UserData userData) throws DataAccessException;

    AuthData getAuthToken(String username) throws DataAccessException;

    void deleteAuthToken(String username) throws DataAccessException;

    void clearAuthData() throws DataAccessException;
}
