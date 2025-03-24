package server;

import com.google.gson.Gson;
import exception.ResponseException;
import model.AuthData;
import model.GameData;
import model.UserData;
import java.io.*;
import java.net.*;
import java.util.Collection;

public class ServerFacade {

    private final String serverUrl;

    public ServerFacade(String url) {
        serverUrl = url;
    }

    public void clearDatabase() throws ResponseException {
        var path = "/db";
        this.makeRequest("DELETE", path, null, null, null);
    }

    public AuthData registerUser(UserData user) throws ResponseException {
        var path = "/user";
        return this.makeRequest("POST", path, user, AuthData.class, null);
    }

    public AuthData loginUser(UserData user) throws ResponseException {
        var path = "/session";
        return this.makeRequest("POST", path, user, AuthData.class, null);
    }

    public void logoutUser(String authToken) throws ResponseException {
        var path = "/session";
        this.makeRequest("DELETE", path, null, null, authToken);
    }

    public Collection<GameData> listGames(String authToken) throws ResponseException {
        var path = "/game";

        record ListGamesResponse(Collection<GameData> games){}

        var response = this.makeRequest("GET", path, null, ListGamesResponse.class, authToken);
        return response.games();
    }

    public GameData createGame(GameData game, String authToken) throws ResponseException {
        var path = "/game";
        return this.makeRequest("POST", path, game, GameData.class, authToken);
    }

    public void joinGame(String team, int gameID, String authToken) throws ResponseException {
        var path = "/game";
        record joinRequest(String playerColor, int gameID){}
        var request = new joinRequest(team, gameID);
        var response = this.makeRequest("PUT", path, request, null, authToken);
        System.out.println("Server Response: " + response);
    }

    private <T> T makeRequest(String method, String path, Object request, Class<T> responseClass, String authToken)
            throws ResponseException {
        try {
            URL url = (new URI(serverUrl + path)).toURL();
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setRequestMethod(method);
            http.setDoOutput(true);
            if(!(authToken == null) && request == null){
                http.addRequestProperty("authorization", authToken);
            } else if (!(authToken == null && request == null)) {
                http.addRequestProperty("authorization", authToken);
                writeBody(request, http);
            }else {
                writeBody(request, http);
            }
            http.connect();
            throwIfNotSuccessful(http);
            return readBody(http, responseClass);
        } catch (ResponseException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new ResponseException(500, ex.getMessage());
        }
    }


    private static void writeBody(Object request, HttpURLConnection http) throws IOException {
        if (request != null) {
            http.addRequestProperty("Content-Type", "application/json");
            String reqData = new Gson().toJson(request);
            try (OutputStream reqBody = http.getOutputStream()) {
                reqBody.write(reqData.getBytes());
            }
        }
    }

    private void throwIfNotSuccessful(HttpURLConnection http) throws IOException, ResponseException {
        var status = http.getResponseCode();
        if (!isSuccessful(status)) {
            try (InputStream respErr = http.getErrorStream()) {
                if (respErr != null) {
                    throw ResponseException.fromJson(respErr);
                }
            }

            throw new ResponseException(status, "other failure: " + status);
        }
    }

    private static <T> T readBody(HttpURLConnection http, Class<T> responseClass) throws IOException {
        T response = null;
        if (http.getContentLength() < 0) {
            try (InputStream respBody = http.getInputStream()) {
                InputStreamReader reader = new InputStreamReader(respBody);
                if (responseClass != null) {
                    response = new Gson().fromJson(reader, responseClass);
                }
            }
        }
        return response;
    }


    private boolean isSuccessful(int status) {
        return status / 100 == 2;
    }
}
