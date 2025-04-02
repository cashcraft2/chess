package websocket;

import javax.websocket.Endpoint;
import javax.websocket.*;
import com.google.gson.Gson;
import com.sun.nio.sctp.NotificationHandler;
import exception.ResponseException;
import websocket.commands.UserGameCommand;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class WebSocketFacade extends Endpoint {
    Session session;
    NotificationHandler notificationHandler;

    public WebSocketFacade(String url, NotificationHandler notificationHandler) throws ResponseException {
        // Write stuff
    }

    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {
    }


}
