package client;

import chess.ChessGame;
import exception.ResponseException;
import model.GameData;
import server.ServerFacade;
import ui.EscapeSequences;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class PostloginClient {
    private final ServerFacade server;
    private final String serverUrl;
    private final Map<Integer, Integer> gameIdMap = new HashMap<>();

    public PostloginClient(String serverUrl) {
        server = new ServerFacade(serverUrl);
        this.serverUrl = serverUrl;
    }

    public String eval(String input, String authToken) {
        try {
            var tokens = input.toLowerCase().split(" ");
            var cmd = (tokens.length > 0) ? tokens[0] : "help";
            var params = Arrays.copyOfRange(tokens, 1, tokens.length);

            return switch (cmd) {
                case "logout" -> logout(authToken);
                case "list" -> listGames(authToken);
                case "create" -> createGame(authToken, params);
                case "join" -> joinGame(authToken, params);
                case "quit" -> "quit";
                default -> help();
            };
        } catch (Exception ex) {
            return ex.getMessage();
        }
    }

    public String help() {
        return EscapeSequences.SET_TEXT_ITALIC + EscapeSequences.SET_TEXT_COLOR_WHITE + """
                                    
                                    --OPTIONS--
                       - logout
                       - list
                       - create <game name>
                       - join <BLACK/WHITE> <game ID>
                """ + EscapeSequences.RESET_TEXT_ITALIC;
    }

    private String joinGame(String authToken, String... params) throws ResponseException {
        if (params.length >= 2) {
            String teamColor = params[0];
            int gameID = Integer.parseInt(params[1]);
            int selectedGame = Integer.parseInt(params[1]);

            if (!gameIdMap.containsKey(selectedGame)) {
                throw new ResponseException(400, EscapeSequences.SET_TEXT_COLOR_RED +
                        "Error: Invalid game number. Type 'list' to see available games." +
                        EscapeSequences.SET_TEXT_COLOR_WHITE);
            }

            int actualGameId = gameIdMap.get(selectedGame);

            server.joinGame(teamColor, actualGameId, authToken);
            return String.format(EscapeSequences.SET_TEXT_COLOR_BLUE + "You successfully joined the game as team: %s" +
                    EscapeSequences.SET_TEXT_COLOR_WHITE, teamColor);
        }
        throw new ResponseException(400, EscapeSequences.SET_TEXT_COLOR_RED +
                "Error: Incorrect input. Expected: <BLACK/WHITE> <game ID>" + EscapeSequences.SET_TEXT_COLOR_WHITE);
    }

    private String createGame(String authToken, String... params) throws ResponseException {
        if (params.length >= 1) {
            String gameName = params[0];
            GameData game = new GameData
                    (null, null, null, gameName, new ChessGame());

            server.createGame(game, authToken);
            return String.format(EscapeSequences.SET_TEXT_COLOR_BLUE + "You successfully created a game called: %s" +
                    EscapeSequences.SET_TEXT_COLOR_WHITE, gameName);
        }
        throw new ResponseException(400, EscapeSequences.SET_TEXT_COLOR_RED +
                "Error: Incorrect input. Expected: <game name>" + EscapeSequences.SET_TEXT_COLOR_WHITE);
    }

    private String listGames(String authToken) throws ResponseException {
        if(authToken != null) {
            Collection<GameData> games = server.listGames(authToken);
            StringBuilder result = new StringBuilder(EscapeSequences.SET_TEXT_COLOR_BLUE);
            result.append("List of games:\n");

            gameIdMap.clear();
            int index = 1;
            for (GameData game : games) {
                gameIdMap.put(index, game.gameID());
                result.append(String.format("%d. %s (White: %s, Black: %s)\n", index++,
                        game.gameName(), game.whiteUsername(), game.blackUsername()));
            }
            result.append(EscapeSequences.SET_TEXT_COLOR_WHITE);
            return result.toString();
        }
        throw new ResponseException(400, EscapeSequences.SET_TEXT_COLOR_RED +
                "Error: unauthorized" + EscapeSequences.SET_TEXT_COLOR_WHITE);
    }

    private String logout(String authToken) throws ResponseException {
        if (authToken != null) {
            server.logoutUser(authToken);
            return "return";
        }
        throw new ResponseException(400, EscapeSequences.SET_TEXT_COLOR_RED +
                "Error: unauthorized" + EscapeSequences.SET_TEXT_COLOR_WHITE);
    }
}
