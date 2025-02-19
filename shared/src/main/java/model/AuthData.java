package model;

public record AuthData(String authToken, String username) {
    public String getAuthToken(String authToken){
        return this.authToken;
    }
    public String getUsername(String username){
        return this.username;
    }
    public AuthData setAuthToken(String authToken){
        return new AuthData(authToken, this.username);
    }
}
