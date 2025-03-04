package chess;

import java.util.List;
import java.util.ArrayList;

public class KnightMoves {
    public List<ChessMove> getValidMoves(ChessBoard board, ChessPosition position) {
        int currentRow = position.getRow();
        int currentCol = position.getColumn();

        if (!ChessPosition.isValidPosition(currentRow, currentCol)) {
            throw new IllegalArgumentException("Invalid Position: {" + currentRow + ", " + currentCol + "}");
        }

        List<ChessMove> validMoves = new ArrayList<>();
        ChessPiece currentPiece = board.getPiece(position);

        int[][] directions = {
                {2, 1},
                {2, -1},
                {-2, 1},
                {-2, -1},
                {1, 2},
                {1, -2},
                {-1, 2},
                {-1, -2}
        };

        for (int[] direction : directions) {
            int newRow = currentRow + direction[0];
            int newCol = currentCol + direction[1];

            if (ChessPosition.isValidPosition(newRow, newCol)) {
                ChessPosition newPosition = new ChessPosition(newRow, newCol);
                ChessPiece.canMoveStep(board, position, newPosition, validMoves, currentPiece);
            }
        }
        return validMoves;
    }
}
