package board.template;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Unfortunately, this is kind of a hack. I found out that Jetty doesn't respect order of servlet declaration.
 * Instead, the mapping order is solely by comparator of {@link org.eclipse.jetty.http.pathmap.PathSpecGroup PathSpecGroup}.
 * See <tt>_mappings</tt> in {@link org.eclipse.jetty.http.pathmap.PathMappings PathMappings}.
 * <p>
 * The solution is following:
 * <ul>
 *     <li>the filter doesn't allow direct requesting of *.th resources, they can be only forwarded
 *     from inside another servlet</li>
 *     <li>when requesting *.th resource, the path is written into the request attribute and
 *     forwarded to <tt>/thymeleaf</tt> servlet, which is also protected from direct accessing</li>
 * </ul>
 */
public class ThymeleafFilter implements Filter {
    private static final String ALREADY_APPLIED = "thymeleaf-filter-applied";

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        Boolean alreadyApplied = (Boolean) request.getAttribute(ALREADY_APPLIED);

        HttpServletRequest req = (HttpServletRequest) request;
        String path = req.getRequestURI();

        if (alreadyApplied == null) {
            if ("/thymeleaf".equals(path)) {
                ((HttpServletResponse) response).sendError(404);
            } else {
                request.setAttribute(ALREADY_APPLIED, true);
                chain.doFilter(request, response);
            }
        } else {
            if ("/thymeleaf".equals(path)) {
                chain.doFilter(request, response);
            } else {
                req.setAttribute("thymeleaf_path", path);
                req.getServletContext().getRequestDispatcher("/thymeleaf").forward(request, response);
            }
        }
    }
}
