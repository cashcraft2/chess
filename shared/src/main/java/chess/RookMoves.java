package chess;

import java.util.List;

public class RookMoves extends SlidingPieceMoves {

    private static final int[][] ROOK_DIRECTIONS = {
            {1, 0}, {-1, 0}, {0, 1}, {0, -1}
    };

    public List<ChessMove> getValidMoves(ChessBoard board, ChessPosition position) {
        return getSlidingMoves(board, position, ROOK_DIRECTIONS);
    }
}

