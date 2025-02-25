package dataaccess;

import chess.ChessGame;
import model.GameData;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class MemoryGameDAO implements GameDAO {

    private final Map<Integer, GameData> games = new HashMap<>();

    @Override
    public void createGame(GameData gameData) throws DataAccessException {
        if (games.containsKey(gameData.gameID())){
            throw new DataAccessException("Error: A game with this ID already exists.");
        }
        games.put(gameData.gameID(), gameData);
    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        GameData game = games.get(gameID);
        if (game == null) {
            throw new DataAccessException("Error: Game with the given game ID does not exist.");
        }
        return game;
    }

    @Override
    public Collection<GameData> listGames() throws DataAccessException {
        if (games.isEmpty()){
            throw new DataAccessException("Error: No existing games.");
        }
        return games.values();
    }

    @Override
    public GameData updateGame(int gameID, String whiteUsername, String blackUsername, String gameName, ChessGame game) throws DataAccessException {
        GameData existingGame = games.get(gameID);
        if (existingGame == null) {
            throw new DataAccessException("The requested game does not exist.");
        }
        GameData updatedGame = new GameData(gameID, whiteUsername, blackUsername, gameName, existingGame.game());
        games.put(gameID, updatedGame);
        return updatedGame;
    }

    @Override
    public void deleteGame(GameData gameData) throws DataAccessException {
        if(!games.containsKey(gameData.gameID())){
            throw new DataAccessException("There is not current game that exists with that game ID.");
        }
        games.remove(gameData.gameID());
    }
}
