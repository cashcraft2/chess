package handler;

import com.google.gson.Gson;
import spark.Request;

public class JsonHandler {
    private static final Gson gson = new Gson();

    public static <T> T fromJson (Request request, Class<T> currentClass) {
        return gson.fromJson(request.body(), currentClass);
    }

    public static String toJson(Object obj) {
        return gson.toJson(obj);
    }
}
