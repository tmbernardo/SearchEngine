import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLEncoder;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringEscapeUtils;
import org.eclipse.jetty.util.log.Log;
import org.eclipse.jetty.util.log.Logger;

@SuppressWarnings("serial")
public class HomeServlet extends HttpServlet {
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

		PrintWriter out = response.getWriter();
		out.printf("<html>%n%n");
		printHead(request, response, "Homepage");
		printBody(request, response);
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

	public static void printHead(HttpServletRequest request, HttpServletResponse response, String title)
			throws IOException {
		PrintWriter out = response.getWriter();

		out.printf("	<head>%n");
		out.printf("	<title>%s</title>%n", title);
		out.printf("	<meta charset=\"utf-8\">"
				+ "		<meta name=\"viewport\" content=\"width=device-width, initial-scale=1\">"
				+ "		<script type=\"text/javascript\" src=\"http://cdnjs.cloudflare.com/ajax/libs/jquery/2.0.3/jquery.min.js\"></script>"
				+ "		<script type=\"text/javascript\" src=\"http://netdna.bootstrapcdn.com/bootstrap/3.3.4/js/bootstrap.min.js\"></script>"
				+ "		<link href=\"http://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.3.0/css/font-awesome.min.css\" rel=\"stylesheet\" type=\"text/css\">"
				+ "		<link href=\"http://pingendo.github.io/pingendo-bootstrap/themes/default/bootstrap.css\" rel=\"stylesheet\" type=\"text/css\">");
		out.printf("<head>%n");
	}

	private static void printBody(HttpServletRequest request, HttpServletResponse response) throws IOException {
		PrintWriter out = response.getWriter();
		out.printf("<body>%n");

		out.printf("<div class=\"navbar navbar-default navbar-static-top\">" + "      <div class=\"container\">"
				+ "        <div class=\"navbar-header\">"
				+ "          <button type=\"button\" class=\"navbar-toggle\" data-toggle=\"collapse\" data-target=\"#navbar-ex-collapse\">"
				+ "            <span class=\"sr-only\">Toggle navigation</span>"
				+ "            <span class=\"icon-bar\"></span>" + "            <span class=\"icon-bar\"></span>"
				+ "            <span class=\"icon-bar\"></span>" + "          </button>"
				+ "          <a class=\"navbar-brand\" href=\"http://github.com/tmbernardo\"><span>Matthew Bernardo</span></a>"
				+ "        </div>" + "        <div class=\"collapse navbar-collapse\" id=\"navbar-ex-collapse\">"
				+ "          <ul class=\"nav navbar-nav navbar-right\">" + "            <li class=\"active\">"
				+ "              <a href=\"/\">Home</a>" + "            </li>" + "            <li>"
				+ "              <a href=\"#\">Enter new seed</a>" + "            </li>" + "          </ul>"
				+ "        </div>" + "      </div>" + "    </div>" + "    <div class=\"section\">"
				+ "      <div class=\"container\">" + "        <div class=\"row\">"
				+ "          <div class=\"col-md-12\">" + "            <h1 class=\"text-center\">Search</h1>"
				+ "          </div>" + "        </div>");
		printForm(request, response);
		out.printf("</div>" + "		</div>");
		out.printf("%n</body>%n");
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
