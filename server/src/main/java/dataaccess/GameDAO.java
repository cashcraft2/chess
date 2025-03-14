package dataaccess;
import chess.ChessGame;
import model.GameData;

import java.util.Collection;

public interface GameDAO {
    void createGame(String gameName) throws DataAccessException;

    GameData getGame(String gameName) throws DataAccessException;

    GameData getGameWithID(int gameID) throws DataAccessException;

    Collection<GameData> listGames() throws DataAccessException;

    void updateGame(int gameID, String whiteUsername, String blackUsername, String gameName, ChessGame game) throws DataAccessException;

    void clearGameData() throws DataAccessException;
}
