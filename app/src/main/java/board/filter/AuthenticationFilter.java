package board.filter;

import board.user.User;
import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.io.IOException;

public class AuthenticationFilter implements Filter {
    public static final String ALREADY_APPLIED = "auth-filter-applied";

    /**
     * Identify publicly accessible resources. No security needed.
     * @param path request path
     * @return <tt>true</tt> if the path is available even if a user is not logged in
     */
    private boolean publicAccess(String path) {
        if (path.startsWith("/user/"))
            return true;

        if (path.equals("/") /*|| path.equals("/index.html")*/)
            return true;

        // todo: maybe include only resources required to correctly display login form
        // todo: other resources may be behind the security
        // fixme: /wizard/whatever.jpg produces NPE!
        if (path.endsWith(".css") || path.endsWith(".jpg"))
            return true;

        return false;
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        Boolean alreadyApplied = (Boolean) request.getAttribute(ALREADY_APPLIED);
        if (alreadyApplied != null && alreadyApplied) {
            chain.doFilter(servletRequest, servletResponse);
            return;
        }
        servletRequest.setAttribute(ALREADY_APPLIED, true); // prevention of endless loop during forwarding

        String path = request.getRequestURI();

        if (publicAccess(path)) {
            chain.doFilter(servletRequest, servletResponse);
            return;
        }

        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");

        if (user == null) {
            System.out.println("user is not logged in");
            response.sendRedirect("/user/login");
            return;
        } else {
            System.out.println("user is logged in: " + user);
        }

        chain.doFilter(servletRequest, servletResponse);
    }
}
