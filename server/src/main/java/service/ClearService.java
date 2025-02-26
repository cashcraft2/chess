package service;

import dataaccess.UserDAO;
import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import dataaccess.DataAccessException;

public class ClearService {

    public record ClearRequest () {}
    public record ClearResult(boolean success, String message) {}

    private final UserDAO userDAO;
    private final GameDAO gameDAO;
    private final AuthDAO authDAO;

    public ClearService(UserDAO userDAO, GameDAO gameDAO, AuthDAO authDAO){
        this.userDAO = userDAO;
        this.gameDAO = gameDAO;
        this.authDAO = authDAO;
    }

    public  ClearResult clear(ClearRequest clearRequest) throws DataAccessException {
        try{
            userDAO.clearUserData();
            gameDAO.clearGameData();
            authDAO.clearAuthData();

            return new ClearResult(true, null);
        }
        catch (DataAccessException error){
            return new ClearResult(false, error.getMessage());
        }
    }
}
