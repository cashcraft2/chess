package chess;


import java.util.ArrayList;
import java.util.List;

public class BishopMoves {

    public List<ChessMove> getValidMoves(ChessBoard board, ChessPosition position) {
        int currentRow = position.getRow();
        int currentCol = position.getColumn();

        if (!ChessPosition.isValidPosition(currentRow, currentCol)) {
            throw new IllegalArgumentException("Invalid position : <" + currentRow + "," + currentCol + ">.");
        }

        List<ChessMove> validMoves = new ArrayList<>();

        int[][] directions = {
                {1,1},
                {1,-1},
                {-1,1},
                {-1,-1}
        };

        for (int[] direction : directions) {
            int row = currentRow;
            int col = currentCol;

            while (true){
                row += direction[0];
                col += direction[1];

                if (!ChessPosition.isValidPosition(row, col)) {
                    break;
                }

                ChessPosition newPosition = new ChessPosition(row, col);

                if (!canMove(board, position, newPosition, validMoves)) {
                    break;
                }
            }
        }
        return validMoves;
    }


    private boolean canMove(ChessBoard board, ChessPosition position, ChessPosition newPosition, List<ChessMove> validMoves) {

        ChessPiece pieceAtNewPos = board.getPiece(newPosition);
        ChessPiece currentPiece = board.getPiece(position);

        if (pieceAtNewPos == null) {
            validMoves.add(new ChessMove(position, newPosition, null));
            return true;
        }
        else if (pieceAtNewPos.getTeamColor() != currentPiece.getTeamColor()) {
            validMoves.add(new ChessMove(position, newPosition, null));
            return false;
        }
        else {
            return false;
        }
    }
}
