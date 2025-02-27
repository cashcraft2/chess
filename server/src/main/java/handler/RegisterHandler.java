package handler;

import dataaccess.AuthDAO;
import dataaccess.UserDAO;
import org.eclipse.jetty.server.Authentication;
import service.RegisterService;
import service.RegisterService.RegisterRequest;
import service.RegisterService.RegisterResult;
import spark.Request;
import spark.Response;
import dataaccess.MemoryUserDAO;
import dataaccess.MemoryAuthDAO;

public class RegisterHandler {

    public Object registerUser(Request request, Response response, UserDAO userDAO, AuthDAO authDAO) {
        RegisterRequest registerRequest = JsonHandler.fromJson(request, RegisterRequest.class);

        RegisterService service = new RegisterService(userDAO, authDAO);
        RegisterResult result = service.register(registerRequest);

        response.status(result.statusCode());

        response.type("application/json");

        return JsonHandler.toJson(result);
    }
}
