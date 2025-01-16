package chess;


import java.util.ArrayList;
import java.util.List;

public class BishopMoves {

    public List<ChessPosition> getValidMoves(ChessPosition position) {
        int currentRow = position.getRow();
        int currentCol = position.getColumn();

        if (!position.isValidPosition(currentRow, currentCol)) {
            throw new IllegalArgumentException("Invalid position : <" + currentRow + "," + currentCol + ">.");
        }

        List<ChessPosition> validMoves = new ArrayList<ChessPosition>();

        // Diagonal movement up and to the right
        for (int i = 1; position.isValidPosition(currentRow + i, currentCol + i); i++) {
            validMoves.add(new ChessPosition(currentRow + i, currentCol + i));
        }

        // Diagonal movement up and to the left
        for (int i = 1; position.isValidPosition(currentRow + i, currentCol - 1); i++) {
            validMoves.add(new ChessPosition(currentRow + i, currentCol - i));
        }

        // Diagonal movement down and to the right
        for (int i = 1; position.isValidPosition(currentRow - i, currentCol + i); i++) {
            validMoves.add(new ChessPosition(currentRow - i, currentCol + i));
        }

        // Diagonal movement down and to the left
        for (int i = 1; position.isValidPosition(currentRow - i, currentCol - i); i++) {
            validMoves.add(new ChessPosition(currentRow - i, currentCol - i));
        }
        return validMoves;
    }
}
