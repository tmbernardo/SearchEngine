import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLEncoder;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringEscapeUtils;
import org.eclipse.jetty.util.log.Log;
import org.eclipse.jetty.util.log.Logger;

/**
 * Creates a server that displays search results. if there are no search results
 * given then it redirects to the Home Server
 *
 */
@SuppressWarnings("serial")
public class ResultsServlet extends BaseServlet {

	private final ConcurrentIndex index;

	private static Logger log = Log.getRootLogger();

	public ResultsServlet(ConcurrentIndex index) {
		super();
		this.index = index;
	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		String searchterm = request.getParameter("searchterm");

		if (searchterm != null) {
			response.setContentType("text/html");
			response.setStatus(HttpServletResponse.SC_OK);

			log.info("MessageServlet ID " + this.hashCode() + " handling GET request.");

			writeHead("ResultPage", response);
			printBody(request, response, searchterm);
			writeFinish(response);

			response.setStatus(HttpServletResponse.SC_OK);
		} else {
			response.sendRedirect(response.encodeRedirectURL("/"));
		}

	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		response.setContentType("text/html");
		response.setStatus(HttpServletResponse.SC_OK);

		log.info("MessageServlet ID " + this.hashCode() + " handling POST request.");

		String searchterm = request.getParameter("searchterm");

		searchterm = searchterm == null ? "" : searchterm;

		// Avoid XSS attacks using Apache Commons StringUtils
		// Comment out if you don't have this library installed
		searchterm = StringEscapeUtils.escapeHtml4(searchterm);

		response.setStatus(HttpServletResponse.SC_OK);
		response.sendRedirect(
				response.encodeRedirectURL("/search?searchterm=" + URLEncoder.encode(searchterm, "UTF-8")));
	}

	/**
	 * Prints the body of the Results web page.
	 *
	 * @param request
	 *            Servlet request from the particular webpage
	 * @param response
	 *            Servlet response from the particular webpage
	 * @param searchterm
	 *            search term to search index for
	 */
	private void printBody(HttpServletRequest request, HttpServletResponse response, String searchterm)
			throws IOException {
		PrintWriter out = response.getWriter();

		String user = getUsername(request);

		String loggedIn = user == null ? "Login" : "Logout";
		String loglink = user == null ? "/login" : "/?logout=";

		out.print(String.format(
				"<div class=\"navbar navbar-default navbar-static-top\">" + "      <div class=\"container\">"
						+ "        <div class=\"navbar-header\">"
						+ "          <button type=\"button\" class=\"navbar-toggle\" data-toggle=\"collapse\" data-target=\".navbar-ex1-collapse\"></button>"
						+ "          <a class=\"navbar-brand\"><span>Matthew Bernardo</span></a>" + "        </div>"
						+ "        <div class=\"collapse navbar-collapse\" id=\"navbar-ex-collapse\">"
						+ "          <ul class=\"nav navbar-nav navbar-right\">" + "            <li class=\"active\">"
						+ "              <a href=\"/\">Home</a>" + "            </li>"
						+ "<li><a href=\"%s\">%s</a> </li>       </ul>"
						+ "          <div class=\"col-sm-3 col-md-3 pull-right\">"
						+ "            <form class=\"navbar-form\" role=\"search\" action=\"%s\" method=\"POST\">"
						+ "              <div class=\"input-group\">"
						+ "                <input type=\"text\" class=\"form-control\" placeholder=\"Search\" name=\"searchterm\""
						+ "                id=\"searchterm\">" + "                <span class=\"input-group-btn\">"
						+ "                  <button type=\"submit\" class=\"btn btn-success\" type=\"submit\">Submit</button>"
						+ "                </span>" + "              </div>" + "            </form>"
						+ "          </div>" + "        </div>" + "      </div>" + "    </div>",
				loglink, loggedIn, request.getServletPath()));
		this.printResults(out, searchterm, user);
	}

	/**
	 * Prints results of the search and saves to history if user is logged in
	 *
	 * @param out
	 *            PrintWriter for current webpage
	 * @param searchterm
	 *            search term to search index for
	 * @param user
	 *            Current user if logged in
	 */
	private void printResults(PrintWriter out, String searchterm, String user) {

		String[] queries = SearcherInterface.cleanLine(searchterm);

		if (user != null) {
			dbhandler.addSearched(user, String.join(" ", queries), getDate());
		}

		out.write(String.format("<div class=\"section\">" + "      <div class=\"container\">"
				+ "        <div class=\"row\">" + "          <div class=\"col-md-12\">"
				+ "            <div class=\"page-header\">" + "              <h1>Showing Results for:"
				+ "                <small>%s</small>" + "              </h1>" + "            </div>"
				+ "          </div>" + "        </div>" + "        <div class=\"row\">"
				+ "          <div class=\"col-md-12\">" + "            <ul class=\"list-group\">",
				String.join(" ", queries)));

		List<SearchQuery> resultList = index.partialSearch(queries);

		if (resultList.isEmpty()) {
			out.write(String.format("<li class=\"list-group-item\">No documents matching \"%s\" were found</li>",
					searchterm));
		} else {
			for (SearchQuery searchQuery : resultList) {
				String link = searchQuery.getWhere();
				String pageTitle = getPageTitle(link);

				out.write(String.format(
						"<li class=\"list-group-item\">" + "<a href=\"/leaving?visitLink=%s\">%s</a></li>", link,
						pageTitle));
			}
		}

		out.write("</ul>" + "          </div>" + "        </div>" + "      </div>" + "    </div>");
	}

	/**
	 * Gets the title for a webpage
	 *
	 * @param link
	 *            link to the webpage taken from the results
	 * @return title of the web page
	 */
	private static String getPageTitle(String link) {

		String html = HTMLCleaner.fetchHTML(link);
		Pattern p = Pattern.compile("<title>\\s*?(.*?)\\s*?</title>");
		Matcher m = p.matcher(html);

		String title = "";

		while (m.find()) {
			title += m.group(1);
		}

		if (title.isEmpty()) {
			title = link;
		}

		return title;

	}
}
