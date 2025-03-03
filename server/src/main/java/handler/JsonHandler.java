package handler;

import com.google.gson.Gson;
import spark.Request;

public class JsonHandler {
    private static final Gson GSON = new Gson();

    public static <T> T fromJson (Request request, Class<T> currentClass) {
        return GSON.fromJson(request.body(), currentClass);
    }

    public static String toJson(Object obj) {
        return GSON.toJson(obj);
    }
}
