package handler;

import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import service.JoinGameService;
import service.JoinGameService.JoinGameRequest;
import service.JoinGameService.JoinGameResult;
import spark.Request;
import spark.Response;

public class JoinGameHandler {
    public Object joinGame(Request request, Response response, GameDAO gameDao, AuthDAO authDAO) {
        String authToken = request.headers("authorization");

        JoinGameRequest joinGameRequest = JsonHandler.fromJson(request, JoinGameRequest.class);
        JoinGameService service = new JoinGameService(gameDao, authDAO);
        JoinGameResult result = service.join(joinGameRequest, authToken);

        response.status(result.statusCode());

        response.type("application/json");

        return JsonHandler.toJson(result);
    }
}
