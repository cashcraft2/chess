package client;

import server.ServerFacade;
import ui.EscapeSequences;
import websocket.NotificationHandler;

import java.util.Arrays;

public class InGameClient {
    private final ServerFacade server;
    private final String serverUrl;
    private final NotificationHandler messageHandler;

    public InGameClient(String serverUrl, NotificationHandler messageHandler) {
        server = new ServerFacade(serverUrl);
        this.serverUrl = serverUrl;
        this.messageHandler = messageHandler;
    }

    public String eval(String input, String authToken, String username) {
        try {
            var tokens = input.toLowerCase().split(" ");
            var cmd = (tokens.length > 0) ? tokens[0] : "help";
            var params = Arrays.copyOfRange(tokens, 1, tokens.length);

            return switch (cmd) {
                case "quit" -> "quit";
                case "redraw" -> redrawBoard(authToken, username);
                case "leave" -> leaveGame(authToken, username);
                case "move" -> makeMove(authToken, username);
                case "resign" -> resign(authToken, username);
                case "highlight" -> highlightLegalMoves(authToken, username);
                default -> help();
            };
        } catch (Exception ex) {
            return ex.getMessage();
        }
    }

    private String redrawBoard(String authToken, String username) {
        return null;
    }

    private String leaveGame(String authToken, String username) {
        return null;
    }

    private String makeMove(String authToken, String username) {
        return null;
    }

    private String resign(String authToken, String username) {
        return null;
    }

    private String highlightLegalMoves(String authToken, String username) {
        return null;
    }

    private String help() {
        return EscapeSequences.SET_TEXT_ITALIC + EscapeSequences.SET_TEXT_COLOR_WHITE + """
                                    
                                    --OPTIONS--
                       - help
                       - redraw
                       - leave
                       - move
                       - resign
                       - highlight
                       - quit
                """ + EscapeSequences.RESET_TEXT_ITALIC;
    }
}
