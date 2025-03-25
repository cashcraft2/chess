package ui;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;

public class ChessBoardRenderer {
    public static void setBoard(ChessBoard board, boolean isTeamWhite) {
        System.out.println(EscapeSequences.ERASE_SCREEN);

        printColumnLabels(isTeamWhite);

        for (int row = 0; row < 8; row++) {
            printRowLabels(row, isTeamWhite);

            for (int col = 0; col < 8; col++) {
                int boardRow;
                int boardCol;

                if (isTeamWhite) {
                    boardRow = 8 - row;
                    boardCol = col + 1;
                }
                else {
                    boardRow = row + 1;
                    boardCol = 8 - col;
                }
                boolean isLightSpace = (boardRow + boardCol) % 2 == 0;
                String spaceColor = isLightSpace ? EscapeSequences.SET_BG_COLOR_LIGHT_GREY :
                        EscapeSequences.SET_BG_COLOR_DARK_GREY;

                ChessPosition pos = new ChessPosition(boardRow, boardCol);
                ChessPiece piece = board.getPiece(pos);

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
            switch(type) {
                case KING: return EscapeSequences.BLACK_KING;
                case QUEEN: return EscapeSequences.BLACK_QUEEN;
                case ROOK: return EscapeSequences.BLACK_ROOK;
                case BISHOP: return EscapeSequences.BLACK_BISHOP;
                case KNIGHT: return EscapeSequences.BLACK_KNIGHT;
                case PAWN: return EscapeSequences.BLACK_PAWN;
            }
        }
        else {
            switch (type) {
                case KING: return EscapeSequences.WHITE_KING;
                case QUEEN: return EscapeSequences.WHITE_QUEEN;
                case ROOK: return EscapeSequences.WHITE_ROOK;
                case BISHOP: return EscapeSequences.WHITE_BISHOP;
                case KNIGHT: return EscapeSequences.WHITE_KNIGHT;
                case PAWN: return EscapeSequences.WHITE_PAWN;
            }
        }
        return EscapeSequences.EMPTY;
    }

    private static void printRowLabels(int row, boolean isTeamWhite) {
        int rowNumber;
        if (isTeamWhite) {
            rowNumber = 8 - row;
        }
        else {
            rowNumber = row + 1;
        }
        System.out.print
                (EscapeSequences.SET_TEXT_COLOR_YELLOW + " " + rowNumber + " " + EscapeSequences.RESET_TEXT_COLOR);
    }

    private static void printColumnLabels(boolean isTeamWhite) {
        System.out.print("   ");
        System.out.print(EscapeSequences.SET_TEXT_COLOR_YELLOW);
        for (int col = 0; col < 8; col ++) {
            char letter;
            if (isTeamWhite) {
                letter = (char) ('a' + col);
            }
            else {
                letter = (char) ('h' - col);
            }
            System.out.print(" " + letter + " ");
        }
        System.out.print(EscapeSequences.RESET_TEXT_COLOR);
        System.out.println();
    }
}


