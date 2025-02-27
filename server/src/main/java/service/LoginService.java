package service;

import dataaccess.UserDAO;
import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import model.AuthData;
import model.UserData;

public class LoginService {

    public record LoginRequest(String username, String password){}
    public record LoginResult(int statusCode, String username, String authToken, String message){}

    private final UserDAO userDAO;
    private final AuthDAO authDAO;

    public LoginService (UserDAO userDAO, AuthDAO authDAO){
        this.userDAO = userDAO;
        this.authDAO = authDAO;
    }

    public LoginResult login(LoginRequest loginRequest){
        String username = loginRequest.username();
        String password = loginRequest.password();

        if (username == null || username.isBlank() || password == null || password.isBlank()) {
            return new LoginResult(401, null, null, "Error: unauthorized");
        }

        try {
            UserData user = userDAO.getUser(username);
            if(user == null || !user.password().equals(password)) {
                return new LoginResult(401, null, null, "Error: unauthorized");
            }

            String authToken = authDAO.createAuthToken(user);

            return new LoginResult(200, username, authToken, null);
        }

        catch (DataAccessException error) {
            return new LoginResult(500, null, null, "Error: " + error.getMessage());
        }
    }
}
