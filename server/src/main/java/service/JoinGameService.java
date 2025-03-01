package service;

import dataaccess.GameDAO;
import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import model.AuthData;
import model.GameData;
import spark.Request;
import java.util.Collection;

public class JoinGameService {
    public record JoinGameRequest(String playerColor, int gameID){}
    public record JoinGameResult(int statusCode, String message){}

    private final GameDAO gameDAO;
    private final AuthDAO authDAO;

    public JoinGameService (GameDAO gameDAO, AuthDAO authDAO){
        this.gameDAO = gameDAO;
        this.authDAO = authDAO;
    }

    public JoinGameResult join(JoinGameRequest joinGameRequest, String authToken){
        if(authToken == null || authToken.isBlank()){
            return new JoinGameResult(401, "Error: unauthorized");
        }

        try {
            AuthData authData = authDAO.getAuthData(authToken);
            if(authData == null) {
                return new JoinGameResult(401, "Error: unauthorized");
            }
            String playerColor = joinGameRequest.playerColor();
            int gameID = joinGameRequest.gameID();

            GameData game = gameDAO.getGameWithID(gameID);
            if(game.blackUsername().equals(playerColor)){
                gameDAO.updateGame(gameID, game.whiteUsername(), playerColor, game.gameName(), game.game());
            } else if (game.whiteUsername().equals(playerColor)) {
                gameDAO.updateGame(gameID, playerColor, game.blackUsername(), game.gameName(), game.game());
            }
            return new JoinGameResult(200, null);
            }

        catch (DataAccessException error) {
            return new JoinGameResult(500, "Error: " + error.getMessage());
        }
    }
}
