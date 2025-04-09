package service;

import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import chess.ChessGame;
import dataaccess.AuthDAO;
import model.AuthData;
import model.GameData;

public class WebsocketService {
    private final GameDAO gameDAO;
    private final AuthDAO authDAO;

    public WebsocketService(GameDAO gameDAO, AuthDAO authDAO) {
        this.gameDAO = gameDAO;
        this.authDAO = authDAO;
    }

    public GameData getGame(Integer gameID) throws DataAccessException {
        return gameDAO.getGameWithID(gameID);
    }

    public void updateGame(Integer gameID, String white, String black, String gameName, ChessGame game)
            throws DataAccessException {
        gameDAO.updateGame(gameID, white, black, gameName, game);
    }

    public AuthData getAuthData(String authToken) throws DataAccessException {
        return authDAO.getAuthData(authToken);
    }
}
