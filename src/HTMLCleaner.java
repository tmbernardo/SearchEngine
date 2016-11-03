import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*
 * This class does not take a particularly efficient approach, but this
 * simplifies the process of retrieving and cleaning HTML code for your
 * web crawler project later (and makes your approach easier to unit 
 * test). 
 */

/**
 * A helper class with several static methods that will help fetch a webpage,
 * strip out all of the HTML, and parse the resulting plain text into words.
 * Meant to be used for the web crawler project.
 *
 * @see HTMLCleaner
 * @see HTMLCleanerTest
 */
public class HTMLCleaner {
	/** Port used by socket. For web servers, should be port 80. */
	public static final int DEFAULT_PORT = 80;

	/** Version of HTTP used and supported. */
	public static final String version = "HTTP/1.1";

	// See: http://www.w3.org/Protocols/rfc2616/rfc2616-sec5.html#sec5.1.1
	/** Valid HTTP method types. */
	public static enum HTTP {
		OPTIONS, GET, HEAD, POST, PUT, DELETE, TRACE, CONNECT
	};

	/**
	 * Will connect to the web server and fetch the URL using the HTTP request
	 * provided. It would be more efficient to operate on each line as returned
	 * instead of storing the entire result as a list.
	 *
	 * @param url
	 *            - url to fetch
	 * @param request
	 *            - full HTTP request
	 *
	 * @return the lines read from the web server
	 *
	 * @throws IOException
	 * @throws UnknownHostException
	 */
	public static List<String> fetchLines(URL url, String request) throws UnknownHostException, IOException {
		ArrayList<String> lines = new ArrayList<>();
		int port = url.getPort() < 0 ? DEFAULT_PORT : url.getPort();

		try (Socket socket = new Socket(url.getHost(), port);
				BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				PrintWriter writer = new PrintWriter(socket.getOutputStream());) {
			writer.println(request);
			writer.flush();

			String line = null;

			while ((line = reader.readLine()) != null) {
				lines.add(line);
			}
		}

		return lines;
	}

	/**
	 * Crafts a minimal HTTP/1.1 request for the provided method.
	 *
	 * @param url
	 *            - url to fetch
	 * @param type
	 *            - HTTP method to use
	 *
	 * @return HTTP/1.1 request
	 *
	 * @see {@link HTTP}
	 */
	public static String craftHTTPRequest(URL url, HTTP type) {
		String host = url.getHost();
		String resource = url.getFile().isEmpty() ? "/" : url.getFile();

		// The specification is specific about where to use a new line
		// versus a carriage return!
		return String.format("%s %s %s\n" + "Host: %s\n" + "Connection: close\n" + "\r\n", type.name(), resource,
				version, host);
	}

	/**
	 * Helper method that parses HTTP headers into a map where the key is the
	 * field name and the value is the field value. The status code will be
	 * stored under the key "Status".
	 *
	 * @param headers
	 *            - HTTP/1.1 header lines
	 * @return field names mapped to values if the headers are properly
	 *         formatted
	 */
	public static Map<String, String> parseHeaders(List<String> headers) {
		Map<String, String> fields = new HashMap<>();

		if (headers.size() > 0 && headers.get(0).startsWith(version)) {
			fields.put("Status", headers.get(0).substring(version.length()).trim());

			for (String line : headers.subList(1, headers.size())) {
				String[] pair = line.split(":", 2);

				if (pair.length == 2) {
					fields.put(pair[0].trim(), pair[1].trim());
				}
			}
		}

		return fields;
	}

	/** Regular expression for removing special characters. */
	public static final String CLEAN_REGEX = "(?U)[^\\p{Alnum}\\p{Space}]+";

	/** Regular expression for splitting text into words by whitespace. */
	public static final String SPLIT_REGEX = "(?U)\\p{Space}+";

	/**
	 * Fetches the webpage at the provided URL, cleans up the HTML tags, and
	 * parses the resulting plain text into words.
	 * 
	 * @param url
	 *            webpage to download
	 * @return list of parsed words
	 * @throws IOException
	 * @throws UnknownHostException
	 */
	public static String[] fetchWords(String url) throws UnknownHostException, IOException {
		// THIS METHOD IS PROVIDED FOR YOU. DO NOT MODIFY.
		String html = fetchHTML(url);
		String text = cleanHTML(html);
		return parseWords(text);
	}

	/**
	 * Parses the provided plain text (already cleaned of HTML tags) into
	 * individual words.
	 *
	 * @param text
	 *            plain text without html tags
	 * @return list of parsed words
	 */
	public static String[] parseWords(String text) {
		// THIS METHOD IS PROVIDED FOR YOU. DO NOT MODIFY.
		text = text.replaceAll(CLEAN_REGEX, "").toLowerCase().trim();
		return text.split(SPLIT_REGEX);
	}

	/**
	 * Removes all style and script tags (and any text in between those tags),
	 * all HTML tags, and all HTML entities.
	 *
	 * @param html
	 *            html code to parse
	 * @return plain text
	 */
	public static String cleanHTML(String html) {
		// THIS METHOD IS PROVIDED FOR YOU. DO NOT MODIFY.
		String text = html;
		text = stripElement("script", text);
		text = stripElement("style", text);
		text = stripTags(text);
		text = stripEntities(text);
		return text;
	}

	/**
	 * Fetches the webpage at the provided URL by opening a socket, sending an
	 * HTTP request, removing the headers, and returning the resulting HTML
	 * code.
	 *
	 * @param link
	 *            webpage to download
	 * @return html code
	 * @throws IOException
	 * @throws UnknownHostException
	 */
	public static String fetchHTML(String link) {
		URL inputLink = null;

		try {
			inputLink = new URL(link);
		} catch (MalformedURLException e) {
			System.out.println("fetchHTML: Bad URL");
		}

		String httpRequest = craftHTTPRequest(inputLink, HTTP.GET);

		List<String> lines = null;
		try {
			lines = fetchLines(inputLink, httpRequest);
		} catch (IOException e) {
			System.out.println("fetchHTML: Bad Input File");
		}

		int lineIndex = 0;

		while (!lines.get(lineIndex).trim().isEmpty() && lineIndex < lines.size()) {
			lineIndex++;
		}

		Map<String, String> fields = parseHeaders(lines.subList(0, lineIndex + 1));
		String type = fields.get("Content-Type");

		if (type != null && type.toLowerCase().contains("html")) {
			return String.join(System.lineSeparator(), lines.subList(lineIndex + 1, lines.size()));
		}
		return null;
	}

	// Click on the Javadoc pane in Eclipse to view the rendered
	// Javadoc for each method. Embedded HTML code, such as in the
	// Javadoc below, will appear properly.

	/**
	 * Removes everything between the element tags, and the element tags
	 * themselves. For example, consider the html code:
	 *
	 * <pre>
	 * &lt;style type="text/css"&gt;body { font-size: 10pt; }&lt;/style&gt;
	 * </pre>
	 *
	 * If removing the "style" element, all of the above code will be removed,
	 * and replaced with the empty string.
	 *
	 * @param name
	 *            name of the element to strip, like "style" or "script"
	 * @param html
	 *            html code to parse
	 * @return html code without the element specified
	 */
	public static String stripElement(String name, String html) {
		return html.replaceAll("(?i)(?ms)\\<" + name + ".*?>.*?</" + ".*?>", "");
	}

	/**
	 * Removes all HTML tags, which is essentially anything between the "<" and
	 * ">" symbols. The tag will be replaced by the empty string.
	 *
	 * @param html
	 *            html code to parse
	 * @return text without any html tags
	 */
	public static String stripTags(String html) {
		return html.replaceAll("(?ms)<.*?>", "");
	}

	/**
	 * Replaces all HTML entities in the text with an empty string. For example,
	 * "2010&ndash;2012" will become "20102012".
	 *
	 * @param html
	 *            the text with html code being checked
	 * @return text with HTML entities replaced by an empty string
	 */
	public static String stripEntities(String html) {
		return html.replaceAll("&[^\\s].*?;", "");
	}
}
