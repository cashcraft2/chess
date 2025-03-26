package client;

import exception.ResponseException;
import model.AuthData;
import model.UserData;
import server.ServerFacade;
import ui.EscapeSequences;



import java.util.Arrays;

public class PreloginClient {
    private final ServerFacade server;
    private final String serverUrl;
    private String authToken;

    public PreloginClient(String serverUrl) {
        server = new ServerFacade(serverUrl);
        this.serverUrl = serverUrl;
    }

    public String eval(String input) {
        try {
            var tokens = input.toLowerCase().split(" ");
            var cmd = (tokens.length > 0) ? tokens[0] : "help";
            var params = Arrays.copyOfRange(tokens, 1, tokens.length);

            return switch (cmd) {
                case "register" -> register(params);
                case "login" -> login(params);
                case "quit" -> "quit";
                default -> help();
            };
        } catch (Exception ex) {
            return ex.getMessage();
        }
    }

    public String help() {
        return EscapeSequences.SET_TEXT_ITALIC + """
                                    
                                    --OPTIONS--
                       - register <username> <password> <email>
                       - login <username> <password>
                       - quit
                """ + EscapeSequences.RESET_TEXT_ITALIC;
    }

    private String login(String... params) throws ResponseException {
        if (params.length >= 2) {
            String username = params[0];
            String password = params[1];

            try{
                UserData user = new UserData(username, password, null);
                AuthData authData = server.loginUser(user);
                this.authToken = authData.authToken();
                return String.format(EscapeSequences.SET_TEXT_COLOR_BLUE + "You logged in as %s" +
                        EscapeSequences.SET_TEXT_COLOR_WHITE, username);
            } catch (ResponseException ex) {
                return EscapeSequences.SET_TEXT_COLOR_RED +
                        "Error: Incorrect username or password" +
                        EscapeSequences.SET_TEXT_COLOR_WHITE;
            }
        }
        return EscapeSequences.SET_TEXT_COLOR_RED +
                "Error: Incorrect username or password" +
                EscapeSequences.SET_TEXT_COLOR_WHITE;
    }

    private String register(String... params) throws ResponseException {
        if (params.length >= 3) {
            String username = params[0];
            String password = params[1];
            String email = params[2];
            try {
                UserData user = new UserData(username, password, email);
                server.registerUser(user);
                return String.format(EscapeSequences.SET_TEXT_COLOR_BLUE + "You successfully registered %s" +
                        EscapeSequences.SET_TEXT_COLOR_WHITE, username);
            } catch (ResponseException ex) {
                return EscapeSequences.SET_TEXT_COLOR_RED +
                        "Error: This user already exists. Please try a different username." +
                        EscapeSequences.SET_TEXT_COLOR_WHITE;
            }
        }
        return EscapeSequences.SET_TEXT_COLOR_RED +
                "Error: Incorrect input. Expected: <username> <password> <email>" +
                EscapeSequences.SET_TEXT_COLOR_WHITE;
    }

    public String getAuthToken(){
        return authToken;
    }
}
