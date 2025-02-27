package service;

import dataaccess.UserDAO;
import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import model.AuthData;
import model.UserData;

public class RegisterService {

    public record RegisterRequest(String username, String password, String email){}
    public record RegisterResult(int statusCode, String username, String authToken, String message){}

    private final UserDAO userDAO;
    private final AuthDAO authDAO;

    public RegisterService (UserDAO userDAO, AuthDAO authDAO){
        this.userDAO = userDAO;
        this.authDAO = authDAO;
    }

    public RegisterResult register(RegisterRequest registerRequest){
        String username = registerRequest.username();
        String password = registerRequest.password();
        String email = registerRequest.email();

        if (username == null || username.isBlank() || password == null || password.isBlank() || email == null || email.isBlank()) {
            return new RegisterResult(400, null, null, "Error: bad request");
        }

        try {
            if (userDAO.getUser(username) != null) {
                return new RegisterResult(403, null, null, "Error: already taken");
            }
            UserData user = new UserData(username, password, email);
            userDAO.createUser(user);
            authDAO.createAuthToken(user);
            AuthData authData = authDAO.getAuthToken(username);
            String authToken = authData.authToken();

            return new RegisterResult(200, username, authToken, null);
        }

        catch (DataAccessException error) {
            return new RegisterResult(500, null, null, "Error: " + error.getMessage());
        }
    }
}
