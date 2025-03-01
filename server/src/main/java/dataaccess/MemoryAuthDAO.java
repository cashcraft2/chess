package dataaccess;

import model.AuthData;
import model.UserData;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MemoryAuthDAO implements AuthDAO {

    private final Map<String, AuthData> authTokens = new HashMap<>();


    @Override
    public void createAuthToken(AuthData authData) throws DataAccessException {
        if (authTokens.containsKey(authData.authToken())){
            throw new DataAccessException("This authToken already exists.");
        }
        authTokens.put(authData.authToken(), authData);
    }

    @Override
    public AuthData getAuthData(String authToken){
        return authTokens.get(authToken);
    }

    @Override
    public AuthData getAuthToken(String username) throws DataAccessException {
        if(!authTokens.containsKey(username)) {
            throw new DataAccessException("There is no existing authToken for this user.");
        }
        return authTokens.get(username);
    }

    @Override
    public void deleteAuthToken(String authData) throws DataAccessException {
        if (!authTokens.containsKey(authData)) {
            throw new DataAccessException("There is no existing authToken for this user.");
        }
        authTokens.remove(authData);
    }

    @Override
    public void clearAuthData() {
        authTokens.clear();
    }
}
