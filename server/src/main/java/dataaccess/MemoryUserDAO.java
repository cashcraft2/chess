package dataaccess;

import model.UserData;
import org.eclipse.jetty.server.Authentication;

import java.util.HashMap;
import java.util.Map;

public class MemoryUserDAO implements UserDAO {
    private static final Map<String, UserData> users = new HashMap<>();

    @Override
    public void createUser(UserData userData) throws DataAccessException {
        String normalizedUsername = userData.username().toLowerCase();
        if(users.containsKey(normalizedUsername)){
            throw new DataAccessException("Error: already taken");
        }
        users.put(normalizedUsername, new UserData(normalizedUsername, userData.password(), userData.email()));
    }

    @Override
    public UserData getUser(String username){
        return users.get(username.toLowerCase());
    }

    @Override
    public void updateUser(UserData user) throws DataAccessException {
        if (!users.containsKey(user.username())){
            throw new DataAccessException("Unable to update user. User does not exist");
        }
        users.put(user.username(), user);
    }

    @Override
    public void clearUserData() throws DataAccessException {
        if (users.isEmpty()){
            throw new DataAccessException("There are no users to clear from the database.");
        }
        users.clear();
    }
}
