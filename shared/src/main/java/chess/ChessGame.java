package chess;

import java.util.ArrayList;
import java.util.Collection;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {
    private TeamColor currentTurn;
    private ChessBoard board;

    public ChessGame() {
        this.board = new ChessBoard();
        this.board.resetBoard();
        this.currentTurn = TeamColor.WHITE;
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return currentTurn;
    }
    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        this.currentTurn = team;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        ChessPiece currentPiece = board.getPiece(startPosition);

        if (currentPiece == null) {
            return null;
        }

        Collection<ChessMove> possibleMoves = currentPiece.pieceMoves(board, startPosition);
        Collection<ChessMove> legalMoves = new ArrayList<>();

        for (ChessMove move : possibleMoves) {
            if (isLegal(startPosition, move)) {
                legalMoves.add(move);
            }
        }
        return legalMoves;
    }

    private boolean isLegal(ChessPosition startPosition, ChessMove move) {
        ChessPosition endPos = move.getEndPosition();
        ChessPiece currentPiece = board.getPiece(startPosition);
        ChessPiece capturedPiece = board.getPiece(endPos);

        board.addPiece(endPos, currentPiece);
        board.addPiece(startPosition, null);

        try{
            return !isInCheck(currentPiece.getTeamColor());
        }
        finally {
            board.addPiece(startPosition, currentPiece);
            board.addPiece(endPos, capturedPiece);
        }
    }


    /**
     * Makes a move in a chess game
     *
     * @param move chess move to preform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        ChessPiece currentPiece = board.getPiece(move.getStartPosition());
        validateMove(move, currentPiece);

        ChessPiece capturedPiece = performMove(move, currentPiece);
        if(move.getPromotionPiece() != null){
            handlePromotion(move);
        }

        if(isInCheck(currentTurn)){
            undoMove(move, currentPiece, capturedPiece);
            throw new InvalidMoveException("Invalid Move: Leaves King in Check!");
        }
        toggleTurn();
    }

    private void validateMove(ChessMove move, ChessPiece currentPiece) throws InvalidMoveException {
        if(currentPiece == null) {
            throw new InvalidMoveException("No piece at the given start position: " + move.getStartPosition());
        }
        if(currentPiece.getTeamColor() != currentTurn) {
            throw new InvalidMoveException("It is not this player's turn.");
        }
        if (!validMoves(move.getStartPosition()).contains(move)) {
            throw new InvalidMoveException("The desired move is not legal.");
        }
    }

    private ChessPiece performMove(ChessMove move, ChessPiece currentPiece) {
        ChessPiece capturedPiece = board.getPiece(move.getEndPosition());
        board.addPiece(move.getEndPosition(), currentPiece);
        board.addPiece(move.getStartPosition(), null);
        return capturedPiece;
    }

    private void undoMove(ChessMove move, ChessPiece currentPiece, ChessPiece capturedPiece) {
        board.addPiece(move.getStartPosition(), currentPiece);
        board.addPiece(move.getEndPosition(), capturedPiece);
    }

    private void handlePromotion(ChessMove move){
        board.addPiece(move.getEndPosition(), new ChessPiece(currentTurn, move.getPromotionPiece()));
    }

    private void toggleTurn() {
        currentTurn = (currentTurn == TeamColor.WHITE) ? TeamColor.BLACK : TeamColor.WHITE;
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        ChessPosition kingPos = findKing(teamColor);

        if (kingPos == null) {
            return false;
        }
        return isInDanger(teamColor, kingPos);
    }

    private boolean isInDanger(TeamColor teamColor, ChessPosition kingPos) {
        for (int row = 1; row <= 8; row++) {
            for (int col = 1; col <= 8; col++) {
                ChessPosition position = new ChessPosition(row, col);
                ChessPiece currentPiece = board.getPiece(position);
                if (currentPiece != null && currentPiece.getTeamColor() != teamColor) {
                    if (canAttackKing(currentPiece, new ChessPosition(row, col), kingPos)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private boolean canAttackKing(ChessPiece currentPiece, ChessPosition position, ChessPosition kingPos) {
        return currentPiece.pieceMoves(board, position)
                .stream().anyMatch(move -> move.getEndPosition().equals(kingPos));
    }

    private ChessPosition findKing(TeamColor teamColor){
        for (int row = 1; row <= 8; row++) {
            for (int col = 1; col <= 8; col++){
                ChessPosition position = new ChessPosition(row, col);
                ChessPiece currentPiece = board.getPiece(position);

                if (currentPiece != null && currentPiece.getPieceType() == ChessPiece.PieceType.KING &&
                        currentPiece.getTeamColor() == teamColor) {
                    return position;
                }
            }
        }
        return null;
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        return isInCheck(teamColor) && !hasEscape(teamColor);
    }

    private boolean hasEscape(TeamColor teamColor) {
        for (int row = 1; row <= 8; row++){
            for (int col = 1; col <= 8; col++){
                ChessPosition currentPos = new ChessPosition(row, col);
                ChessPiece currentPiece = board.getPiece(currentPos);
                if (currentPiece != null &&  currentPiece.getTeamColor() == teamColor) {
                    if (!validMoves(currentPos).isEmpty()) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        return !isInCheck(teamColor) && !hasValidMoves(teamColor);
    }

    private boolean hasValidMoves(TeamColor teamColor) {
        for (int row = 1; row <= 8; row++) {
            for (int col = 1; col <= 8; col++) {
                ChessPosition position = new ChessPosition(row, col);
                ChessPiece currentPiece = board.getPiece(position);
                if(currentPiece != null && currentPiece.getTeamColor() == teamColor){
                    if(!validMoves(position).isEmpty()) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */


    public void setBoard(ChessBoard board) {
        this.board = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return board;
    }
}

