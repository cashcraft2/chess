package service;

import dataaccess.UserDAO;
import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import dataaccess.DataAccessException;

public class ClearService {

    public record ClearResult(int statusCode, String message) {}

    private final UserDAO userDAO;
    private final GameDAO gameDAO;
    private final AuthDAO authDAO;

    public ClearService(UserDAO userDAO, GameDAO gameDAO, AuthDAO authDAO){
        this.userDAO = userDAO;
        this.gameDAO = gameDAO;
        this.authDAO = authDAO;
    }

    public  ClearResult clear() {
        try{
            authDAO.clearAuthData();
            userDAO.clearUserData();
            gameDAO.clearGameData();


            return new ClearResult(200, null);
        }
        catch (DataAccessException error){
            return new ClearResult(500, "Error: " + error.getMessage());
        }
    }
}
