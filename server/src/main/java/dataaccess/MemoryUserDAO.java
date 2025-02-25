package dataaccess;

import model.UserData;
import org.eclipse.jetty.server.Authentication;

import java.util.HashMap;
import java.util.Map;

public class MemoryUserDAO implements UserDAO {
    private final Map<String, UserData> users = new HashMap<>();

    @Override
    public void createUser(UserData userData) throws DataAccessException {
        if (users.containsKey(userData.username())){
            throw new DataAccessException("Error: This username is already taken by another user.");
        }
        users.put(userData.username(), userData);
    }

    @Override
    public UserData getUser(String username) throws DataAccessException {
        UserData user = users.get(username);

        if (user == null) {
            throw new DataAccessException("Error: User does not exist.");
        }

        return user;
    }

    @Override
    public void updateUser(UserData user) throws DataAccessException {
        if (!users.containsKey(user.username())){
            throw new DataAccessException("Error: Unable to update user. User does not exist");
        }
        users.put(user.username(), user);
    }
}
