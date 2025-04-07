package websocket.commands;

import chess.ChessMove;

public class MakeMoveCommand extends UserGameCommand{
    private final ChessMove move;
    private final String username;
    private final String teamColor;

    public MakeMoveCommand(CommandType commandType, String authToken, Integer gameID,
                           ChessMove move, String username, String teamColor) {
        super(commandType, authToken, gameID);
        this.move = move;
        this.username = username;
        this.teamColor = teamColor;
    }


    public String getUsername() {
        return username;
    }

    public ChessMove getMove() {
        return move;
    }

    public String getTeamColor() {
        return teamColor;
    }
}
