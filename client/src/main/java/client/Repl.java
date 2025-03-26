package client;

import java.util.Scanner;
import chess.ChessBoard;
import ui.ChessBoardRenderer;
import ui.EscapeSequences;

public class Repl {
    private final PreloginClient preClient;
    private final PostloginClient postClient;
    private final InGameClient gameClient;
    private ReplState replState;
    private String authToken = null;
    private final ChessBoard board;

    public Repl(String serverUrl) {
        preClient = new PreloginClient(serverUrl);
        postClient = new PostloginClient(serverUrl);
        gameClient = new InGameClient(serverUrl);
        replState = ReplState.PRELOGIN;
        board = new ChessBoard();
        board.resetBoard();
    }

    public void run() {
        System.out.println(EscapeSequences.SET_TEXT_COLOR_WHITE + EscapeSequences.BLACK_QUEEN +
                EscapeSequences.SET_TEXT_COLOR_YELLOW + "Welcome to the game of Chess! Register or Login to begin."
                + EscapeSequences.SET_TEXT_COLOR_WHITE +  EscapeSequences.BLACK_QUEEN);

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
                    case INGAME -> result = gameClient.eval(line);
                }

                if (result.contains("You logged in as")) {
                    authToken = preClient.getAuthToken();
                    replState = ReplState.POSTLOGIN;
                    System.out.print(result);
                    System.out.print(postClient.help());
                    continue;
                }

                if (result.contains("You successfully joined the game as team: WHITE")) {
                    replState = ReplState.INGAME;
                    ChessBoardRenderer.setBoard(board, true);
                    continue;
                }
                if (result.contains("You successfully joined the game as team: BLACK")) {
                    replState = ReplState.INGAME;
                    ChessBoardRenderer.setBoard(board, false);
                    continue;
                }

                if (result.contains("return") && replState == ReplState.POSTLOGIN) {
                    replState = ReplState.PRELOGIN;
                    System.out.print(EscapeSequences.SET_TEXT_COLOR_BLUE + "Successfully logged out" +
                            EscapeSequences.SET_TEXT_COLOR_WHITE);
                    System.out.print(preClient.help());
                }

                System.out.print(result);

            } catch (Exception ex) {
                System.out.print(
                        EscapeSequences.SET_TEXT_COLOR_RED + ex.getMessage() + EscapeSequences.SET_TEXT_COLOR_WHITE);
            }
        }
        System.out.println();
    }

    private void printPrompt() {
        System.out.println("\n" + EscapeSequences.SET_TEXT_COLOR_GREEN + ">>> " + EscapeSequences.SET_TEXT_COLOR_WHITE);
    }

    private enum ReplState {
        PRELOGIN,
        POSTLOGIN,
        INGAME
    }
}
