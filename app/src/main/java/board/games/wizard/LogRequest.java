package board.games.wizard;

import board.user.User;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

public class LogRequest {
    /**
     * Log request and its parameters.
     * @param request http request
     */
    public static void request(HttpServletRequest request, Object event) {
        if (event instanceof Map && (((Map<?, ?>) event).size() == 1))  // map of size 1 -> none event
            return;

        var buf = new StringBuilder();

        User user = (User) request.getSession().getAttribute("user");
        buf.append(user.getName()).append(" - ");

        buf.append(request.getRequestURI());

        System.out.println(buf);
    }
}
