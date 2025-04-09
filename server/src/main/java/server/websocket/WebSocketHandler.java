package server.websocket;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;
import chess.InvalidMoveException;
import com.google.gson.Gson;
import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import model.AuthData;
import model.GameData;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import service.WebsocketService;
import websocket.commands.MakeMoveCommand;
import websocket.commands.UserGameCommand;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import java.io.IOException;

@WebSocket
public class WebSocketHandler {
    private final ConnectionManager connections = new ConnectionManager();
    private final GameDAO gameDAO;
    private final AuthDAO authDAO;

    public WebSocketHandler(GameDAO gameDAO, AuthDAO authDAO) {
        this.gameDAO = gameDAO;
        this.authDAO = authDAO;
    }

    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws IOException {
        UserGameCommand command = new Gson().fromJson(message, UserGameCommand.class);
        MakeMoveCommand moveCommand = new Gson().fromJson(message, MakeMoveCommand.class);
        String authToken = command.getAuthToken();
        String teamColor = moveCommand.getTeamColor();
        Integer gameID = command.getGameID();

        try {
            switch (command.getCommandType()) {
                case CONNECT -> connect(authToken, gameID, session, teamColor);
                case MAKE_MOVE -> makeMove(authToken, gameID, session, moveCommand, teamColor);
                case LEAVE -> leave(authToken, gameID, session);
                case RESIGN -> resign(gameID, authToken, session);
                default -> sendError(session, "Error: invalid command type.");
            }
        }
        catch (Exception ex) {
                sendError(session, "Error: " + ex.getMessage());
        }
    }

    private void connect(String authToken, Integer gameID, Session session, String teamColor)
            throws IOException, DataAccessException {

        WebsocketService service = accessDAO(authDAO, gameDAO);
        GameData game = service.getGame(gameID);
        AuthData authData = service.getAuthData(authToken);

        if (authData == null || authData.username() == null) {
            sendError(session, "Error: unauthorized - invalid or expired token");
            return;
        }

        String username = authData.username();
        String token = authData.authToken();

        if(token == null) {
            String message = "Error: unauthorized";
            sendError(session, message);
            return;
        }
        if (game == null) {
            String message = "Error: no game found";
            sendError(session, message);
            return;
        }
        if (game.game() == null) {
            String message = "Error: game is null inside GameData";
            sendError(session, message);
            return;
        }

        if (teamColor == null) {
            connections.add(gameID, username, session);
            var note = String.format("%s has joined the game as an spectator", username);
            var load = new LoadGameMessage(ServerMessage.ServerMessageType.LOAD_GAME, null, game.game());
            var notification = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, note);
            connections.sendToUser(gameID, username, load);
            connections.broadcast(gameID, username, notification);

        }
        else {
            connections.add(gameID, username, session);
            var message = String.format("%s has joined the game as team: %s", username, teamColor);
            var notification = new LoadGameMessage(ServerMessage.ServerMessageType.LOAD_GAME, message, game.game());
            connections.broadcast(gameID, username, notification);
        }
    }

    private void makeMove(String authToken, Integer gameID, Session session, MakeMoveCommand command, String teamColor)
            throws DataAccessException, IOException, InvalidMoveException {
        WebsocketService service = accessDAO(authDAO, gameDAO);
        AuthData authData = service.getAuthData(authToken);
        String username = authData.username();
        GameData game = service.getGame(gameID);

        if (game == null) {
            String message = "Error: The game does not exists";
            sendError(session, message);
            return;
        }

        if (!username.equals(game.whiteUsername()) && !username.equals(game.blackUsername())) {
            sendError(session, "Error: Spectators cannot make moves.");
            return;
        }
        ChessGame.TeamColor currentTurn = game.game().getTeamTurn();
        if ((currentTurn == ChessGame.TeamColor.WHITE && !username.equals(game.whiteUsername())) ||
                (currentTurn == ChessGame.TeamColor.BLACK && !username.equals(game.blackUsername()))) {
            sendError(session, "Error: It is not your turn.");
            return;
        }

        ChessMove move = command.getMove();
        ChessPosition start = move.getStartPosition();
        ChessPosition end = move.getEndPosition();
        ChessGame chessGame = game.game();

        if (chessGame.isGameOver()) {
            String message = "Cannot make any moves. The game is over!";
            sendError(session, message);
            return;
        }

        if (!chessGame.validMoves(start).contains(move)) {
            String message = "Error: Invalid move";
            sendError(session, message);
            return;
        }

        chessGame.makeMove(move);
        chessGame.determineGame(chessGame.getTeamTurn());

        service.updateGame(gameID, game.whiteUsername(), game.blackUsername(), game.gameName(), chessGame);

        var load = new LoadGameMessage(ServerMessage.ServerMessageType.LOAD_GAME, null, chessGame);
        connections.broadcast(gameID, null, load);

        String note = username + " moved from " + start + " to " + end;
        var notification = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, note);
        connections.broadcast(gameID, username, notification);

        if (chessGame.isInCheckmate(chessGame.getTeamTurn())) {
            connections.broadcast(gameID, null, 
                    new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, "Checkmate!"));
            service.updateGame(gameID, game.whiteUsername(), game.blackUsername(), game.gameName(), chessGame);
        }
        else if (chessGame.isInCheck(chessGame.getTeamTurn())) {
            connections.broadcast(gameID, null,
                    new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, "Check!"));
        }
        else if (chessGame.isInStalemate(chessGame.getTeamTurn())) {
            connections.broadcast(gameID, null,
                    new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, "Stalemate!"));
        }
    }

    private void leave(String authToken, Integer gameID, Session session) throws DataAccessException, IOException {
        WebsocketService service = accessDAO(authDAO, gameDAO);
        AuthData authData = service.getAuthData(authToken);
        String username = authData.username();
        GameData game = service.getGame(gameID);

        if (game == null) {
            String message = "Error: The game does not exists";
            sendError(session, message);
            return;
        }
        ChessGame chessGame = game.game();

        if (username.equals(game.whiteUsername())) {
             game = new GameData(gameID, null, game.blackUsername(), game.gameName(), chessGame);
        } else if (username.equals(game.blackUsername())) {
            game = new GameData(gameID, game.whiteUsername(), null, game.gameName(), chessGame);
        }
        service.updateGame(gameID, game.whiteUsername(), game.blackUsername(),
                game.gameName(), chessGame);

        connections.remove(gameID, username);

        var note = String.format("%s has left the game.", username);
        var notification = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, note);
        connections.broadcast(gameID, username, notification);
    }

    private void resign(Integer gameID, String authToken, Session session) throws DataAccessException, IOException {
        WebsocketService service = accessDAO(authDAO, gameDAO);
        AuthData authData = service.getAuthData(authToken);
        String username = authData.username();
        GameData game = service.getGame(gameID);

        if (game == null) {
            String message = "Error: The game does not exists";
            sendError(session, message);
            return;
        }

        ChessGame chessGame = game.game();

        boolean isWhite = username.equals(game.whiteUsername());
        boolean isBlack = username.equals(game.blackUsername());
        if (!isWhite && !isBlack) {
            sendError(session, "Error: Only players can resign from a game.");
            return;
        }

        if (chessGame.isGameOver()) {
            sendError(session, "Error: The game is already over.");
            return;
        }

        chessGame.resign();
        service.updateGame(gameID, game.whiteUsername(), game.blackUsername(), game.gameName(), chessGame);

        String note = String.format("%s has resigned from the game. The game is over!", username);
        var notification = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, note);
        connections.broadcast(gameID, null, notification);
    }

    private void sendError(Session session, String error) throws IOException {
        ServerMessage errorMessage = new ErrorMessage(ServerMessage.ServerMessageType.ERROR, error);
        session.getRemote().sendString(errorMessage.toString());
    }

    public WebsocketService accessDAO(AuthDAO authDAO, GameDAO gameDAO) {
        return new WebsocketService(gameDAO, authDAO);
    }
}
