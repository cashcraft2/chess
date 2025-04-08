package server.websocket;

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
                case CONNECT -> connect(authToken, gameID, teamColor, session);
                case MAKE_MOVE -> makeMove(authToken, session);
                case LEAVE -> leave();
                case RESIGN -> resign();
                default -> sendError(session, "Error: invalid command type.");
            }
        }
        catch (Exception ex) {
                sendError(session, "Error: " + ex.getMessage());
        }
    }

    private void connect(String authToken, Integer gameID, String teamColor, Session session)
            throws IOException, DataAccessException {

        WebsocketService service = accessDAO(authDAO, gameDAO);
        GameData game = service.getGame(gameID);
        AuthData authData = service.getAuthData(authToken);
        String username = authData.username();
        String token = authData.authToken();

        if(token == null) {
            throw new IOException("Error: unauthorized");
        }
        if (game == null) {
            throw new IOException("Error: no game found");
        }

        if (teamColor == null) {
            connections.add(gameID, username, session);
            var message = String.format("%s has joined the game as an observer", username);
            var notification = new LoadGameMessage(ServerMessage.ServerMessageType.LOAD_GAME, message, game.game());
            connections.broadcast(gameID, username, notification);
        }
        else {
            connections.add(gameID, username, session);
            var message = String.format("%s has joined the game as team: %s", username, teamColor);
            var notification = new LoadGameMessage(ServerMessage.ServerMessageType.LOAD_GAME, message, game.game());
            connections.broadcast(gameID, username, notification);
        }
    }

    private void makeMove(String authToken, Session session){

    }

    private void leave(){
        
    }

    private void resign() {

    }

    private void sendError(Session session, String error) throws IOException {
        ServerMessage errorMessage = new ErrorMessage(ServerMessage.ServerMessageType.ERROR, error);
        session.getRemote().sendString(errorMessage.toString());
    }

    public WebsocketService accessDAO(AuthDAO authDAO, GameDAO gameDAO) {
        return new WebsocketService(gameDAO, authDAO);
    }
}
