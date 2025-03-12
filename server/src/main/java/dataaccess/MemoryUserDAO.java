package dataaccess;

import model.UserData;
import service.LoginService;

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
    public boolean verifyUser(String username, String password, UserData user) {;
        if(username == null || !user.password().equals(password)) {
            return false;
        }
        return true;
    }

    @Override
    public UserData getUser(String username){
        return users.get(username);
    }

    @Override
    public void clearUserData() {
        users.clear();
    }
}
