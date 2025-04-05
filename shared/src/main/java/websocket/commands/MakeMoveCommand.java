package websocket.commands;

import chess.ChessMove;

public class MakeMoveCommand {
    private final UserGameCommand.CommandType commandType;
    private final String authToken;
    private final Integer gameID;
    private final ChessMove move;
    private final String username;

    public MakeMoveCommand(UserGameCommand.CommandType commandType, String authToken, Integer gameID, ChessMove move,
                           String username) {
        this.commandType = commandType;
        this.authToken = authToken;
        this.gameID = gameID;
        this.move = move;
        this.username = username;
    }

    public String getAuthToken(){
        return authToken;
    }

    public String getUsername() {
        return username;
    }
    public Integer getGameID() {
        return gameID;
    }
    public ChessMove getMove() {
        return move;
    }
}
