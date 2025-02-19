import chess.*;
import server.Server;
import spark.*;

public class Main {
    public static void main(String[] args) {
        new Server().run(8080);

        var piece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
        System.out.println("♕ 240 Chess Server: " + piece);
    }
}