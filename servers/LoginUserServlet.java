import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Handles login requests.
 *
 */
@SuppressWarnings("serial")
public class LoginUserServlet extends BaseServlet {

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

		if (getUsername(request) == null) {
			writeHead("Login", response);
			printBody(request, response);
			writeFinish(response);
		} else {
			response.sendRedirect(response.encodeRedirectURL("/"));
		}
	}

	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response) {
		String user = request.getParameter("user");
		String pass = request.getParameter("pass");

		Status status = dbhandler.authenticateUser(user, pass);

		try {
			if (status == Status.OK) {
				// should eventually change this to something more secure
				response.addCookie(new Cookie("login", "true"));
				response.addCookie(new Cookie("name", user));
				response.sendRedirect(response.encodeRedirectURL("/"));
			} else {
				response.addCookie(new Cookie("login", "false"));
				response.addCookie(new Cookie("name", ""));
				response.sendRedirect(response.encodeRedirectURL("/login?error=" + status.ordinal()));
			}
		} catch (Exception ex) {
			log.error("Unable to process login form.", ex);
		}
	}

	/**
	 * Prints the body of the login page.
	 * 
	 * @param request
	 *            Servlet request from the particular webpage
	 * @param response
	 *            Servlet response from the particular webpage
	 */
	private void printBody(HttpServletRequest request, HttpServletResponse response) throws IOException {
		PrintWriter out = response.getWriter();
		String error = request.getParameter("error");
		int code = 0;

		out.println("<div class=\"navbar navbar-default navbar-static-top\">" + "      <div class=\"container\">"
				+ "        <div class=\"navbar-header\">"
				+ "          <button type=\"button\" class=\"navbar-toggle\" data-toggle=\"collapse\" data-target=\"#navbar-ex-collapse\">"
				+ "            <span class=\"sr-only\">Toggle navigation</span>"
				+ "            <span class=\"icon-bar\"></span>" + "            <span class=\"icon-bar\"></span>"
				+ "            <span class=\"icon-bar\"></span>" + "          </button>"
				+ "          <a class=\"navbar-brand\" href=\"/\"><span>Matthew Bernardo</span></a>" + "        </div>"
				+ "        <div class=\"collapse navbar-collapse\" id=\"navbar-ex-collapse\">"
				+ "          <ul class=\"nav navbar-nav navbar-right\">" + "            <li class=\"active\">"
				+ "              <a href=\"/\">Home</a>" + "            </li>" + "          </ul>" + "        </div>"
				+ "      </div>" + "    </div>" + "    <div class=\"section\">" + "      <div class=\"container\">"
				+ "        <div class=\"row\">" + "          <div class=\"col-md-12\">"
				+ "            <div class=\"page-header\">" + "              <h1>Login</h1>" + "            </div>"
				+ "          </div>" + "        </div>" + "      </div>" + "    </div>");

		if (error != null) {
			try {
				code = Integer.parseInt(error);
			} catch (Exception ex) {
				code = -1;
			}

			String errorMessage = getStatusMessage(code);
			out.println("<div class=\"col-sm-offset-1 col-md-4\">");
			out.println("<p class=\"alert alert-danger\">" + errorMessage + "</p>");
			out.println("</div>");
		}

		if (request.getParameter("newuser") != null) {
			out.println("<div class=\"col-sm-offset-1 col-md-5\">");
			out.println("<p class=\"alert alert-success\">Registration was successful!");
			out.println("Login with your new username and password below.</p>");
			out.println("</div>");
		}

		printForm(out, request.getServletPath());
		out.println("</div>" + "        </div>" + "      </div>" + "    </div>");
	}

	/**
	 * Prints the login form for the login page
	 *
	 * @param out
	 *            PrintWriter for the login page
	 * @param action
	 *            server path for login page
	 */
	private void printForm(PrintWriter out, String action) {
		assert out != null;

		out.println("<div class=\"section\">" + "      <div class=\"container\">" + "        <div class=\"row\">"
				+ "          <div class=\"col-md-12\">");
		out.write(String
				.format("<form class=\"form-horizontal\" role=\"form\" action=\"%s\"" + "            method=\"POST\">"
						+ "              <div class=\"form-group\">" + "                <div class=\"col-sm-2\">"
						+ "                  <label for=\"inputuser\" class=\"control-label\">Username</label>"
						+ "                </div>" + "                <div class=\"col-sm-10\">"
						+ "                  <input type=\"text\" id=\"inputuser\" placeholder=\"Username\" name=\"user\" class=\"form-control\">"
						+ "                </div>" + "              </div>" + "              <div class=\"form-group\">"
						+ "                <div class=\"col-sm-2\">"
						+ "                  <label for=\"inputPassword\" class=\"control-label\">Password</label>"
						+ "                </div>" + "                <div class=\"col-sm-10\">"
						+ "                  <input type=\"password\" id=\"inputPassword\" placeholder=\"Password\" name=\"pass\""
						+ "                  class=\"form-control\">" + "                </div>"
						+ "              </div>" + "              <div class=\"form-group\">"
						+ "                <div class=\"col-sm-offset-2 col-sm-10\">"
						+ "                  <button type=\"submit\" class=\"btn btn-default\">Sign in</button>"
						+ "                </div>" + "              </div>" + "              <div class=\"form-group\">"
						+ "                <div class=\"col-sm-offset-2 col-sm-10\">"
						+ "                  <a class=\"btn btn-default\" href=\"/register\">New user? Click here to register a new account</a>"
						+ "                </div>" + "              </div>" + "            </form>", action));
	}
}
