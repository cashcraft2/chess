package server;
import dataaccess.MemoryAuthDAO;
import dataaccess.MemoryGameDAO;
import dataaccess.MemoryUserDAO;
import handler.*;
import org.eclipse.jetty.util.log.Log;
import spark.*;

public class Server {

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        MemoryUserDAO userDAO = new MemoryUserDAO();
        MemoryAuthDAO authDAO = new MemoryAuthDAO();
        MemoryGameDAO gameDAO = new MemoryGameDAO();

        // Register your endpoints and handle exceptions here.
        createRoutes(userDAO, authDAO, gameDAO);
        //This line initializes the server and can be removed once you have a functioning endpoint 
        Spark.init();

        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }

    private static void createRoutes(MemoryUserDAO userDAO, MemoryAuthDAO authDAO, MemoryGameDAO gameDAO){
        ClearHandler clearHandler = new ClearHandler();
        RegisterHandler registerHandler = new RegisterHandler();
        LoginHandler loginHandler = new LoginHandler();
        LogoutHandler logoutHandler = new LogoutHandler();
        ListGamesHandler listGamesHandler = new ListGamesHandler();
        CreateGameHandler createGameHandler = new CreateGameHandler();

        Spark.delete("/db", (request, response) -> {
            // Call the ClearHandler class and pass it the request and response. Use the common json to java object class to do the conversion
            return clearHandler.clearData(request, response, userDAO, authDAO, gameDAO);
        });
        Spark.post("/user", (request, response) -> {
            return registerHandler.registerUser(request, response, userDAO, authDAO);
        });
        Spark.post("/session", (request, response) -> {
            return loginHandler.loginUser(request, response, userDAO, authDAO);
        });
        Spark.delete("/session", (request, response) -> {
            return logoutHandler.logoutUser(request, response, authDAO);
        });
        Spark.post("/game", (request, response) -> {
            return createGameHandler.createGame(request, response, gameDAO, authDAO);
        });
        Spark.get("/game", (request, response) -> {
            return listGamesHandler.listGames(request, response, gameDAO, authDAO);
        });
    }
}
