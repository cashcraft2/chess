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
    private final Map<Integer, Integer> gameIdMap = new HashMap<>();

    public PostloginClient(String serverUrl) {
        server = new ServerFacade(serverUrl);
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
                case "spectate" -> spectateGame( authToken, params);
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
                       - spectate <game ID> 
                       - help
                """ + EscapeSequences.RESET_TEXT_ITALIC;
    }

    private String joinGame(String authToken, String... params) throws ResponseException {
        if (params.length >= 2) {
            String teamColor = params[0].toUpperCase();

            if (!"WHITE".equals(teamColor) && !"BLACK".equals(teamColor)) {
                return EscapeSequences.SET_TEXT_COLOR_RED +
                        "Error: Invalid team color. Please choose 'WHITE' or 'BLACK'." +
                        EscapeSequences.SET_TEXT_COLOR_WHITE;
            }

            int selectedGame;

            try{
                selectedGame = Integer.parseInt(params[1]);
            } catch (NumberFormatException ex) {
                return EscapeSequences.SET_TEXT_COLOR_RED +
                        "Error: Invalid game ID. It must be a number." +
                        EscapeSequences.SET_TEXT_COLOR_WHITE;
            }

            if (gameIdMap.isEmpty()) {
                listGames(authToken);
            }

            if (!gameIdMap.containsKey(selectedGame)) {
                return EscapeSequences.SET_TEXT_COLOR_RED +
                        "Error: Invalid game number. Type 'list' to see available games." +
                        EscapeSequences.SET_TEXT_COLOR_WHITE;
            }

            int actualGameId = gameIdMap.get(selectedGame);

            Collection<GameData> games = server.listGames(authToken);

            GameData chosenGame = games.stream()
                    .filter(game -> game.gameID() == actualGameId)
                    .findFirst()
                    .orElse(null);

            if (chosenGame == null) {
                return EscapeSequences.SET_TEXT_COLOR_RED +
                        "Error: Game not found. Type 'list' to see the full list of available games." +
                        EscapeSequences.SET_TEXT_COLOR_WHITE;
            }

            if ("WHITE".equals(teamColor) && chosenGame.whiteUsername() != null) {
                return EscapeSequences.SET_TEXT_COLOR_RED +
                        "Error: White team is already taken. Choose BLACK or another game." +
                        EscapeSequences.SET_TEXT_COLOR_WHITE;
            }
            if ("BLACK".equals(teamColor) && chosenGame.blackUsername() != null) {
                return EscapeSequences.SET_TEXT_COLOR_RED +
                        "Error: Black team is already taken. Choose WHITE or another game." +
                        EscapeSequences.SET_TEXT_COLOR_WHITE;
            }

            server.joinGame(teamColor, actualGameId, authToken);
            return String.format(EscapeSequences.SET_TEXT_COLOR_BLUE + "You successfully joined the game as team: %s" +
                    EscapeSequences.SET_TEXT_COLOR_WHITE, teamColor);
        }
        return EscapeSequences.SET_TEXT_COLOR_RED +
                "Error: Incorrect input. Expected: <BLACK/WHITE> <game ID>" +
                EscapeSequences.SET_TEXT_COLOR_WHITE;
    }

    private String spectateGame(String authToken, String...params) throws ResponseException {
        if (params.length < 1) {
            return EscapeSequences.SET_TEXT_COLOR_RED +
                    "Error: Invalid input. Expected: <game ID>" +
                    EscapeSequences.SET_TEXT_COLOR_WHITE;
        }
        int selectedGames;
        try {
            selectedGames = Integer.parseInt(params[0]);
        } catch (NumberFormatException ex){
            return EscapeSequences.SET_TEXT_COLOR_RED +
                    "Error: Invalid game ID. Must be a number" +
                    EscapeSequences.SET_TEXT_COLOR_WHITE;
        }
        if (gameIdMap.isEmpty()) {
            listGames(authToken);
        }

        if (!gameIdMap.containsKey(selectedGames)) {
            return EscapeSequences.SET_TEXT_COLOR_RED +
                    "Error: Game does not exist. Type 'list' to see available games." +
                    EscapeSequences.SET_TEXT_COLOR_WHITE;
        }

        int actualGameId = gameIdMap.get(selectedGames);
        Collection<GameData> games = server.listGames(authToken);

        GameData chosenGame = games.stream()
                .filter(game -> game.gameID() == actualGameId)
                .findFirst()
                .orElse(null);

        if (chosenGame == null) {
            return EscapeSequences.SET_TEXT_COLOR_RED +
                    "Error: Game does not exist. Type 'list' to see available games." +
                    EscapeSequences.SET_TEXT_COLOR_WHITE;
        }
        return EscapeSequences.SET_TEXT_COLOR_BLUE +
                "You are now spectating the game." +
                EscapeSequences.SET_TEXT_COLOR_WHITE;
    }

    private String createGame(String authToken, String... params) throws ResponseException {
        if (params.length == 1) {
            if (!params[0].trim().isEmpty()) {
                String gameName = params[0].trim();
                GameData game = new GameData
                        (null, null, null, gameName, new ChessGame());

                server.createGame(game, authToken);
                return String.format(EscapeSequences.SET_TEXT_COLOR_BLUE +
                        "You successfully created a game called: %s" + EscapeSequences.SET_TEXT_COLOR_WHITE, gameName);
            }
        }
        return EscapeSequences.SET_TEXT_COLOR_RED +
                "Error: Incorrect input. Expected: 'create <game name>'cre" +
                EscapeSequences.SET_TEXT_COLOR_WHITE;
    }

    private String listGames(String authToken) throws ResponseException {
        if(authToken != null) {
            Collection<GameData> games = server.listGames(authToken);
            if (games == null) {
                return EscapeSequences.SET_TEXT_COLOR_RED +
                        "Error: Unable to retrieve game list. Please create a game." +
                        EscapeSequences.SET_TEXT_COLOR_WHITE;
            }
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
        return EscapeSequences.SET_TEXT_COLOR_RED +
                "Error: Unauthorized access." +
                EscapeSequences.SET_TEXT_COLOR_WHITE;
    }

    private String logout(String authToken) {
        if (authToken != null) {
            try {
                server.logoutUser(authToken);
                return "return";
            }catch (Exception ex) {
                return EscapeSequences.SET_TEXT_COLOR_RED +
                        "Error: Logout failed. Please try again." +
                        EscapeSequences.SET_TEXT_COLOR_WHITE;
            }
        }
        return EscapeSequences.SET_TEXT_COLOR_RED +
                "Error: Unauthorized access." +
                EscapeSequences.SET_TEXT_COLOR_WHITE;
    }
}
