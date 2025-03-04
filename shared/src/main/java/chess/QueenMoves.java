package chess;

import java.util.List;

public class QueenMoves extends SlidingPieceMoves {

    private static final int[][] QUEEN_DIRECTIONS = {
            {1, 0}, {-1, 0}, {0, 1}, {0, -1}, // Rook-like moves
            {1, 1}, {1, -1}, {-1, 1}, {-1, -1}  // Bishop-like moves
    };

    public List<ChessMove> getValidMoves(ChessBoard board, ChessPosition position) {
        return getSlidingMoves(board, position, QUEEN_DIRECTIONS);
    }
}
