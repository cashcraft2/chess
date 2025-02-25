package handler;

import com.google.gson.Gson;
import spark.Request;

public class JsonHandler {
    // read in json text using Gson library and parse it into a java object to be used in the different Handler classes.
    // This method should written in a way that it can be used with all my Handler classes
    private static final Gson gson = new Gson();

    public static <T> T fromJson (Request request, Class<T> currentClass) {
        return gson.fromJson(request.body(), currentClass);
    }

    public static String toJson(Object obj) {
        return gson.toJson(obj);
    }
}
