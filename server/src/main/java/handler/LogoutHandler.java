package handler;

import dataaccess.AuthDAO;
import service.LogoutService;
import spark.Request;
import spark.Response;


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
