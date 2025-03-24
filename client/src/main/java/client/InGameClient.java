package client;

import server.ServerFacade;

public class InGameClient {
    private final ServerFacade server;
    private final String serverUrl;

    public InGameClient(String serverUrl) {
        server = new ServerFacade(serverUrl);
        this.serverUrl = serverUrl;
    }

    public String eval(String input) {
        return null;
    }
}
