package dataaccess;
import model.UserData;

import javax.xml.crypto.Data;

public interface UserDAO {
    void createUser(UserData user) throws DataAccessException;

    UserData getUser(String username) throws DataAccessException;

    void updateUser(UserData user) throws DataAccessException;

    void clearUserData() throws DataAccessException;

}
