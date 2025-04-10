package client;

import java.util.Scanner;
import chess.ChessBoard;
import chess.ChessGame;
import exception.ResponseException;
import ui.ChessBoardRenderer;
import ui.EscapeSequences;
import websocket.WebSocketFacade;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;
import websocket.NotificationHandler;

public class Repl implements NotificationHandler {
    private final PreloginClient preClient;
    private final PostloginClient postClient;
    private InGameClient gameClient;
    private ReplState replState;
    private String authToken = null;
    private String username = null;
    private String teamColor = null;
    private Integer gameID = null;
    private ChessBoard board;
    private boolean isWhite = false;
    private WebSocketFacade ws = null;

    public Repl(String serverUrl) {
        preClient = new PreloginClient(serverUrl);
        postClient = new PostloginClient(serverUrl);
        try{
            this.ws = new WebSocketFacade(serverUrl, this);
            gameClient = new InGameClient(serverUrl, ws, this);
        } catch (ResponseException ex) {
            System.out.println("Error: " + ex.getMessage());
        }

        replState = ReplState.PRELOGIN;
    }

    public void run() {
        System.out.println(EscapeSequences.SET_TEXT_COLOR_WHITE + EscapeSequences.BLACK_QUEEN +
                EscapeSequences.SET_TEXT_COLOR_YELLOW + "Welcome to the game of Chess! Register or Login to begin."
                + EscapeSequences.SET_TEXT_COLOR_WHITE + EscapeSequences.BLACK_QUEEN);

        System.out.print(preClient.help());
        Scanner scanner = new Scanner(System.in);
        String result = "";

        while (!result.equalsIgnoreCase("quit")) {
            printPrompt();
            String line = scanner.nextLine().trim();

            try {
                switch (replState) {
                    case PRELOGIN -> result = preClient.eval(line);
                    case POSTLOGIN -> result = postClient.eval(line, authToken);
                    case INGAME -> result = gameClient.eval(line, authToken, username,
                                    teamColor, gameID, board, isWhite);
                }
                transitionRepl(result);
                System.out.print(result);
            } catch (Exception ex) {
                System.out.print(EscapeSequences.SET_TEXT_COLOR_RED + ex.getMessage() + EscapeSequences.SET_TEXT_COLOR_WHITE);
            }
        }
        System.out.println();
    }

    private void transitionRepl(String result) {

        if (result.contains("You logged in as") || result.contains("You successfully registered")) {
            authToken = preClient.getAuthToken();
            replState = ReplState.POSTLOGIN;
            System.out.print(postClient.help());
            return;
        }

        if (result.contains("You successfully joined the game as team: WHITE") ||
            result.contains("You successfully joined the game as team: BLACK") ||
            result.contains("You are now spectating the game")) {

            username = preClient.getUsername();
            teamColor = postClient.getTeamColor();
            gameID = postClient.getGameId();
            replState = ReplState.INGAME;

            System.out.println("Waiting for board state from server...");
        }


        if (result.contains("return") && replState == ReplState.POSTLOGIN) {
            replState = ReplState.PRELOGIN;
            System.out.print(EscapeSequences.SET_TEXT_COLOR_BLUE + "Successfully logged out" +
                    EscapeSequences.SET_TEXT_COLOR_WHITE);
            System.out.print(preClient.help());
        }
        if (result.contains("You left the game.") && replState == ReplState.INGAME) {
            replState = ReplState.POSTLOGIN;
            System.out.print(EscapeSequences.SET_TEXT_COLOR_BLUE + "You successfully left the game." +
                    EscapeSequences.SET_TEXT_COLOR_WHITE);
            System.out.print(postClient.help());
        }
    }

    private void printPrompt() {
        System.out.println("\n" + EscapeSequences.SET_TEXT_COLOR_GREEN + ">>> " + EscapeSequences.SET_TEXT_COLOR_WHITE);
    }

    @Override
    public void notify(ServerMessage message) {
        try {
            if (message instanceof ErrorMessage) {
                System.out.println(EscapeSequences.SET_TEXT_COLOR_RED + message);
            }
            if (message instanceof NotificationMessage) {
                System.out.println(EscapeSequences.SET_TEXT_COLOR_GREEN + message);
            }
            if (message instanceof LoadGameMessage loadGameMessage) {
                System.out.println("Received LoadGameMessage, rendering board...");
                ChessGame chessGame = loadGameMessage.getGame();

                if (chessGame != null && chessGame.getBoard() != null) {
                    this.board = chessGame.getBoard();
                    this.isWhite = teamColor == null || teamColor.equalsIgnoreCase("WHITE");

                    if(replState == ReplState.INGAME) {
                        System.out.println("Waiting for board state from server...");
                        ChessBoardRenderer.setBoard(board, isWhite);
                    }
                }
            }
            printPrompt();
        } catch (Exception e) {
            System.out.println("ERROR: " + e.getMessage());
        }
    }


    private enum ReplState {
        PRELOGIN,
        POSTLOGIN,
        INGAME
    }
}
