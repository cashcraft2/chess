package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {

    private final ChessGame.TeamColor pieceColor;
    private final PieceType type;

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.pieceColor = pieceColor;
        this.type = type;
    }

    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {
        return pieceColor;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return type;
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        if (getPieceType() == PieceType.BISHOP) {
            BishopMoves bishopMoves = new BishopMoves();
            return bishopMoves.getValidMoves(board, myPosition);
        }
        else if (getPieceType() == PieceType.PAWN) {
            PawnMoves pawnMoves = new PawnMoves();
            return pawnMoves.getValidMoves(board, myPosition);
        }
        else if (getPieceType() == PieceType.QUEEN) {
            QueenMoves queenMoves = new QueenMoves();
            return queenMoves.getValidMoves(board, myPosition);
        }
        else if (getPieceType() == PieceType.ROOK) {
            RookMoves rookMoves = new RookMoves();
            return rookMoves.getValidMoves(board, myPosition);
        }
        else if (getPieceType() == PieceType.KNIGHT) {
            KnightMoves knightMoves = new KnightMoves();
            return knightMoves.getValidMoves(board, myPosition);
        }
        else if (getPieceType() == PieceType.KING) {
            KingMoves kingMoves = new KingMoves();
            return kingMoves.getValidMoves(board, myPosition);
        }
        else{
            return new ArrayList<>();
        }
    }

    public static boolean canMove(ChessBoard board, ChessPosition position, ChessPosition newPosition,
                                  List<ChessMove> validMoves) {
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

    public static void canMoveStep(ChessBoard board, ChessPosition position, ChessPosition newPosition,
                                   List<ChessMove> validMoves, ChessPiece currentPiece) {
        ChessPiece pieceAtNewPos = board.getPiece(newPosition);

        if (pieceAtNewPos == null) {
            validMoves.add(new ChessMove(position, newPosition, null));
        }
        else if (pieceAtNewPos.getTeamColor() != currentPiece.getTeamColor()) {
            validMoves.add(new ChessMove(position, newPosition, null));
        }
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessPiece piece = (ChessPiece) o;
        return pieceColor == piece.pieceColor && type == piece.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(pieceColor, type);
    }
}

