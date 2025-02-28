package handler;

import dataaccess.AuthDAO;
import dataaccess.UserDAO;
import service.LoginService;
import service.LoginService.LoginRequest;
import service.LoginService.LoginResult;
import spark.Request;
import spark.Response;

public class LoginHandler {

    public Object loginUser(Request request, Response response, UserDAO userDAO, AuthDAO authDAO) {
        LoginRequest loginRequest = JsonHandler.fromJson(request, LoginRequest.class);

        LoginService service = new LoginService(userDAO, authDAO);
        LoginResult result = service.login(loginRequest);

        response.status(result.statusCode());

        response.type("application/json");

        return JsonHandler.toJson(result);
    }
}
