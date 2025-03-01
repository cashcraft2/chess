package handler;

import dataaccess.*;
import service.ClearService;
import service.ClearService.ClearResult;
import spark.Response;

public class ClearHandler {

    public Object clearData(Response response, UserDAO userDAO, AuthDAO authDAO, GameDAO gameDAO) throws DataAccessException {
        ClearService service = new ClearService(userDAO, gameDAO, authDAO);
        ClearResult result = service.clear();

        response.type("application/json");

        return JsonHandler.toJson(result);
    }
}
