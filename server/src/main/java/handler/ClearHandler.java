package handler;

import dataaccess.DataAccessException;
import service.ClearService;
import service.ClearService.ClearRequest;
import service.ClearService.ClearResult;
import spark.Request;
import spark.Response;
import dataaccess.MemoryUserDAO;
import dataaccess.MemoryGameDAO;
import dataaccess.MemoryAuthDAO;

public class ClearHandler {

    MemoryUserDAO userDAO = new MemoryUserDAO();
    MemoryGameDAO gameDAO = new MemoryGameDAO();
    MemoryAuthDAO authDAO = new MemoryAuthDAO();

    public Object clearData(Request request, Response response) throws DataAccessException {
        // use the JsonHandler file to read in json text.
        // Call the ClearService and pass in the converted json data.
        ClearRequest clearRequest = JsonHandler.fromJson(request, ClearService.ClearRequest.class);

        ClearService service = new ClearService(userDAO, gameDAO, authDAO);
        ClearResult result = service.clear(clearRequest);

        response.type("application/json");

        if(result.success()){
            response.status(200);
        }
        else{
            response.status(500);
        }
        return JsonHandler.toJson(result);
    }
}
