package client;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPosition;
import exception.ResponseException;
import server.ServerFacade;
import ui.ChessBoardRenderer;
import ui.EscapeSequences;
import websocket.NotificationHandler;
import websocket.WebSocketFacade;

import java.util.Arrays;

public class InGameClient {
    private final ServerFacade server;
    private final String serverUrl;
    private final NotificationHandler notificationHandler;
    private final WebSocketFacade ws;
    private ChessBoard board;
    private boolean isWhite;

    public InGameClient(String serverUrl, WebSocketFacade ws, NotificationHandler notificationHandler) throws ResponseException {
        server = new ServerFacade(serverUrl);
        this.serverUrl = serverUrl;
        this.notificationHandler = notificationHandler;
        this.ws = ws;
    }

    public String eval(String input, String authToken, String username, String teamColor,
                       Integer gameID, ChessBoard board, boolean isWhite) {
        try {
            var tokens = input.toLowerCase().split(" ");
            var cmd = (tokens.length > 0) ? tokens[0] : "help";
            var params = Arrays.copyOfRange(tokens, 1, tokens.length);

            return switch (cmd) {
                case "quit" -> "quit";
                case "redraw" -> redrawBoard(authToken, username, teamColor, board, isWhite, params);
                case "leave" -> leaveGame(authToken, username, teamColor, gameID, params);
                case "move" -> makeMove(authToken, username, teamColor, gameID, params);
                case "resign" -> resign(authToken, username, teamColor, gameID, params);
                case "highlight" -> highlightLegalMoves(authToken, username, teamColor, board, isWhite, params);
                default -> help();
            };
        } catch (Exception ex) {
            return ex.getMessage();
        }
    }

    private String redrawBoard(String authToken, String username, String teamColor,
                               ChessBoard board, boolean isWhite, String... params) {
        try {
            ChessBoardRenderer.setBoard(board, isWhite);
            return "";
        } catch (Exception ex) {
            return EscapeSequences.SET_TEXT_COLOR_RED +
                    "Error drawing board: " + ex.getMessage() +
                    EscapeSequences.SET_TEXT_COLOR_WHITE;
        }
    }

    private String leaveGame(String authToken, String username, String teamColor, Integer gameID, String... params) {
        try {
            ws.leaveGame(authToken, gameID, username, teamColor);
            return EscapeSequences.SET_TEXT_COLOR_BLUE +
                    "You left the game." +
                    EscapeSequences.SET_TEXT_COLOR_WHITE;
        } catch (Exception ex) {
            return EscapeSequences.SET_TEXT_COLOR_RED +
                    "Error: " + ex.getMessage() +
                    EscapeSequences.SET_TEXT_COLOR_WHITE;
        }
    }

    private String makeMove(String authToken, String username, String teamColor, Integer gameID, String...params) {
        try {
            if (params.length < 2) {
                return EscapeSequences.SET_TEXT_COLOR_RED +
                        "Error: move <start> <end>" +
                        EscapeSequences.SET_TEXT_COLOR_WHITE;
            }
            ChessPosition start = createPosition(params[0]);
            ChessPosition end = createPosition(params[1]);

            ChessMove move = new ChessMove(start, end, null);
            ws.makeMove(authToken, gameID, move, username, teamColor);
            return EscapeSequences.SET_TEXT_COLOR_BLUE +
                    "Move sent!" +
                    EscapeSequences.SET_TEXT_COLOR_WHITE;
        } catch (Exception ex) {
            return EscapeSequences.SET_TEXT_COLOR_RED +
                    "Error: " + ex.getMessage() +
                    EscapeSequences.SET_TEXT_COLOR_WHITE;
        }
    }

    private String resign(String authToken, String username, String teamColor, Integer gameID, String...params) {
        try {
            ws.resignGame(authToken, gameID, username, teamColor);
            return EscapeSequences.SET_TEXT_COLOR_BLUE +
                    "You resigned from the game." +
                    EscapeSequences.SET_TEXT_COLOR_WHITE;
        } catch (Exception ex) {
            return EscapeSequences.SET_TEXT_COLOR_RED +
                    "Error: " + ex.getMessage() +
                    EscapeSequences.SET_TEXT_COLOR_WHITE;
        }
    }

    private String highlightLegalMoves(String authToken, String username, String teamColor,
                                       ChessBoard board, boolean isWhite, String... params) {
        return null;
    }

    private ChessPosition createPosition(String pos) throws IllegalArgumentException {
        if (pos.length() != 2) throw new IllegalArgumentException("Invalid position: " + pos);

        char colChar = pos.charAt(0);
        char rowChar = pos.charAt(1);

        int col = colChar - 'a' + 1;
        int row = Character.getNumericValue(rowChar);

        if (col < 1 || col > 8 || row < 1 || row > 8) {
            throw new IllegalArgumentException("Position out of bounds: " + pos);
        }

        return new ChessPosition(row, col);
    }

    public void updateBoard(ChessBoard board) {
        this.board = board;
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
