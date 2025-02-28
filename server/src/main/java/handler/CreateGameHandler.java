package handler;

import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import service.CreateGameService;
import service.CreateGameService.CreateGameRequest;
import service.CreateGameService.CreateGameResult;
import spark.Request;
import spark.Response;

public class CreateGameHandler {
    public Object createGame(Request request, Response response, GameDAO gameDao, AuthDAO authDAO) {
        String authToken = request.headers("authorization");

        CreateGameService service = new CreateGameService(gameDao, authDAO);
        CreateGameService.CreateGameResult result = service.create(authToken);

        response.status(result.statusCode());

        response.type("application/json");

        return JsonHandler.toJson(result);
    }
}
