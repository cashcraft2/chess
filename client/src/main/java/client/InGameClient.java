package client;

import server.ServerFacade;
import ui.EscapeSequences;
import websocket.MessageHandler;

import java.util.Arrays;

public class InGameClient {
    private final ServerFacade server;
    private final String serverUrl;
    private final MessageHandler messageHandler;

    public InGameClient(String serverUrl, MessageHandler messageHandler) {
        server = new ServerFacade(serverUrl);
        this.serverUrl = serverUrl;
        this.messageHandler = messageHandler;
    }

    public String eval(String input) {
        try {
            var tokens = input.toLowerCase().split(" ");
            var cmd = (tokens.length > 0) ? tokens[0] : "help";
            var params = Arrays.copyOfRange(tokens, 1, tokens.length);

            return switch (cmd) {
                case "quit" -> "quit";
                case "redraw" -> redrawBoard();
                case "leave" -> leaveGame();
                case "move" -> makeMove();
                case "resign" -> resign();
                case "highlight" -> highlightLegalMoves();
                default -> help();
            };
        } catch (Exception ex) {
            return ex.getMessage();
        }
    }

    private String redrawBoard() {
        return null;
    }

    private String leaveGame() {
        return null;
    }

    private String makeMove() {
        return null;
    }

    private String resign() {
        return null;
    }

    private String highlightLegalMoves() {
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
