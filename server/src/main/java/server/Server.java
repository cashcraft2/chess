package server;
import handler.*;
import spark.*;

public class Server {

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        // Register your endpoints and handle exceptions here.
        createRoutes();
        //This line initializes the server and can be removed once you have a functioning endpoint 
        Spark.init();

        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }

    private static void createRoutes(){
        ClearHandler clearHandler = new ClearHandler();

        Spark.delete("/db", (request, response) -> {
            // Call the ClearHandler class and pass it the request and response. Use the common json to java object class to do the conversion
            return clearHandler.clearData(request, response);
        });
        Spark.post("/user", (request, response) -> {
            return null;
        });
    }
}
