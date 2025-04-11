package websocket;

import javax.websocket.Endpoint;
import javax.websocket.*;

import chess.ChessMove;
import com.google.gson.Gson;
import exception.ResponseException;
import websocket.commands.MakeMoveCommand;
import websocket.commands.UserGameCommand;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;
import javax.websocket.MessageHandler;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class WebSocketFacade extends Endpoint {
    Session session;
    NotificationHandler notificationHandler;

    public WebSocketFacade(String url, NotificationHandler notificationHandler) throws ResponseException {
        try {
            url = url.replace("http", "ws");
            URI socketUri = new URI(url + "/ws");
            this.notificationHandler = notificationHandler;

            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            this.session = container.connectToServer(this, socketUri);

            this.session.addMessageHandler(new MessageHandler.Whole<String>() {
                @Override
                public void onMessage(String message) {
                    ServerMessage serverMessage = new Gson().fromJson(message, ServerMessage.class);

                    switch (serverMessage.getServerMessageType()) {
                        case ServerMessage.ServerMessageType.ERROR:
                            ErrorMessage errorMessage = new Gson().fromJson(message, ErrorMessage.class);
                            notificationHandler.notify(errorMessage);
                            break;
                        case ServerMessage.ServerMessageType.NOTIFICATION:
                            NotificationMessage notificationMessage =
                                    new Gson().fromJson(message, NotificationMessage.class);
                            notificationHandler.notify(notificationMessage);
                            break;
                        case ServerMessage.ServerMessageType.LOAD_GAME:
                            LoadGameMessage loadGameMessage = new Gson().fromJson(message, LoadGameMessage.class);
                            notificationHandler.notify(loadGameMessage);
                            break;
                    }
                }
            });
        }
        catch (DeploymentException | IOException | URISyntaxException ex) {
            throw new ResponseException(500, ex.getMessage());
        }
    }

    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {
    }

    public void connectToGame(String authToken, Integer gameID, String teamColor)
            throws ResponseException {
        try {
            var command = new MakeMoveCommand
                    (UserGameCommand.CommandType.CONNECT, authToken, gameID, null, teamColor);
            this.session.getBasicRemote().sendText(new Gson().toJson(command));
        }
        catch (IOException ex) {
            throw new ResponseException(500, ex.getMessage());
        }
    }

    public void makeMove(String authToken, Integer gameID, ChessMove move, String teamColor)
            throws ResponseException {
        try {
            var command = new MakeMoveCommand(UserGameCommand.CommandType.MAKE_MOVE, authToken, gameID,
                    move, teamColor);
            this.session.getBasicRemote().sendText(new Gson().toJson(command));
        }
        catch (IOException ex){
            throw new ResponseException(500, ex.getMessage());
        }
    }

    public void leaveGame(String authToken, Integer gameID, String teamColor)
            throws ResponseException {
        try {
            var command = new MakeMoveCommand(UserGameCommand.CommandType.LEAVE, authToken, gameID,
                    null, teamColor);
            this.session.getBasicRemote().sendText(new Gson().toJson(command));
        }
        catch (IOException ex) {
            throw new ResponseException(500, ex.getMessage());
        }
    }

    public void resignGame(String authToken, Integer gameID, String teamColor)
            throws ResponseException {
        try {
            var command = new MakeMoveCommand
                    (UserGameCommand.CommandType.RESIGN, authToken, gameID, null, teamColor);
            this.session.getBasicRemote().sendText(new Gson().toJson(command));
        }
        catch (IOException ex) {
            throw new ResponseException(500, ex.getMessage());
        }
    }

    public void quit() throws IOException {
        this.session.close();
    }
}
