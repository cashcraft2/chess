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

    public String eval(String input, String authToken, String username, String teamColor) {
        try {
            var tokens = input.toLowerCase().split(" ");
            var cmd = (tokens.length > 0) ? tokens[0] : "help";
            var params = Arrays.copyOfRange(tokens, 1, tokens.length);

            return switch (cmd) {
                case "quit" -> "quit";
                case "redraw" -> redrawBoard(authToken, username, teamColor);
                case "leave" -> leaveGame(authToken, username, teamColor);
                case "move" -> makeMove(authToken, username, teamColor);
                case "resign" -> resign(authToken, username, teamColor);
                case "highlight" -> highlightLegalMoves(authToken, username, teamColor);
                default -> help();
            };
        } catch (Exception ex) {
            return ex.getMessage();
        }
    }

    private String redrawBoard(String authToken, String username, String teamColor) {
        return null;
    }

    private String leaveGame(String authToken, String username, String teamColor) {
        return null;
    }

    private String makeMove(String authToken, String username, String teamColor) {
        return null;
    }

    private String resign(String authToken, String username, String teamColor) {
        return null;
    }

    private String highlightLegalMoves(String authToken, String username, String teamColor) {
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
