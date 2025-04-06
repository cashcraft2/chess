package server.websocket;

import com.google.gson.Gson;
import org.eclipse.jetty.util.IO;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import websocket.commands.UserGameCommand;
import websocket.messages.ServerMessage;

import java.io.IOException;

@WebSocket
public class WebSocketHandler {
    private final ConnectionManager connections = new ConnectionManager();

    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws IOException {
        UserGameCommand command = new Gson().fromJson(message, UserGameCommand.class);
        String authToken = command.getAuthToken();
        String username = command.getUsername();
        String teamColor = command.getTeamColor();
        Integer gameID = command.getGameID();

        switch (command.getCommandType()) {
            case CONNECT -> connect(authToken, gameID, username, teamColor, session);
            case MAKE_MOVE -> makeMove(authToken, username, session);
            case LEAVE -> leave(username);
            case RESIGN -> resign(username);
        }
    }

    private void connect(String authToken, Integer gameID, String username, String teamColor, Session session) throws IOException {
        if(authToken == null) {
            throw new IOException("Error: unauthorized");
        }
        if (gameID == null) {
            throw new IOException("Error: no game found");
        }
        if (teamColor == null) {
            connections.add(username, session);
            var message = String.format("%s has joined the game as an observer", username);
            var notification = new ServerMessage(ServerMessage.ServerMessageType.LOAD_GAME, message);
            connections.broadcast(username, notification);
        }
        connections.add(username, session);
        var message = String.format("%s has joined the game as team: %s", username, teamColor);
        var notification = new ServerMessage(ServerMessage.ServerMessageType.LOAD_GAME, message);
        connections.broadcast(username, notification);
    }

    private void makeMove(String authToken, String username, Session session){

    }

    private void leave(String username){

    }

    private void resign(String username) {

    }
}
