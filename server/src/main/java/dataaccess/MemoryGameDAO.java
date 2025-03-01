package dataaccess;

import chess.ChessGame;
import model.GameData;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class MemoryGameDAO implements GameDAO {

    private final Map<String, GameData> games = new HashMap<>();
    private final Map<Integer, GameData> gamesWithID = new HashMap<>();
    private int nextGameId = 1;

    @Override
    public void createGame(String gameName) throws DataAccessException {
        if (games.containsKey(gameName)){
            throw new DataAccessException("A game with this name already exists.");
        }
        ChessGame board = new ChessGame();
        int gameId = nextGameId++;

        GameData game = new GameData(gameId, null, null, gameName, board);
        games.put(gameName, game);
        gamesWithID.put(gameId, game);
    }

    @Override
    public GameData getGame(String gameName){
        return games.get(gameName);
    }

    @Override
    public GameData getGameWithID(int gameID){
        return gamesWithID.get(gameID);
    }

    @Override
    public Collection<GameData> listGames(){
        return games.values();
    }

    @Override
    public void updateGame(int gameID, String whiteUsername, String blackUsername, String gameName, ChessGame game) throws DataAccessException {
        GameData existingGame = gamesWithID.get(gameID);
        if (existingGame == null) {
            throw new DataAccessException("The requested game does not exist.");
        }
        GameData updatedGame = new GameData(gameID, whiteUsername, blackUsername, gameName, existingGame.game());
        games.put(gameName, updatedGame);
        gamesWithID.put(gameID, updatedGame);
    }

    @Override
    public void clearGameData() {
        games.clear();
        gamesWithID.clear();
    }
}
