package service;

import dataaccess.UserDAO;
import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import model.AuthData;
import model.UserData;

public class RegisterService {

    public record RegisterRequest(String username, String password, String email){}
    public record RegisterResult(boolean success, String username, String authToken, String message){}

    private final UserDAO userDAO;
    private final AuthDAO authDAO;

    public RegisterService (UserDAO userDAO, AuthDAO authDAO){
        this.userDAO = userDAO;
        this.authDAO = authDAO;
    }

    public RegisterResult register(RegisterRequest registerRequest) throws DataAccessException {
        try {
            String username = registerRequest.username();
            String password = registerRequest.password();
            String email = registerRequest.email();

            if (!(userDAO.getUser(username) == null)) {
                throw new DataAccessException("Error: This username already exists.");
            }
            UserData user = new UserData(username, password, email);
            userDAO.createUser(user);
            authDAO.createAuthToken(user);
            AuthData authData = authDAO.getAuthToken(username);
            String authToken = authData.authToken();

            return new RegisterResult(true, username, authToken, null);
        }
        catch (DataAccessException error) {
            return new RegisterResult(false, null, null, error.getMessage());
        }
    }
}
