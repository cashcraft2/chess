package dataaccess;

import chess.ChessGame;
import handler.JsonHandler;
import model.GameData;
import com.google.gson.Gson;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class MySqlGameDAO implements GameDAO {
    @Override
    public void createGame(String gameName) throws DataAccessException {
        String sql = "INSERT INTO games (gameName, game) VALUES (?, ?)";
        String newSql = "UPDATE games SET gameID = ? WHERE id = ?";
        String chessGame = JsonHandler.toJson(new ChessGame());

        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            statement.setString(1, gameName);
            statement.setString(2, chessGame);
            statement.executeUpdate();

            try (ResultSet resultSet = statement.getGeneratedKeys()){
                if (resultSet.next()) {
                    int gameID = resultSet.getInt(1);

                    try (PreparedStatement newStatement = connection.prepareStatement(newSql)) {
                        newStatement.setInt(1, gameID);
                        newStatement.setInt(2, gameID);
                        newStatement.executeUpdate();
                    }
                }
                else {
                    throw new DataAccessException("Error retrieving gameID");
                }
            }
        }
        catch (SQLException ex) {
            throw new DataAccessException("Error trying to delete authData from database: " + ex.getMessage());
        }
    }

    @Override
    public GameData getGame(String gameName) throws DataAccessException {
        String sql = "SELECT gameID, whiteUsername, blackUsername, gameName, game FROM games WHERE gameName = ?";
        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, gameName);

            try (ResultSet result = statement.executeQuery()) {
                if (result.next()) {
                    int gameID = result.getInt("gameID");
                    String white = result.getString("whiteUsername");
                    String black = result.getString("blackUsername");
                    String serializedGame = result.getString("game");
                    ChessGame game = new Gson().fromJson(serializedGame, ChessGame.class);

                    return new GameData(gameID, white, black, gameName, game);
                }
            }
        }
        catch (SQLException ex) {
            throw new DataAccessException("Error finding game in database: " + ex.getMessage());
        }
        return null;
    }

    @Override
    public GameData getGameWithID(int gameID) throws DataAccessException {
        String sql = "SELECT gameID, whiteUsername, blackUsername, gameName, game WHERE gameID = ?";
        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, gameID);
            try (ResultSet result = statement.executeQuery()) {
                if (result.next()) {
                    String gameName = result.getString("gameName");
                    String white = result.getString("whiteUsername");
                    String black = result.getString("blackUsername");
                    String serializedGame = result.getString("game");
                    ChessGame game = new Gson().fromJson(serializedGame, ChessGame.class);

                    return new GameData(gameID, white, black, gameName, game);
                }
            }
        }
        catch (SQLException ex) {
            throw new DataAccessException("Error finding game in database: " + ex.getMessage());
        }
        return null;
    }

    @Override
    public Collection<GameData> listGames() throws DataAccessException {
        List<GameData> games = new ArrayList<>();
        String sql = "SELECT gameID, whiteUsername, blackUsername, gameName, game FROM games";
        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            try (ResultSet result = statement.executeQuery()) {
                while(result.next()) {
                    int gameID = result.getInt("gameID");
                    String white = result.getString("whiteUsername");
                    String black = result.getString("blackUsername");
                    String gameName = result.getString("gameName");
                    String serializedGame = result.getString("game");
                    ChessGame game = new Gson().fromJson(serializedGame, ChessGame.class);

                    games.add(new GameData(gameID, white, black, gameName, game));
                }
            }
        }
        catch (SQLException ex) {
            throw new DataAccessException("Error retrieving games from the database: " + ex.getMessage());
        }
        return games;
    }

    @Override
    public void updateGame(int gameID, String whiteUsername, String blackUsername, String gameName, ChessGame game)
            throws DataAccessException {
        String sql = """
                UPDATE games
                SET whiteUsername = ?, blackUsername = ?, gameName = ?, game = ?
                WHERE gameID = ?
                """;
        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            String serializedGame = JsonHandler.toJson(game);

            statement.setString(1, whiteUsername);
            statement.setString(2, blackUsername);
            statement.setString(3, gameName);
            statement.setString(4, serializedGame);
            statement.setInt(5, gameID);

            int rows = statement.executeUpdate();
            if (rows == 0) {
                throw new DataAccessException("Error: No game found with the given gameID " + gameID);
            }
        }
        catch (SQLException ex) {
            throw new DataAccessException("Error updating game in the database: " + ex.getMessage());
        }
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
