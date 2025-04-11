package ui;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;

import java.util.Set;

public class ChessBoardRenderer {
    public static void setBoard(ChessBoard board, boolean isTeamWhite) {
        setBoardWithHighlights(board, isTeamWhite, Set.of());
    }

    public static void setBoardWithHighlights(ChessBoard board, boolean isTeamWhite, Set<ChessPosition> highlights) {
        System.out.println(EscapeSequences.ERASE_SCREEN);
        printColumnLabels(isTeamWhite);

        for (int row = 0; row < 8; row++) {
            printRowLabels(row, isTeamWhite);

            for (int col = 0; col < 8; col++) {
                int boardRow = isTeamWhite ? 8 - row : row + 1;
                int boardCol = isTeamWhite ? col + 1 : 8 - col;

                ChessPosition pos = new ChessPosition(boardRow, boardCol);
                ChessPiece piece = board.getPiece(pos);

                boolean isLightSpace = (boardRow + boardCol) % 2 != 0;
                String baseColor =
                        isLightSpace ? EscapeSequences.SET_BG_COLOR_LIGHT_GREY : EscapeSequences.SET_BG_COLOR_DARK_GREY;


                String spaceColor = highlights.contains(pos)
                        ? EscapeSequences.SET_BG_COLOR_GREEN
                        : baseColor;

                String pieceType = getPieceType(piece);
                System.out.print(spaceColor + pieceType + EscapeSequences.RESET_BG_COLOR);
            }

            printRowLabels(row, isTeamWhite);
            System.out.println();
        }

        printColumnLabels(isTeamWhite);
    }

    private static String getPieceType(ChessPiece piece) {
        if (piece == null) {
            return EscapeSequences.EMPTY;
        }

        ChessGame.TeamColor color = piece.getTeamColor();
        ChessPiece.PieceType type = piece.getPieceType();

        if (color == ChessGame.TeamColor.WHITE) {
            return switch (type) {
                case KING -> EscapeSequences.BLACK_KING;
                case QUEEN -> EscapeSequences.BLACK_QUEEN;
                case ROOK -> EscapeSequences.BLACK_ROOK;
                case BISHOP -> EscapeSequences.BLACK_BISHOP;
                case KNIGHT -> EscapeSequences.BLACK_KNIGHT;
                case PAWN -> EscapeSequences.BLACK_PAWN;
            };
        } else {
            return switch (type) {
                case KING -> EscapeSequences.WHITE_KING;
                case QUEEN -> EscapeSequences.WHITE_QUEEN;
                case ROOK -> EscapeSequences.WHITE_ROOK;
                case BISHOP -> EscapeSequences.WHITE_BISHOP;
                case KNIGHT -> EscapeSequences.WHITE_KNIGHT;
                case PAWN -> EscapeSequences.WHITE_PAWN;
            };
        }
    }

    private static void printRowLabels(int row, boolean isTeamWhite) {
        int rowNumber = isTeamWhite ? 8 - row : row + 1;
        System.out.print(EscapeSequences.SET_TEXT_COLOR_YELLOW + " " + rowNumber + " " +
                EscapeSequences.RESET_TEXT_COLOR);
    }

    private static void printColumnLabels(boolean isTeamWhite) {
        System.out.print("   ");
        System.out.print(EscapeSequences.SET_TEXT_COLOR_YELLOW);
        for (int col = 0; col < 8; col++) {
            char letter = isTeamWhite ? (char) ('a' + col) : (char) ('h' - col);
            System.out.print(" " + letter + " ");
        }
        System.out.print(EscapeSequences.RESET_TEXT_COLOR);
        System.out.println();
    }
}
