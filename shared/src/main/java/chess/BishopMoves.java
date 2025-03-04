package chess;

import java.util.List;

public class BishopMoves extends SlidingPieceMoves {

    private static final int[][] BISHOP_DIRECTIONS = {
            {1, 1}, {1, -1}, {-1, 1}, {-1, -1}
    };

    public List<ChessMove> getValidMoves(ChessBoard board, ChessPosition position) {
        return getSlidingMoves(board, position, BISHOP_DIRECTIONS);
    }
}

