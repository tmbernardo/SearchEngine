import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@SuppressWarnings("serial")
public class PassChangeServlet extends BaseServlet {

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

		if (getUsername(request) != null) {

			writeHead("Password Change", response);
			printBody(request, response);
			writeFinish(response);

		} else {
			// redirects if user is not logged in
			response.sendRedirect(response.encodeRedirectURL("/"));
		}
	}

	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
		writeHead("Register New User", response);

		String oldpass = request.getParameter("oldpass");
		String newpass = request.getParameter("newpass");
		Status status = dbhandler.updatePass(getUsername(request), oldpass, newpass);

		if (status == Status.OK) {
			response.sendRedirect(response.encodeRedirectURL("/login?newuser=true"));
		} else {
			String url = "/register?error=" + status.name();
			url = response.encodeRedirectURL(url);
			response.sendRedirect(url);
		}
	}

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
				+ "      </div>" + "    </div>");

		if (error != null) {
			String errorMessage = getStatusMessage(error);
			out.println("<div class=\"col-sm-offset-1 col-md-4\">");
			out.println("<p class=\"alert alert-danger\">" + errorMessage + "</p>");
			out.println("</div>");
		}
		printForm(request, out);
		out.println("</div>" + "        </div>" + "      </div>" + "    </div>");
	}

	private void printForm(HttpServletRequest request, PrintWriter out) {
		assert out != null;

		out.printf("<div class=\"section\">" + "      <div class=\"container\">" + "        <div class=\"row\">"
				+ "          <div class=\"col-md-12\">"
				+ "            <form class=\"form-horizontal\" role=\"form\" action=\"%s\" method=\"POST\">"
				+ "              <div class=\"form-group\">" + "                <div class=\"col-sm-2\">"
				+ "                  <label for=\"oldPassword\" class=\"control-label\">Old password</label>"
				+ "                </div>" + "                <div class=\"col-sm-10\">"
				+ "                  <input type=\"password\" id=\"oldPassword\" placeholder=\"Old Password\" name=\"oldpass\""
				+ "                  class=\"form-control\">" + "                </div>" + "              </div>"
				+ "              <div class=\"form-group\">" + "                <div class=\"col-sm-2\">"
				+ "                  <label for=\"newPassword\" class=\"control-label\">New password</label>"
				+ "                </div>" + "                <div class=\"col-sm-10\">"
				+ "                  <input type=\"password\" id=\"newPassword\" placeholder=\"New password\" name=\"newpass\""
				+ "                  class=\"form-control\">" + "                </div>" + "              </div>"
				+ "              <div class=\"form-group\">"
				+ "                <div class=\"col-sm-offset-2 col-sm-10\">"
				+ "                  <button type=\"submit\" class=\"btn btn-default\">Update</button>"
				+ "                </div>" + "              </div>" + "            </form>" + "          </div>"
				+ "        </div>" + "      </div>" + "    </div>", request.getServletPath());
	}
}
