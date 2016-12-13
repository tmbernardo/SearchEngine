import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * If a user is logged in it saves the outgoing link to the user's history then
 * redirects them to the link. otherwise guest is redirected to link.
 *
 */
@SuppressWarnings("serial")
public class VisitRecordServlet extends BaseServlet {

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
		String visit = request.getParameter("visitLink");
		String user = getUsername(request);

		if (user != null) {
			dbhandler.addVisited(user, visit, getDate());
		}
		response.sendRedirect(visit);
	}

	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
		doGet(request, response);
	}
}
