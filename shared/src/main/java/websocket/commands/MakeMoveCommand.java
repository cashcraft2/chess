package websocket.commands;

import chess.ChessMove;

public class MakeMoveCommand extends UserGameCommand{
    private final ChessMove move;
    private final String teamColor;

    public MakeMoveCommand(CommandType commandType, String authToken, Integer gameID,
                           ChessMove move, String teamColor) {
        super(commandType, authToken, gameID);
        this.move = move;
        this.teamColor = teamColor;
    }

    public ChessMove getMove() {
        return move;
    }

    public String getTeamColor() {
        return teamColor;
    }
}
