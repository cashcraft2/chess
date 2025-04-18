package dataaccess;
import model.UserData;

public interface UserDAO {
    void createUser(UserData user) throws DataAccessException;

    UserData getUser(String username) throws DataAccessException;

    boolean verifyUser(String username, String password, UserData user);

    void clearUserData() throws DataAccessException;

}
