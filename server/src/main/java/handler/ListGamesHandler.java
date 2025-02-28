package handler;

import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import dataaccess.UserDAO;
import service.ListGamesService;
import service.ListGamesService.ListGamesRequest;
import service.ListGamesService.ListGamesResult;
import spark.Request;
import spark.Response;

public class ListGamesHandler {
    public Object listGames(Request request, Response response, GameDAO gameDao, AuthDAO authDAO) {
        String authToken = request.headers("authorization");

        ListGamesService service = new ListGamesService(gameDao, authDAO);
        ListGamesService.ListGamesResult result = service.list(authToken);

        response.status(result.statusCode());

        response.type("application/json");

        return JsonHandler.toJson(result);
    }
}
