package handler;

import service.ClearService;
import service.ClearService.ClearRequest;
import service.ClearService.ClearResult;
import spark.Request;
import spark.Response;

public class ClearHandler {

    public Object clearData(Request request, Response response) {
        // use the JsonHandler file to read in json text.
        // Call the ClearService and pass in the converted json data.
        ClearRequest clearRequest = JsonHandler.fromJson(request, ClearService.ClearRequest.class);

        ClearService service = new ClearService();
        ClearResult result = service.clear(clearRequest);

        response.type("application/json");
        return JsonHandler.toJson(result);
    }
}
