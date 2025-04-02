package server.websocket;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import org.eclipse.jetty.websocket.api.Session;
import websocket.messages.ServerMessage;

public class ConnectionManager {
    public final ConcurrentHashMap<String, Connection> connections = new ConcurrentHashMap<>();

    public void add(String username, Session session) {
        var connection = new Connection(username, session);
        connections.put(username, connection);
    }

    public void remove(String username) {
        connections.remove(username);
    }

    public void broadcast(String excludedUser, ServerMessage message) throws IOException {
        var removeList = new ArrayList<Connection>();

        for (var connection : connections.values()) {
            if (connection.session.isOpen()) {
                if(!connection.username.equals(excludedUser)) {
                    connection.send(message.toString());
                }
            }
            else {
                removeList.add(connection);
            }
        }
        for (var connection : removeList) {
            connections.remove(connection.username);
        }
    }
}
