package dataaccess;

import model.UserData;
import org.eclipse.jetty.server.Authentication;

import java.util.HashMap;
import java.util.Map;

public class MemoryUserDAO implements UserDAO {
    private final Map<String, UserData> users = new HashMap<>();

    @Override
    public void createUser(UserData userData) throws DataAccessException {
        String username = userData.username();
        if(users.containsKey(username)){
            throw new DataAccessException("Error: already taken");
        }
        users.put(username, new UserData(username, userData.password(), userData.email()));
    }

    @Override
    public UserData getUser(String username){
        return users.get(username);
    }

    @Override
    public void updateUser(UserData user) throws DataAccessException {
        if (!users.containsKey(user.username())){
            throw new DataAccessException("Unable to update user. User does not exist");
        }
        users.put(user.username(), user);
    }

    @Override
    public void clearUserData() {
        users.clear();
    }
}
