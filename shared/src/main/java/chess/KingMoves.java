package chess;

import java.util.ArrayList;
import java.util.List;

public class KingMoves {
    public List<ChessMove> getValidMoves(ChessBoard board, ChessPosition position) {
        int currentRow = position.getRow();
        int currentCol = position.getColumn();

        if (!ChessPosition.isValidPosition(currentRow, currentCol)) {
            throw new IllegalArgumentException("Invalid Position: {" + currentRow + ", " + currentCol + "}");
        }

        List<ChessMove> validMoves = new ArrayList<>();
        ChessPiece currentPiece = board.getPiece(position);

        int[][] directions = {
                {1,0},
                {-1,0},
                {0,1},
                {0,-1},
                {1,1},
                {1,-1},
                {-1,1},
                {-1,-1}
        };

        for (int[] direction : directions) {
            int newRow = currentRow + direction[0];
            int newCol = currentCol + direction[1];

            if (!ChessPosition.isValidPosition(newRow, newCol)) {
                break;
            }
            ChessPosition newPosition = new ChessPosition(newRow, newCol);
            canMove(board, position, newPosition, validMoves, currentPiece);
        }
        return validMoves;
    }

    private void canMove(ChessBoard board, ChessPosition position, ChessPosition newPosition, List<ChessMove> validMoves, ChessPiece currentPiece) {
        ChessPiece pieceAtNewPos = board.getPiece(newPosition);

        if (pieceAtNewPos == null) {
            validMoves.add(new ChessMove(position, newPosition, null));
        }
        else if (pieceAtNewPos.getTeamColor() != currentPiece.getTeamColor()) {
            validMoves.add(new ChessMove(position, newPosition, null));
        }
    }
}