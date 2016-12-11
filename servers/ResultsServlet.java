import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLEncoder;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringEscapeUtils;
import org.eclipse.jetty.util.log.Log;
import org.eclipse.jetty.util.log.Logger;

@SuppressWarnings("serial")
public class ResultsServlet extends HttpServlet {

	private final ConcurrentIndex index;

	private static Logger log = Log.getRootLogger();

	public ResultsServlet(ConcurrentIndex index) {
		super();
		this.index = index;
	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		response.setContentType("text/html");
		response.setStatus(HttpServletResponse.SC_OK);

		log.info("MessageServlet ID " + this.hashCode() + " handling GET request.");

		PrintWriter out = response.getWriter();
		out.printf("<html>%n%n");
		HomeServlet.printHead(request, response, "ResultPage");
		printBody(request, response, request.getParameter("searchterm"));
		out.printf("</html>%n");

		response.setStatus(HttpServletResponse.SC_OK);
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		response.setContentType("text/html");
		response.setStatus(HttpServletResponse.SC_OK);

		log.info("MessageServlet ID " + this.hashCode() + " handling POST request.");

		String searchterm = request.getParameter("searchterm");

		searchterm = searchterm == null ? "nosearchterm" : searchterm;

		// Avoid XSS attacks using Apache Commons StringUtils
		// Comment out if you don't have this library installed
		searchterm = StringEscapeUtils.escapeHtml4(searchterm);

		response.setStatus(HttpServletResponse.SC_OK);
		response.sendRedirect(
				response.encodeRedirectURL("/search?searchterm=" + URLEncoder.encode(searchterm, "UTF-8")));
	}

	private void printBody(HttpServletRequest request, HttpServletResponse response, String searchterm)
			throws IOException {
		PrintWriter out = response.getWriter();
		out.printf("<body>%n");
		out.print(String.format(
				"<div class=\"navbar navbar-default navbar-static-top\">" + "      <div class=\"container\">"
						+ "        <div class=\"navbar-header\">"
						+ "          <button type=\"button\" class=\"navbar-toggle\" data-toggle=\"collapse\" data-target=\".navbar-ex1-collapse\"></button>"
						+ "          <a class=\"navbar-brand\"><span>Matthew Bernardo</span></a>" + "        </div>"
						+ "        <div class=\"collapse navbar-collapse\" id=\"navbar-ex-collapse\">"
						+ "          <ul class=\"nav navbar-nav navbar-right\">" + "            <li class=\"active\">"
						+ "              <a href=\"/\">Home</a>" + "            </li>" + "          </ul>"
						+ "          <div class=\"col-sm-3 col-md-3 pull-right\">"
						+ "            <form class=\"navbar-form\" role=\"search\" action=\"%s\" method=\"POST\">"
						+ "              <div class=\"input-group\">"
						+ "                <input type=\"text\" class=\"form-control\" placeholder=\"Search\" name=\"searchterm\""
						+ "                id=\"searchterm\">" + "                <span class=\"input-group-btn\">"
						+ "                  <button type=\"submit\" class=\"btn btn-success\" type=\"submit\">Submit</button>"
						+ "                </span>" + "              </div>" + "            </form>"
						+ "          </div>" + "        </div>" + "      </div>" + "    </div>",
				request.getServletPath()));
		this.printResults(out, searchterm);
		out.printf("%n</body>%n");
	}

	private void printResults(PrintWriter out, String searchterm) {
		String[] queries = SearcherInterface.cleanLine(searchterm);

		out.write(String.format("<div class=\"section\">" + "      <div class=\"container\">"
				+ "        <div class=\"row\">" + "          <div class=\"col-md-12\">"
				+ "            <div class=\"page-header\">" + "              <h1>Showing Results for:"
				+ "                <small>%s</small>" + "              </h1>" + "            </div>"
				+ "          </div>" + "        </div>" + "        <div class=\"row\">"
				+ "          <div class=\"col-md-12\">" + "            <ul class=\"list-group\">",
				String.join(" ", queries)));

		List<SearchQuery> resultList = index.partialSearch(queries);
		List<SearchQuery> subList = null;

		// if (resultList.size() > 10) {
		// subList = resultList.subList(0, 9);
		// } else {
		// subList = resultList;
		// }
		subList = resultList;

		if (resultList.isEmpty()) {
			out.write(String.format("<li class=\"list-group-item\">No documents matching \"%s\" were found</li>",
					searchterm));
		} else {
			for (SearchQuery searchQuery : subList) {
				String link = searchQuery.getWhere();
				String pageTitle = getPageTitle(link);

				out.write(String.format("<li class=\"list-group-item\">" + "<a href=\"%s\">%s</a></li>", link,
						pageTitle));
			}
		}

		out.write("</ul>" + "          </div>" + "        </div>" + "      </div>" + "    </div>");
	}

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
