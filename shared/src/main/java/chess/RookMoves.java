package chess;

import java.util.ArrayList;
import java.util.List;

public class RookMoves {
    public List<ChessMove> getValidMoves (ChessBoard board, ChessPosition position) {
        int currentRow = position.getRow();
        int currentCol = position.getColumn();

        if (!ChessPosition.isValidPosition(currentRow, currentCol)) {
            throw new IllegalArgumentException("Invalid Position: {" + currentRow + ", " + currentCol + "}");
        }
        List<ChessMove> validMoves = new ArrayList<>();

        int[][] directions = {
                {1,0},
                {-1,0},
                {0,1},
                {0,-1}
        };

        for(int[] direction : directions) {
            int row = currentRow;
            int col = currentCol;

            while (true) {
                row += direction[0];
                col += direction[1];

                if(!ChessPosition.isValidPosition(row, col)) {
                    break;
                }

                ChessPosition newPosition = new ChessPosition(row, col);

                if(!ChessPiece.canMove(board, position, newPosition, validMoves)){
                    break;
                }
            }
        }
        return validMoves;
    }
}
