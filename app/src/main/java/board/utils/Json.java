package board.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * How to construct JSON object
 * https://www.javaguides.net/2019/07/jsonsimple-tutorial-read-and-write-json-in-java.html
 */
public class Json {
    /**
     * Helper class to send JSON HTTP response
     *
     * @param resp   http response
     * @param object an object to serialize as a json
     * @throws IOException may be thrown when calling <tt>resp.getWriter()</tt>
     */
    public static void renderJson(HttpServletResponse resp, Object object) throws IOException {
        resp.setContentType("application/json;charset=UTF-8");
        resp.setHeader("Pragma", "no-cache");
        resp.setHeader("Cache-Control", "no-cache");
        resp.setDateHeader("Expires", 0);

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        resp.getWriter().println(gson.toJson(object));
    }
}
