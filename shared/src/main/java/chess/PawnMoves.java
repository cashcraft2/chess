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
        ChessPiece currentPiece = board.getPiece(position);
        ChessGame.TeamColor teamColor = currentPiece.getTeamColor();

        int teamDirection;
        if (teamColor == ChessGame.TeamColor.WHITE) {
            teamDirection = 1;
        }
        else {
            teamDirection = -1;
        }

        ChessPosition moveForwardOne = new ChessPosition(currentRow + teamDirection, currentCol);

        if (board.getPiece(moveForwardOne) == null) {
            addMove(validMoves, position, moveForwardOne, currentRow + teamDirection);

            int startRow;
            if (teamColor == ChessGame.TeamColor.WHITE) {
                startRow = 2;
            }
            else {
                startRow = 7;
            }

            ChessPosition moveForwardTwo = new ChessPosition(currentRow + 2 * teamDirection, currentCol);

            if (currentRow == startRow && board.getPiece(moveForwardTwo) == null) {
                addMove(validMoves, position, moveForwardTwo, currentRow + 2 * teamDirection);
            }
        }

        int[][] captureDirections = {{teamDirection, 1}, {teamDirection, -1}};
        for (int[] direction : captureDirections) {
            ChessPosition capturePos = new ChessPosition(currentRow + direction[0], currentCol + direction[1]);
            if (ChessPosition.isValidPosition(capturePos.getRow(), capturePos.getColumn())) {
                ChessPiece pieceToCapture = board.getPiece(capturePos);
                if (pieceToCapture != null && pieceToCapture.getTeamColor() != teamColor) {
                    addMove(validMoves, position, capturePos, capturePos.getRow());
                }
            }
        }
        return validMoves;
    }

    private void addMove(List<ChessMove> moves, ChessPosition startPosition, ChessPosition endPosition, int targetRow) {
        ChessPiece.PieceType promotionQ = null;
        ChessPiece.PieceType promotionK = null;
        ChessPiece.PieceType promotionR = null;
        ChessPiece.PieceType promotionB = null;

        if (targetRow == 8 || targetRow == 1) {
            promotionQ = ChessPiece.PieceType.QUEEN;
            promotionK = ChessPiece.PieceType.KNIGHT;
            promotionR = ChessPiece.PieceType.ROOK;
            promotionB = ChessPiece.PieceType.BISHOP;

            moves.add(new ChessMove(startPosition, endPosition, promotionQ));
            moves.add(new ChessMove(startPosition, endPosition, promotionK));
            moves.add(new ChessMove(startPosition, endPosition, promotionR));
            moves.add(new ChessMove(startPosition, endPosition, promotionB));
        }
        else {
            moves.add(new ChessMove(startPosition, endPosition, null));
        }
    }
}

