package service;

import dataaccess.GameDAO;
import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import model.AuthData;
import model.GameData;
import model.UserData;
import java.util.Collection;

public class ListGamesService {
    public record ListGamesRequest(){}
    public record ListGamesResult(int statusCode, String message, Collection<GameData> games){}

    private final GameDAO gameDAO;
    private final AuthDAO authDAO;

    public ListGamesService (GameDAO gameDAO, AuthDAO authDAO){
        this.gameDAO = gameDAO;
        this.authDAO = authDAO;
    }

    public ListGamesResult list(String authToken){
        if(authToken == null || authToken.isBlank()){
            return new ListGamesResult(401, "Error: unauthorized", null);
        }

        try {
            AuthData authData = authDAO.getAuthData(authToken);
            if(authData == null){
                return new ListGamesResult(401, "Error: unauthorized", null);
            }
            Collection<GameData> games = gameDAO.listGames();
            return new ListGamesResult(200, null, games);
        }

        catch (DataAccessException error) {
            return new ListGamesResult(500, "Error: " + error.getMessage(), null);
        }
    }
}
