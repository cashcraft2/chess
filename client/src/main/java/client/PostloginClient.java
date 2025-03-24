package client;

import server.ServerFacade;

import java.util.Arrays;

public class PostloginClient {
    private final ServerFacade server;
    private final String serverUrl;

    public PostloginClient(String serverUrl) {
        server = new ServerFacade(serverUrl);
        this.serverUrl = serverUrl;
    }

    public String eval(String input) {
        try {
            var tokens = input.toLowerCase().split(" ");
            var cmd = (tokens.length > 0) ? tokens[0] : "help";
            var params = Arrays.copyOfRange(tokens, 1, tokens.length);

            return switch (cmd) {
                case "logout" -> logout(params);
                case "list" -> listGames(params);
                case "create" -> createGame(params);
                case "join" -> joinGame(params);
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

    private String joinGame(String[] params) {
        return null;
    }

    private String createGame(String[] params) {
        return null;
    }

    private String listGames(String[] params) {
        return null;
    }

    private String logout(String[] params) {
        return null;
    }
}
