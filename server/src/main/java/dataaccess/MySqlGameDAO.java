package dataaccess;

import chess.ChessGame;
import model.GameData;

import java.util.Collection;
import java.util.List;

public class MySqlGameDAO implements GameDAO {
    @Override
    public void createGame(String gameName) throws DataAccessException {

    }

    @Override
    public GameData getGame(String gameName) {
        return null;
    }

    @Override
    public GameData getGameWithID(int gameID) {
        return null;
    }

    @Override
    public Collection<GameData> listGames() throws DataAccessException {
        return List.of();
    }

    @Override
    public void updateGame(int gameID, String whiteUsername, String blackUsername, String gameName, ChessGame game) throws DataAccessException {

    }

    @Override
    public void clearGameData() throws DataAccessException {

    }
}
