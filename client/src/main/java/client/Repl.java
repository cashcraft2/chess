package client;

import java.util.Scanner;
import ui.EscapeSequences;

public class Repl {
    private final PreloginClient preClient;
    private final PostloginClient postClient;
    private final InGameClient gameClient;
    private ReplState replState;
    private String authToken = null;

    public Repl(String serverUrl) {
        preClient = new PreloginClient(serverUrl);
        postClient = new PostloginClient(serverUrl);
        gameClient = new InGameClient(serverUrl);
        replState = ReplState.PRELOGIN;
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
                    continue;
                }

                if (result.equalsIgnoreCase("game started")) {
                    replState = ReplState.INGAME;
                    System.out.print(EscapeSequences.SET_TEXT_COLOR_BLUE + result +
                            EscapeSequences.SET_TEXT_COLOR_WHITE);
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
        System.out.println("\n" + EscapeSequences.SET_TEXT_COLOR_GREEN + ">>> " + EscapeSequences.SET_TEXT_COLOR_BLACK);
    }

    private enum ReplState {
        PRELOGIN,
        POSTLOGIN,
        INGAME
    }
}
