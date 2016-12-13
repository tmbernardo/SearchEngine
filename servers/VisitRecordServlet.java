import java.io.IOException;
import java.net.URLEncoder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Redirects to welcome page or login page depending on whether user session is
 * detected.
 *
 * @see LoginServer
 */
@SuppressWarnings("serial")
public class VisitRecordServlet extends BaseServlet {

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
		String visit = request.getParameter("visitLink");

		String user = getUsername(request);

		if (user != null) {
			String title = request.getParameter("title");
			title = URLEncoder.encode(title, "UTF-8");
			dbhandler.addVisited(user, visit, getDate());
		}
		response.sendRedirect(visit);
	}

	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
		doGet(request, response);
	}
}
