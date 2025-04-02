package websocket.commands;

import chess.ChessMove;

public class MakeMoveCommand {
    private final UserGameCommand.CommandType commandType;
    private final String authToken;
    private final Integer gameID;
    private final ChessMove move;

    public MakeMoveCommand(UserGameCommand.CommandType commandType, String authToken, Integer gameID, ChessMove move) {
        this.commandType = commandType;
        this.authToken = authToken;
        this.gameID = gameID;
        this.move = move;
    }
}
