package dataaccess;

import chess.ChessGame;
import model.GameData;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
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
        String sql = "DELETE FROM games";
        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.executeUpdate();
        }
        catch (SQLException ex) {
            throw new DataAccessException("Error clearing the games table in database: " + ex.getMessage());
        }
    }
}
