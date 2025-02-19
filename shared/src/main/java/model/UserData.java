package model;

public record UserData(String username, String password, String email) {
    public String getUsername(String username){
        return this.username;
    }
    public String getPassword(String password){
        return this.password;
    }
    public String getEmail(String email){
        return this.email;
    }
}
