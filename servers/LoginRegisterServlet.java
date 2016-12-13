import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Handles Registration requests
 *
 */
@SuppressWarnings("serial")
public class LoginRegisterServlet extends BaseServlet {

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

		if (getUsername(request) == null) {

			writeHead("Register", response);
			printBody(request, response);
			writeFinish(response);

		} else {
			response.sendRedirect(response.encodeRedirectURL("/"));
		}

	}

	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
		writeHead("Register New User", response);

		String newuser = request.getParameter("user");
		String newpass = request.getParameter("pass");
		Status status = dbhandler.registerUser(newuser, newpass);

		if (status == Status.OK) {
			response.sendRedirect(response.encodeRedirectURL("/login?newuser=true"));
		} else {
			String url = "/register?error=" + status.name();
			url = response.encodeRedirectURL(url);
			response.sendRedirect(url);
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
		String error = request.getParameter("error");

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
				+ "            <div class=\"page-header\">" + "              <h1>Registration</h1>"
				+ "            </div>" + "          </div>" + "        </div>" + "      </div>" + "    </div>");

		if (error != null) {
			String errorMessage = getStatusMessage(error);
			out.println("<div class=\"col-sm-offset-1 col-md-4\">");
			out.println("<p class=\"alert alert-danger\">" + errorMessage + "</p>");
			out.println("</div>");
		}
		printForm(request, out);
		out.println("</div>" + "        </div>" + "      </div>" + "    </div>");
	}

	/**
	 * Prints the search form in the navbar for the Home Page
	 *
	 * @param request
	 *            Servlet request from the particular webpage
	 * @param response
	 *            Servlet response from the particular webpage
	 */
	private void printForm(HttpServletRequest request, PrintWriter out) {
		assert out != null;

		out.println();
		out.printf(
				"<div class=\"section\">" + "      <div class=\"container\">" + "        <div class=\"row\">"
						+ "          <div class=\"col-md-12\">"
						+ "            <form class=\"form-horizontal\" role=\"form\" action=\"%s\""
						+ "            method=\"POST\">" + "              <div class=\"form-group\">"
						+ "                <div class=\"col-sm-2\">"
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
						+ "                  <button type=\"submit\" class=\"btn btn-default\">Register</button>"
						+ "                </div>" + "              </div>" + "              <div class=\"form-group\">"
						+ "                <div class=\"col-sm-offset-2 col-sm-10\">"
						+ "                  <a class=\"btn btn-default\" href=\"/login\">Already Registered? Click here to login</a>"
						+ "                </div>" + "              </div>" + "            </form>",
				request.getServletPath());
	}
}
