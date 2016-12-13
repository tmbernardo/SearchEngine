import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Base servlet that can be used to create and finish the web pages as well as
 * handle cookies
 *
 */
@SuppressWarnings("serial")
public class BaseServlet extends HttpServlet {

	protected static Logger log = LogManager.getLogger();
	protected static final LoginDatabaseHandler dbhandler = LoginDatabaseHandler.getInstance();

	/**
	 * Writes the metadata to the webpage
	 *
	 * @param title
	 *            title of the webpage
	 * @param response
	 *            Servlet response from the particular webpage
	 */
	protected void writeHead(String title, HttpServletResponse response) {
		try {
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
			out.printf("<body>%n");
		} catch (IOException ex) {
			log.warn("Unable to prepare HTTP response.");
			return;
		}
	}

	/**
	 * Finishes writing the web page. adds last generated time to bottom
	 *
	 * @param response
	 *            Servlet response from the particular webpage
	 */
	protected void writeFinish(HttpServletResponse response) {
		try {
			PrintWriter writer = response.getWriter();

			writer.printf("%n");
			writer.printf("<div class=\"col-sm-offset-1 col-sm-10\">");
			writer.printf("<p class=\"small text-muted\">");
			writer.printf("Last generated at %s", getDate());
			writer.printf("</div>");
			writer.printf("</p>%n%n");

			writer.printf(
					"<script src=\"https://ajax.googleapis.com/ajax/libs/jquery/1.12.4/jquery.min.js\"></script>%n");
			writer.printf(
					"<script src=\"https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js\" integrity=\"sha384-Tc5IQib027qvyjSMfHjOMaLkfuWVxZxUPnCJA7l2mCWNIpG9mGCD8wGNIcPD7Txa\" crossorigin=\"anonymous\"></script>%n");

			writer.printf("</body>%n");
			writer.printf("</html>%n");

			writer.flush();

			response.setStatus(HttpServletResponse.SC_OK);
			response.flushBuffer();
		} catch (IOException ex) {
			log.warn("Unable to finish HTTP response.");
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			return;
		}
	}

	/**
	 * Gets the current date and time
	 *
	 * @return current date and time
	 */
	protected String getDate() {
		String format = "hh:mm a 'on' EEE, MMM dd, yyyy";
		DateFormat dateFormat = new SimpleDateFormat(format);
		return dateFormat.format(Calendar.getInstance().getTime());
	}

	/**
	 * Gets the cookie data
	 *
	 * @param request
	 *            Servlet request from the particular webpage
	 * @return a Map of cookie name as the key and value
	 */
	protected Map<String, String> getCookieMap(HttpServletRequest request) {
		HashMap<String, String> map = new HashMap<String, String>();

		Cookie[] cookies = request.getCookies();

		if (cookies != null) {
			for (Cookie cookie : cookies) {
				map.put(cookie.getName(), cookie.getValue());
			}
		}

		return map;
	}

	/**
	 * Clears the cookies for current session
	 *
	 * @param request
	 *            Servlet request from the particular webpage
	 * @param response
	 *            Servlet response from the particular webpage
	 */
	protected void clearCookies(HttpServletRequest request, HttpServletResponse response) {
		Cookie[] cookies = request.getCookies();

		if (cookies == null) {
			return;
		}

		for (Cookie cookie : cookies) {
			cookie.setValue("");
			cookie.setMaxAge(0);
			response.addCookie(cookie);
		}
	}

	/**
	 * Clears a particular cookie for current session
	 *
	 * @param cookieName
	 *            Name of cookie to delete
	 * @param response
	 *            Servlet response from the particular webpage
	 */
	protected void clearCookie(String cookieName, HttpServletResponse response) {
		Cookie cookie = new Cookie(cookieName, null);
		cookie.setMaxAge(0);
		response.addCookie(cookie);
	}

	/**
	 * Logs data for cookies
	 *
	 * @param request
	 *            Servlet response from the particular webpage
	 */
	protected void debugCookies(HttpServletRequest request) {
		Cookie[] cookies = request.getCookies();

		if (cookies == null) {
			log.info("Saved Cookies: []");
		} else {
			String[] names = new String[cookies.length];

			for (int i = 0; i < names.length; i++) {
				names[i] = String.format("(%s, %s, %d)", cookies[i].getName(), cookies[i].getValue(),
						cookies[i].getMaxAge());
			}

			log.info("Saved Cookies: " + Arrays.toString(names));
		}
	}

	/**
	 * Gets a particular status message given an error
	 *
	 * @param cookieName
	 *            Name of cookie to delete
	 * @return response Servlet response from the particular webpage
	 */
	protected String getStatusMessage(String errorName) {
		Status status = null;

		try {
			status = Status.valueOf(errorName);
		} catch (Exception ex) {
			log.debug(errorName, ex);
			status = Status.ERROR;
		}

		return status.toString();
	}

	/**
	 * Gets a particular status message given a code
	 *
	 * @param code
	 *            error code
	 * @return error that accompanies given code
	 */
	protected String getStatusMessage(int code) {
		Status status = null;

		try {
			status = Status.values()[code];
		} catch (Exception ex) {
			log.debug(ex.getMessage(), ex);
			status = Status.ERROR;
		}

		return status.toString();
	}

	/**
	 * Gets the username value stored in cookies
	 *
	 * @param request
	 *            Servlet request
	 * @return null if user does not exist and user if there is
	 */
	protected String getUsername(HttpServletRequest request) {
		Map<String, String> cookies = getCookieMap(request);

		String login = cookies.get("login");
		String user = cookies.get("name");

		if ((login != null) && login.equals("true") && (user != null)) {
			// this is not necessarily safe!
			return user.replaceAll("\\W+", "");
		}

		return null;
	}
}
