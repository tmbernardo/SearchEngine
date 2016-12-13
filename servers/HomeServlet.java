import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLEncoder;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringEscapeUtils;
import org.eclipse.jetty.util.log.Log;
import org.eclipse.jetty.util.log.Logger;

/**
 * Handles the home page of the search engine website
 *
 */
@SuppressWarnings("serial")
public class HomeServlet extends BaseServlet {
	private static Logger log = Log.getRootLogger();
	private final ConcurrentWebCrawler crawler;

	public HomeServlet(ConcurrentWebCrawler crawler) {
		super();
		this.crawler = crawler;
	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		response.setContentType("text/html");
		response.setStatus(HttpServletResponse.SC_OK);

		log.info("MessageServlet ID " + this.hashCode() + " handling GET request.");

		if (request.getParameter("logout") != null) {
			// if the user clicked logout then their cookies are deleted
			clearCookies(request, response);
		}

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
		String seedlink = request.getParameter("seedlink");

		// Avoid XSS attacks using Apache Commons StringUtils
		// Comment out if you don't have this library installed
		searchterm = StringEscapeUtils.escapeHtml4(searchterm);
		seedlink = StringEscapeUtils.escapeHtml4(seedlink);

		response.setStatus(HttpServletResponse.SC_OK);

		if (seedlink != null && !seedlink.trim().isEmpty()) {
			crawler.crawl(seedlink);
			crawler.shutdown();
			response.sendRedirect(response.encodeRedirectURL("/" + URLEncoder.encode(seedlink, "UTF-8")));
		} else if (searchterm != null && !searchterm.trim().isEmpty()) {
			response.sendRedirect(
					response.encodeRedirectURL("/search?searchterm=" + URLEncoder.encode(searchterm, "UTF-8")));
		} else {
			response.sendRedirect(response.encodeRedirectURL("/"));
		}

	}

	/**
	 * Prints the body of the Home Page. Buttons and information change per
	 * user/guest
	 *
	 * @param request
	 *            Servlet request from the particular webpage
	 * @param response
	 *            Servlet response from the particular webpage
	 */
	private void printBody(HttpServletRequest request, HttpServletResponse response) throws IOException {

		PrintWriter out = response.getWriter();

		String user = getUsername(request);
		String newPassLink = user == null ? "" : "<li><a href=\"/passchange\">Change Password</a></li>";

		// changes buttons and links to either Login or Logout respectively
		String loggedIn = user == null ? "Login" : "Logout";
		String loglink = user == null ? "/login" : "/?logout=true";

		// HTML for navbar and headers and New Crawl submission
		out.write(String.format("<div class=\"navbar navbar-default navbar-static-top\">"
				+ "      <div class=\"container\">" + "        <div class=\"navbar-header\">"
				+ "          <button type=\"button\" class=\"navbar-toggle\" data-toggle=\"collapse\" data-target=\"#navbar-ex-collapse\">"
				+ "            <span class=\"sr-only\">Toggle navigation</span>"
				+ "            <span class=\"icon-bar\"></span>" + "            <span class=\"icon-bar\"></span>"
				+ "            <span class=\"icon-bar\"></span>" + "          </button>"
				+ "          <a class=\"navbar-brand\" href=\"http://github.com/tmbernardo\"><span>Matthew Bernardo</span></a>"
				+ "        </div>" + "        <div class=\"collapse navbar-collapse\" id=\"navbar-ex-collapse\">"
				+ "          <ul class=\"nav navbar-nav navbar-right\">" + "<li>"
				+ "<form class=\"navbar-form\" role=\"index\" action=\"%s\" method=\"POST\">"
				+ "<div class=\"input-group\">"
				+ "<input type=\"text\" class=\"form-control\" placeholder=\"Enter new seed link\" name=\"seedlink\" id=\"seedlink\">"
				+ "<span class=\"input-group-btn\">"
				+ "<button type=\"submit\" class=\"btn btn-success\" type=\"submit\">Submit</button>" + "</span>"
				+ "</div>" + "</form>" + "</li>" + "            <li class=\"active\">"
				+ "              <a href=\"%s\">%s</a>" + "            </li>" + "%s" + "          </ul>"
				+ "        </div>" + "      </div>" + "    </div>", request.getServletPath(), loglink, loggedIn,
				newPassLink));

		if (request.getParameter("passchangesuccess") != null) {
			// Lets user know if the password change was a success
			out.println("<div class=\"text-center\">");
			out.println("<p class=\"alert alert-success\">Password change was successful!");
			out.println("</p>");
			out.println("</div>");
		}
		if (request.getParameter("logout") != null) {
			// Lets user know if the logout was a success
			clearCookies(request, response);
			out.println("<div class=\"text-center\">");
			out.println("<p class=\"alert alert-success\">Successfully logged out.</p>");
			out.println("</div>");
		}
		// Writes the logo
		out.write("    <div class=\"section\">" + "      <div class=\"container\">" + "        <div class=\"row\">"
				+ "          <div class=\"col-md-12\">"
				+ "            <h1 class=\"text-center\">Matt's Search Engine</h1>" + "          </div>"
				+ "        </div>");

		printForm(request, response);

		String delSearched = request.getParameter("delSearched");
		String delVisit = request.getParameter("delVisit");

		if (delSearched != null) {
			// truncates search history for a user
			dbhandler.removeSearched(user);
		} else if (user != null) {
			// prints search history for a user
			printSearchHist(out, user);
		}
		if (delVisit != null) {
			// truncates visit history for a user
			dbhandler.removeVisited(user);
		} else if (user != null) {
			// prints visit history for a user
			printVisitHist(out, user);
		}

		// prints out github logo with a link to my github :D
		out.printf("</div>" + "		</div>" + "<div class=\"section\">" + "      <div class=\"container\">"
				+ "        <div class=\"text-center\">"
				+ "          <a href=\"https://github.com/tmbernardo\"><i class=\"fa fa-3x fa-fw fa-github\"></i></a>"
				+ "        </div>" + "      </div>" + "    </div>");
	}

	/**
	 * Prints the search form for the Home Page
	 *
	 * @param request
	 *            Servlet request from the particular webpage
	 * @param response
	 *            Servlet response from the particular webpage
	 */
	public static void printForm(HttpServletRequest request, HttpServletResponse response) throws IOException {
		PrintWriter out = response.getWriter();
		out.printf("<div class=\"row\">" + "					<div class=\"col-md-offset-2 col-md-8\">"
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

	/**
	 * Prints the search history for the Home Page
	 *
	 * @param out
	 *            PrintWriter for a web page
	 * @param user
	 *            user to retrieve search history from
	 */
	private static void printSearchHist(PrintWriter out, String user) {
		Map<String, String> hist = dbhandler.getSearched(user);

		if (hist != null) {
			out.printf("<div class=\"row\"><div class=\"col-md-offset-2 col-md-8\">");
			out.printf("<h2><small>Search again:</small></h2>");
			for (String string : hist.keySet()) {
				out.printf(
						"<li><a href=\"/search?searchterm=%s\">%s</a><a class=\"text-muted\">&nbsp;&nbsp;&nbsp;&nbsp;Searched: %s</a></li>",
						string, string, hist.get(string));
			}
			out.println("<a href=\"/?delSearched=\">delete search history</a>");
			out.printf("</div></div>");
		}
	}

	/**
	 * Prints the visit history for the Home Page
	 *
	 * @param out
	 *            PrintWriter for a web page
	 * @param user
	 *            user to retrieve visit history from
	 */
	private static void printVisitHist(PrintWriter out, String user) {
		Map<String, String> hist = dbhandler.getVisit(user);

		if (hist != null) {
			out.printf("<div class=\"row\"><div class=\"col-md-offset-2 col-md-8\">");
			out.printf("<h2><small>Recently Visited Websites:</small></h2>");
			for (String string : hist.keySet()) {
				out.printf(
						"<li><a href=\"/leaving?visitLink=%s\">%s</a><a class=\"text-muted\">&nbsp;&nbsp;&nbsp;&nbsp;Visted: %s</a></li>",
						string, string, hist.get(string));
			}
			out.println("<a href=\"/?delVisit=\">delete visit history</a>");
			out.printf("</div></div>");
		}
	}
}
