package model;

import chess.ChessGame;

public record GameData(int gameID, String whiteUsername, String blackUsername, String gameName, ChessGame game) {
    public int getGameID(int gameID){
        return this.gameID;
    }
    public String getWhiteUser(String whiteUsername){
        return this.whiteUsername;
    }
    public String getBlackUser(String blackUsername){
        return this.blackUsername;
    }
    public String getGameName(String gameName){
        return this.gameName;
    }
    public ChessGame getGame(String game){
        return this.game;
    }
    public GameData updateGame(ChessGame game){
        return new GameData(this.gameID, this.whiteUsername, this.blackUsername, this.gameName, game);
    }
    public GameData setWhiteUser(String whiteUsername){
        return new GameData(this.gameID, whiteUsername, this.blackUsername, this.gameName, this.game);
    }
    public GameData setBlackUser(String blackUsername){
        return new GameData(this.gameID, this.whiteUsername, blackUsername, this.gameName, this.game);
    }
    public GameData setGameID(int gameID){
        return new GameData(gameID, this.whiteUsername, this.blackUsername, this.gameName, this.game);
    }
    public GameData setGameName(String gameName){
        return new GameData(this.gameID, this.whiteUsername, this.blackUsername, gameName, this.game);
    }
}
