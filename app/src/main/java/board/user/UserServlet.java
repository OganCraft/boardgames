package board.user;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * User management
 */
public class UserServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String path = req.getRequestURI();
        if ("/user/login".equals(path)) {
            // just display the login form
            getServletContext().getRequestDispatcher("/th/user/login.th").forward(req, resp);
        } else if ("/user/logout".equals(path)) {
            req.getSession().invalidate();
            getServletContext().getRequestDispatcher("/internal/user/user_logged_out.html").forward(req, resp);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        UserRepository userDB = (UserRepository) req.getServletContext().getAttribute("userDb");

        String loginName = req.getParameter("loginName");
        User user = userDB.findUserByLoginName(loginName);

        if (user == null) {
            req.setAttribute("error", "User not found: " + loginName);
            getServletContext().getRequestDispatcher("/th/user/login.th").forward(req, resp);
        } else {
            req.getSession().setAttribute("user", user);
            resp.sendRedirect("/room");
        }
    }
}
