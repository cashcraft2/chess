package handler;

import service.LoginService;
import service.LoginService.LoginRequest;
import service.LoginService.LoginResult;
import spark.Request;
import spark.Response;
import dataaccess.MemoryUserDAO;
import dataaccess.MemoryAuthDAO;

public class LoginHandler {
    MemoryUserDAO userDAO = new MemoryUserDAO();
    MemoryAuthDAO authDAO = new MemoryAuthDAO();

    public Object loginUser(Request request, Response response) {
        LoginRequest loginRequest = JsonHandler.fromJson(request, LoginRequest.class);

        LoginService service = new LoginService(userDAO, authDAO);
        LoginResult result = service.login(loginRequest);

        response.status(result.statusCode());

        response.type("application/json");

        return JsonHandler.toJson(result);
    }
}
