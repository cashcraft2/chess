package service;

import dataaccess.UserDAO;
import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import model.AuthData;
import model.UserData;

import java.util.UUID;

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
            if (!userDAO.verifyUser(username, password, user)) {
                return new LoginResult(401, null, null, "Error: unauthorized");
            }
            String authToken = generateToken();
            AuthData newAuthData = new AuthData(authToken, user.username());
            authDAO.createAuthToken(newAuthData);

            return new LoginResult(200, username, authToken, null);
        }

        catch (DataAccessException error) {
            return new LoginResult(500, null, null, "Error: " + error.getMessage());
        }
    }

    public static String generateToken() {
        return UUID.randomUUID().toString();
    }
}
