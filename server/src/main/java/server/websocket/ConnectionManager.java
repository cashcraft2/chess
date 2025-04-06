package server.websocket;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.eclipse.jetty.websocket.api.Session;
import websocket.messages.ServerMessage;

public class ConnectionManager {
    public final ConcurrentHashMap<Integer, Set<Connection>> connections = new ConcurrentHashMap<>();

    public void add(int gameID, String username, Session session) {
        var connection = new Connection(username, session);
        connections.computeIfAbsent(gameID, key -> ConcurrentHashMap.newKeySet()).add(connection);
    }

    public void remove(int gameID, String username) {
        var newConnections = connections.get(gameID);
        if (newConnections != null) {
            newConnections.removeIf(connection-> connection.username.equals(username));
            if (newConnections.isEmpty()) {
                connections.remove(gameID);
            }
        }
    }

    public void broadcast(int gameID, String excludedUser, ServerMessage message) throws IOException {
        var newConnections = connections.get(gameID);
        if (newConnections == null){
            return;
        }
        var removeList = new ArrayList<Connection>();

        for (var connection : newConnections) {
            if (connection.session.isOpen()) {
                if(!connection.username.equals(excludedUser)) {
                    connection.send(message.toString());
                }
            }
            else {
                removeList.add(connection);
            }
        }
        newConnections.removeAll(removeList);
        if (newConnections.isEmpty()) {
            connections.remove(gameID);
        }
    }
}
