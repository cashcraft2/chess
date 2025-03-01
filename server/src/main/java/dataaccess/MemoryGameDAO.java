package dataaccess;

import chess.ChessGame;
import model.GameData;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class MemoryGameDAO implements GameDAO {

    private final Map<String, GameData> games = new HashMap<>();

    @Override
    public void createGame(String gameName) throws DataAccessException {
        if (games.containsKey(gameName)){
            throw new DataAccessException("A game with this name already exists.");
        }
        ChessGame board = new ChessGame();
        Random random = new Random();
        int gameId = random.nextInt(1000) + 1;
        GameData game = new GameData(gameId, null, null, gameName, board);
        games.put(gameName, game);
    }

    @Override
    public GameData getGame(String gameName){
        return games.get(gameName);
    }

    @Override
    public Collection<GameData> listGames() throws DataAccessException {
        if (games.isEmpty()){
            throw new DataAccessException("No existing games.");
        }
        return games.values();
    }

    @Override
    public GameData updateGame(int gameID, String whiteUsername, String blackUsername, String gameName, ChessGame game) throws DataAccessException {
        GameData existingGame = games.get(gameName);
        if (existingGame == null) {
            throw new DataAccessException("The requested game does not exist.");
        }
        GameData updatedGame = new GameData(gameID, whiteUsername, blackUsername, gameName, existingGame.game());
        games.put(gameName, updatedGame);
        return updatedGame;
    }

    @Override
    public void deleteGame(GameData gameData) throws DataAccessException {
        if(!games.containsKey(gameData.gameName())){
            throw new DataAccessException("There is not current game that exists with that game ID.");
        }
        games.remove(gameData.gameName());
    }

    @Override
    public void clearGameData() throws DataAccessException {
        if (games.isEmpty()){
            throw new DataAccessException("There are no games to clear from the database.");
        }
        games.clear();
    }
}
