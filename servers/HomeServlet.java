import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLEncoder;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringEscapeUtils;
import org.eclipse.jetty.util.log.Log;
import org.eclipse.jetty.util.log.Logger;

//TODO clean up request/response parameters to take in PrintWriter out instead
//TODO clean up all html
@SuppressWarnings("serial")
public class HomeServlet extends BaseServlet {
	private static Logger log = Log.getRootLogger();

	public HomeServlet() {
		super();
	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		response.setContentType("text/html");
		response.setStatus(HttpServletResponse.SC_OK);

		log.info("MessageServlet ID " + this.hashCode() + " handling GET request.");

		writeHead("Homepage", response);
		printBody(request, response);
		writeFinish(response);

		response.setStatus(HttpServletResponse.SC_OK);
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		response.setContentType("text/html");
		response.setStatus(HttpServletResponse.SC_OK);

		log.info("MessageServlet ID " + this.hashCode() + " handling POST request.");

		String searchterm = request.getParameter("searchterm");

		// Avoid XSS attacks using Apache Commons StringUtils
		// Comment out if you don't have this library installed
		searchterm = StringEscapeUtils.escapeHtml4(searchterm);

		response.setStatus(HttpServletResponse.SC_OK);

		if (searchterm != null && searchterm.trim() != "") {
			response.sendRedirect(
					response.encodeRedirectURL("/search?searchterm=" + URLEncoder.encode(searchterm, "UTF-8")));
		} else {
			response.sendRedirect(response.encodeRedirectURL("/" + URLEncoder.encode(searchterm, "UTF-8")));
		}

	}

	private void printBody(HttpServletRequest request, HttpServletResponse response) throws IOException {
		PrintWriter out = response.getWriter();
		String user = getUsername(request);
		System.out.println(user);

		String loggedIn = user == null ? "Login" : "Logout";
		String loglink = user == null ? "/login" : "/login?logout=";

		out.write(String.format("<div class=\"navbar navbar-default navbar-static-top\">"
				+ "      <div class=\"container\">" + "        <div class=\"navbar-header\">"
				+ "          <button type=\"button\" class=\"navbar-toggle\" data-toggle=\"collapse\" data-target=\"#navbar-ex-collapse\">"
				+ "            <span class=\"sr-only\">Toggle navigation</span>"
				+ "            <span class=\"icon-bar\"></span>" + "            <span class=\"icon-bar\"></span>"
				+ "            <span class=\"icon-bar\"></span>" + "          </button>"
				+ "          <a class=\"navbar-brand\" href=\"http://github.com/tmbernardo\"><span>Matthew Bernardo</span></a>"
				+ "        </div>" + "        <div class=\"collapse navbar-collapse\" id=\"navbar-ex-collapse\">"
				+ "          <ul class=\"nav navbar-nav navbar-right\">" + "            <li class=\"active\">"
				+ "              <a href=\"%s\">%s</a>" + "            </li>" + "            <li>"
				+ "              <a href=\"#\">Enter new seed</a>" + "            </li>" + "          </ul>"
				+ "        </div>" + "      </div>" + "    </div>" + "    <div class=\"section\">"
				+ "      <div class=\"container\">" + "        <div class=\"row\">"
				+ "          <div class=\"col-md-12\">" + "            <h1 class=\"text-center\">Search</h1>"
				+ "          </div>" + "        </div>", loglink, loggedIn));
		printForm(request, response);
		String delCookies = request.getParameter("delcookies");
		if (user != null && delCookies == null) {
			out.println("<a href=\"/?delcookies=\">delete cookies</a>");
		}
		if (delCookies != null) {
			dbhandler.removeHist(user);
		}

		out.printf("</div>" + "		</div>" + "<div class=\"section\">" + "      <div class=\"container\">"
				+ "        <div class=\"text-center\">"
				+ "          <a href=\"https://github.com/tmbernardo\"><i class=\"fa fa-3x fa-fw fa-github\"></i></a>"
				+ "        </div>" + "      </div>" + "    </div>");
	}

	public static void printForm(HttpServletRequest request, HttpServletResponse response) throws IOException {
		PrintWriter out = response.getWriter();
		out.printf("<div class=\"row\">" + "					<div class=\"col-md-offset-3 col-md-6\">"
				+ "						<form role=\"form\" action=\"%s\" method=\"POST\" class=\"form-horizontal\">"
				+ "							<div class=\"form-group\">"
				+ "								<div class=\"input-group\">"
				+ "									<input type=\"text\" class=\"form-control\" name=\"searchterm\" placeholder=\"Enter your search query\">"
				+ "									<span class=\"input-group-btn\">"
				+ "										<button type=\"submit\" class=\"btn btn-success\" type=\"submit\">Submit</button>"
				+ "									</span>" + "								</div>"
				+ "							</div>" + "						</form>" + "					</div>"
				+ "				</div>", request.getServletPath());
	}
}
