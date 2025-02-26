package dataaccess;

import model.AuthData;
import model.UserData;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MemoryAuthDAO implements AuthDAO {

    private final Map<String, AuthData> authTokens = new HashMap<>();

    public static String generateToken() {
        return UUID.randomUUID().toString();
    }

    @Override
    public void createAuthToken(UserData userData) throws DataAccessException {
        String authToken = generateToken();
        AuthData newAuthData = new AuthData(authToken,userData.username());
        if (authTokens.containsKey(newAuthData.authToken())){
            throw new DataAccessException("Error: This authToken already exists.");
        }
        authTokens.put(userData.username(), newAuthData);
    }

    @Override
    public AuthData getAuthToken(String username) throws DataAccessException {
        if(!authTokens.containsKey(username)){
            throw new DataAccessException("Error: There is no existing authToken for this user.");
        }
        return authTokens.get(username);
    }

    @Override
    public void deleteAuthToken(String username) throws DataAccessException {
        if (!authTokens.containsKey(username)) {
            throw new DataAccessException("Error: There is no existing authToken for this user.");
        }
        authTokens.remove(username);
    }

    @Override
    public void clearAuthData() throws DataAccessException {
        if (authTokens.isEmpty()){
            throw new DataAccessException("Error: There are no authTokens to clear from the database.");
        }
        authTokens.clear();
    }
}
