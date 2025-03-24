package client;

import exception.ResponseException;
import server.ServerFacade;

import java.util.Arrays;

public class PreloginClient {
    private final ServerFacade server;
    private final String serverUrl;

    public PreloginClient(String serverUrl) {
        server = new ServerFacade(serverUrl);
        this.serverUrl = serverUrl;
    }

    public String eval(String input) {
        try {
            var tokens = input.toLowerCase().split(" ");
            var cmd = (tokens.length > 0) ? tokens[0] : "help";
            var params = Arrays.copyOfRange(tokens, 1, tokens.length);

            return switch (cmd) {
                case "register" -> register(params);
                case "login" -> login(params);
                case "quit" -> "quit";
                default -> help();
            };
        } catch (Exception ex) {
            return ex.getMessage();
        }
    }

    private String help() {
        return null;
    }

    private String login(String[] params) {
        return null;
    }

    private String register(String[] params) {
        return null;
    }
}
