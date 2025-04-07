package websocket.messages;

import chess.ChessGame;

public class LoadGameMessage extends ServerMessage {
    private final ChessGame game;
    private final String message;

    public LoadGameMessage(ServerMessageType type, String message, ChessGame game) {
        super(type);
        this.game = game;
        this.message = message;
    }

    public ChessGame getGame() {
        return game;
    }

    public String getMessage() {
        return message;
    }
}
