package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

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

        for (ChessMove move : possibleMoves){
            ChessPiece capturedPiece = board.getPiece(move.getEndPosition());

            board.addPiece(move.getEndPosition(), currentPiece);
            board.addPiece(move.getStartPosition(), null);

            boolean inCheck = isInCheck(currentPiece.getTeamColor());

            board.addPiece(move.getStartPosition(), currentPiece);
            board.addPiece(move.getEndPosition(), capturedPiece);

            if(!inCheck){
                legalMoves.add(move);
            }
        }
        return legalMoves;
    }



    /**
     * Makes a move in a chess game
     *
     * @param move chess move to preform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        ChessPiece currentPiece = board.getPiece(move.getStartPosition());

        if (currentPiece == null){
            throw new InvalidMoveException("No Piece at the given starting position: " + move.getStartPosition());
        }

        if (currentPiece.getTeamColor() != currentTurn) {
            throw new InvalidMoveException("It is not " + currentPiece.getTeamColor() + "'s turn!");
        }

        Collection<ChessMove> legalMoves = validMoves(move.getStartPosition());
        if (!legalMoves.contains(move)) {
            throw new InvalidMoveException("The desired move is not legal: " + move);
        }

        ChessPiece simulatePiece = board.getPiece(move.getEndPosition());
        board.addPiece(move.getEndPosition(), currentPiece);
        board.addPiece(move.getStartPosition(), null);

        if(move.getPromotionPiece() != null && currentPiece.getPieceType() == ChessPiece.PieceType.PAWN){
            board.addPiece(move.getEndPosition(), new ChessPiece(currentTurn, move.getPromotionPiece()));
        }

        if (isInCheck(currentTurn)){
            board.addPiece(move.getStartPosition(), currentPiece);
            board.addPiece(move.getEndPosition(), simulatePiece);
            throw new InvalidMoveException("Invalid move: Leaves King in check!");
        }

        if(currentTurn == TeamColor.WHITE){
            currentTurn = TeamColor.BLACK;
        }
        else {
            currentTurn = TeamColor.WHITE;
        }
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

        for (int row = 1; row < 8; row++) {
            for (int col = 1; col < 8; col++) {
                ChessPosition position = new ChessPosition(row, col);
                ChessPiece currentPiece = board.getPiece(position);

                if (currentPiece != null && currentPiece.getTeamColor() != teamColor) {
                    Collection<ChessMove> currentMoves = currentPiece.pieceMoves(board, position);

                    for(ChessMove move : currentMoves){
                        if(move.getEndPosition().equals(kingPos)) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    private ChessPosition findKing(TeamColor teamColor){
        for (int row = 1; row < 8; row++) {
            for (int col = 1; col < 8; col++){
                ChessPosition position = new ChessPosition(row, col);
                ChessPiece currentPiece = board.getPiece(position);

                if (currentPiece != null && currentPiece.getPieceType() == ChessPiece.PieceType.KING && currentPiece.getTeamColor() == teamColor) {
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
        if (!isInCheck(teamColor)) {
            return false;
        }

        ChessPosition kingPos = null;
        ChessBoard currentBoard = getBoard();
        for (int row = 0; row < 8; row++){
            for (int col = 0; col < 8; col++){
                ChessPosition currentPos = new ChessPosition(row + 1, col + 1);
                ChessPiece currentPiece = currentBoard.getPiece(currentPos);
                if (currentPiece != null && currentPiece.getPieceType() == ChessPiece.PieceType.KING && currentPiece.getTeamColor() == teamColor) {
                    kingPos = currentPos;
                    break;
                }
            }
        }
        if (kingPos == null) {
            throw new IllegalStateException("No King detected on the board.");
        }

        Collection<ChessMove> kingMoves = validMoves(kingPos);
        for (ChessMove move : kingMoves) {
            ChessPiece kingPiece = currentBoard.getPiece(kingPos);
            ChessPosition endPos = move.getEndPosition();
            ChessBoard copyBoard = currentBoard.copyBoard();
            copyBoard.addPiece(endPos, kingPiece);

            if(!isInCheck(teamColor)){
                return false;
            }
        }
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++){
                ChessPosition currentPos = new ChessPosition(row + 1, col + 1);
                ChessPiece currentPiece = currentBoard.getPiece(currentPos);
                if(currentPiece != null && currentPiece.getTeamColor() == teamColor){
                    Collection<ChessMove> legalMoves = validMoves(currentPos);
                    for (ChessMove move : legalMoves) {
                        ChessPosition endPos = move.getEndPosition();
                        ChessBoard copyBoard = currentBoard.copyBoard();
                        copyBoard.addPiece(endPos, currentPiece);

                        if(!isInCheck(teamColor)){
                            return false;
                        }
                    }
                }
            }
        }
        return true;
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        if(isInCheck(teamColor)){
            return false;
        }
        ChessBoard currentBoard = getBoard();
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                ChessPosition position = new ChessPosition(row + 1, col + 1);
                ChessPiece currentPiece = currentBoard.getPiece(position);
                if(currentPiece != null && currentPiece.getTeamColor() == teamColor){
                    Collection<ChessMove> validMoves = validMoves(position);
                    if (!validMoves.isEmpty()) {
                        return false;
                    }
                }
            }
        }
        return true;
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
