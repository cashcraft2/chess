package handler;

import dataaccess.AuthDAO;
import service.LoginService;
import service.LogoutService;
import service.LogoutService.LogoutRequest;
import service.LogoutService.LogoutResult;
import spark.Request;
import spark.Response;
import dataaccess.MemoryAuthDAO;

public class LogoutHandler {

    public Object logoutUser(Request request, Response response, AuthDAO authDAO) {
        String authToken = request.headers("authorization");

        LogoutService service = new LogoutService(authDAO);
        LogoutService.LogoutResult result = service.logout(authToken);

        response.status(result.statusCode());

        response.type("application/json");

        return JsonHandler.toJson(result);
    }
}
