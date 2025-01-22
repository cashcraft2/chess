package chess;

import java.util.ArrayList;
import java.util.List;

public class PawnMoves {
    public List<ChessMove> getValidMoves(ChessBoard board, ChessPosition position) {
        int currentRow = position.getRow();
        int currentCol = position.getColumn();

        if (!ChessPosition.isValidPosition(currentRow, currentCol)){
            throw new IllegalArgumentException("Invalid Position: {" + currentRow + ", " + currentCol + "}");
        }

        List<ChessMove> validMoves = new ArrayList<>();

        int[] normalDirections = {1,0};
        int[][] captureDirections = {{1,1}, {1,-1}};

        int row = currentRow;
        int col = currentCol;

        row += normalDirections[0];

        ChessPosition newPosition = new ChessPosition(row, col);

        canMove(board, position, newPosition, validMoves);



        return validMoves;

    }
    private boolean canMove(ChessBoard board, ChessPosition position, ChessPosition newPosition, List<ChessMove> validMoves) {
        ChessPiece pieceAtNewPos = board.getPiece(newPosition);
        ChessPiece currentPiece = board.getPiece(position);

        if (pieceAtNewPos == null) {
            validMoves.add(new ChessMove(position, newPosition, null));
            return true;
        }
        else {
            return false;
        }
    }
}

