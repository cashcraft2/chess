package service;

import dataaccess.GameDAO;
import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import model.AuthData;
import model.GameData;
import spark.Request;

import java.util.Collection;

public class CreateGameService {
    public record CreateGameRequest(String gameName){}
    public record CreateGameResult(int statusCode, Integer gameID, String message){}

    private final GameDAO gameDAO;
    private final AuthDAO authDAO;

    public CreateGameService (GameDAO gameDAO, AuthDAO authDAO){
        this.gameDAO = gameDAO;
        this.authDAO = authDAO;
    }

    public CreateGameResult create(Request createGameRequest, String authToken){
        if(authToken == null || authToken.isBlank()){
            return new CreateGameResult(401, null, "Error: unauthorized");
        }

        try {
            AuthData authData = authDAO.getAuthData(authToken);
            if(authData == null) {
                return new CreateGameResult(401, null, "Error: unauthorized");
            }
            String gameName = createGameRequest.body();
            if(gameName == null) {
                return new CreateGameResult(400, null, "Error: bad request");
            }
            GameData game = gameDAO.getGame(gameName);
            if (!(game == null)) {
                return new CreateGameResult(400, null, "Error: bad request");
            }
            gameDAO.createGame(gameName);
            GameData newGame = gameDAO.getGame(gameName);

            int gameId = newGame.gameID();

            return new CreateGameResult(200, gameId, null);
        }

        catch (DataAccessException error) {
            return new CreateGameResult(500, null, "Error: " + error.getMessage());
        }
    }
}
