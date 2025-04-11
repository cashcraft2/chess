package client;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;
import exception.ResponseException;
import server.ServerFacade;
import ui.ChessBoardRenderer;
import ui.EscapeSequences;
import websocket.NotificationHandler;
import websocket.WebSocketFacade;

import java.util.Arrays;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

public class InGameClient {
    private final ServerFacade server;
    private final String serverUrl;
    private final NotificationHandler notificationHandler;
    private final WebSocketFacade ws;


    public InGameClient(String serverUrl, WebSocketFacade ws, NotificationHandler notificationHandler)
            throws ResponseException {
        server = new ServerFacade(serverUrl);
        this.serverUrl = serverUrl;
        this.notificationHandler = notificationHandler;
        this.ws = ws;
    }

    public String eval(String input, String authToken, String username, String teamColor,
                       Integer gameID, ChessBoard board, boolean isWhite, ChessGame game, boolean spect) {
        try {
            var tokens = input.toLowerCase().split(" ");
            var cmd = (tokens.length > 0) ? tokens[0] : "help";
            var params = Arrays.copyOfRange(tokens, 1, tokens.length);

            return switch (cmd) {
                case "quit" -> quitAll();
                case "redraw" -> redrawBoard(board, isWhite);
                case "leave" -> leaveGame(authToken, teamColor, gameID);
                case "move" -> makeMove(authToken, teamColor, gameID, params);
                case "resign" -> resign(authToken, teamColor, gameID, spect);
                case "highlight" -> highlightLegalMoves(game, isWhite, params);
                default -> help();
            };
        } catch (Exception ex) {
            return ex.getMessage();
        }
    }

    private String quitAll() {
        try {
            ws.quit();
            return EscapeSequences.SET_TEXT_COLOR_BLUE +
                    "You have closed your session. Please reload the application." +
                    EscapeSequences.SET_TEXT_COLOR_WHITE;
        } catch (Exception ex) {
            return EscapeSequences.SET_TEXT_COLOR_RED +
                    "Error: " + ex.getMessage() +
                    EscapeSequences.SET_TEXT_COLOR_WHITE;
        }
    }

    private String redrawBoard(ChessBoard board, boolean isWhite) {
        try {
            ChessBoardRenderer.setBoard(board, isWhite);
            return "";
        } catch (Exception ex) {
            return EscapeSequences.SET_TEXT_COLOR_RED +
                    "Error drawing board: " + ex.getMessage() +
                    EscapeSequences.SET_TEXT_COLOR_WHITE;
        }
    }

    private String leaveGame(String authToken, String teamColor, Integer gameID) {
        try {
            ws.leaveGame(authToken, gameID, teamColor);
            return EscapeSequences.SET_TEXT_COLOR_BLUE +
                    "You left the game." +
                    EscapeSequences.SET_TEXT_COLOR_WHITE;
        } catch (Exception ex) {
            return EscapeSequences.SET_TEXT_COLOR_RED +
                    "Error: " + ex.getMessage() +
                    EscapeSequences.SET_TEXT_COLOR_WHITE;
        }
    }

    private String makeMove(String authToken, String teamColor, Integer gameID, String...params) {
        try {
            if (params.length < 2) {
                return EscapeSequences.SET_TEXT_COLOR_RED +
                        "Error: move <start> <end>" +
                        EscapeSequences.SET_TEXT_COLOR_WHITE;
            }
            ChessPosition start = createPosition(params[0]);
            ChessPosition end = createPosition(params[1]);

            ChessMove move = new ChessMove(start, end, null);
            ws.makeMove(authToken, gameID, move, teamColor);
            return EscapeSequences.SET_TEXT_COLOR_BLUE +
                    "Move sent!" +
                    EscapeSequences.SET_TEXT_COLOR_WHITE;
        } catch (Exception ex) {
            return EscapeSequences.SET_TEXT_COLOR_RED +
                    "Error: " + ex.getMessage() +
                    EscapeSequences.SET_TEXT_COLOR_WHITE;
        }
    }

    private String resign(String authToken, String teamColor, Integer gameID, boolean spect) {
        if (spect){
            return EscapeSequences.SET_TEXT_COLOR_RED +
                    "Error: Spectators cannot resign from a game." +
                    EscapeSequences.SET_TEXT_COLOR_WHITE;
        }
        try {
            ws.resignGame(authToken, gameID, teamColor);
            return EscapeSequences.SET_TEXT_COLOR_BLUE +
                    "You resigned from the game." +
                    EscapeSequences.SET_TEXT_COLOR_WHITE;
        } catch (Exception ex) {
            return EscapeSequences.SET_TEXT_COLOR_RED +
                    "Error: " + ex.getMessage() +
                    EscapeSequences.SET_TEXT_COLOR_WHITE;
        }
    }

    private String highlightLegalMoves(ChessGame game, boolean isWhite, String... params) {
        try {
            if (params.length < 1) {
                return EscapeSequences.SET_TEXT_COLOR_RED +
                        "Usage: highlight <position>" +
                        EscapeSequences.SET_TEXT_COLOR_WHITE;
            }
            ChessPosition start = createPosition(params[0]);

            Collection<ChessMove> legalMoves = game.validMoves(start);

            if (legalMoves == null || legalMoves.isEmpty()) {
                return EscapeSequences.SET_TEXT_COLOR_YELLOW +
                        "No legal moves from " + params[0] +
                        EscapeSequences.SET_TEXT_COLOR_WHITE;
            }
            Set<ChessPosition> highlights = legalMoves.stream()
                    .map(ChessMove::getEndPosition).collect(Collectors.toSet());

            highlights.add(start);

            ChessBoardRenderer.setBoardWithHighlights(game.getBoard(), isWhite, highlights);
            return EscapeSequences.SET_TEXT_COLOR_BLUE +
                    "Legal moves from " + params[0] + " highlighted." +
                    EscapeSequences.SET_TEXT_COLOR_WHITE;
        } catch (Exception ex) {
            return EscapeSequences.SET_TEXT_COLOR_RED +
                    "Error: " + ex.getMessage() +
                    EscapeSequences.SET_TEXT_COLOR_WHITE;
        }
    }

    private ChessPosition createPosition(String pos) throws IllegalArgumentException {
        if (pos.length() != 2) {
            throw new IllegalArgumentException("Invalid position: " + pos);
        }

        char colChar = pos.charAt(0);
        char rowChar = pos.charAt(1);

        int col = colChar - 'a' + 1;
        int row = Character.getNumericValue(rowChar);

        if (col < 1 || col > 8 || row < 1 || row > 8) {
            throw new IllegalArgumentException("Position out of bounds: " + pos);
        }

        return new ChessPosition(row, col);
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
