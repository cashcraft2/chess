package client;

import java.util.Scanner;
import ui.EscapeSequences.*;

public class Repl {
    //private final PreloginClient preClient;
    //private final PostloginClient postClient;
    //private final InGameClient gameClient;
    //private final ReplState replState;

    public Repl(String serverUrl) {
        //preClient = new PreloginClient(serverUrl);
        //postClient = new PostloginClient(serverUrl);
        //gameClient = new InGameClient(serverUrl);
    }

    public void run() {
        System.out.println("\u265B" +
                "\u001B[33m Welcome to the game of Chess! Type Help to get started. \u001B[0m" +  "\u265B");
        //System.out.print(preClient.help());
    }

    private enum ReplState {
        PRELOGIN,
        POSTLOGIN,
        INGAME
    }
}
