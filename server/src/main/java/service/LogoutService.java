package service;
import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import model.AuthData;

public class LogoutService {
    public record LogoutResult(int statusCode, String message){}

    private final AuthDAO authDAO;

    public LogoutService (AuthDAO authDAO){
        this.authDAO = authDAO;
    }

    public LogoutResult logout(String authToken){
        if(authToken == null || authToken.isBlank()){
            return new LogoutResult(401, "Error: unauthorized");
        }
        try {
            AuthData authData = authDAO.getAuthData(authToken);
            if(authData == null){
                return new LogoutResult(401, "Error: unauthorized");
            }
            authDAO.deleteAuthToken(authData.authToken());

            if(!(authDAO.getAuthData(authToken) == null)) {
                return new LogoutResult(401, "Error: unauthorized");
            }
            return new LogoutResult(200, null);
        }

        catch (DataAccessException error) {
            return new LogoutResult(500, "Error: " + error.getMessage());
        }
    }
}
