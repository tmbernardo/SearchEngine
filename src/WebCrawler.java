import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * 
 */
public class WebCrawler {

	private final static int MAXLINKS = 50;

	private static URL base;

	/**
	 * Runs through a URL parsing all of the links within and saves them to a
	 * queue
	 * 
	 * @param inputURL
	 *            Origin URL to parse links through
	 * 
	 * @return urls list of URLs that have been found from originating link
	 */
	public static List<String> getURLs(String inputURL) {

		List<String> urls = new ArrayList<String>();
		Queue<String> urlQueue = new LinkedList<String>();

		urlQueue.add(inputURL);

		while (urlQueue.size() < MAXLINKS && urls.size() < MAXLINKS && urlQueue.size() > 0) {

			try {
				base = new URL(urlQueue.remove());

				urls.add(base.toString());

				addToQueue(base.toString(), urlQueue);

			} catch (MalformedURLException e) {
				System.out.println("getURLs: String could not be formatted to a URL");
			} catch (UnknownHostException e) {
				System.out.println("getURLs: Host is unknown");
			} catch (IOException e) {
				System.out.println("getURLs: file IOException");
			} catch (URISyntaxException e) {
				System.out.println("getURLs: URISyntaxException");
			}
		}

		while (urls.size() < MAXLINKS && urlQueue.size() > 0)

		{
			urls.add(urlQueue.remove());
		}

		return urls;
	}

	/**
	 * Adds link from a given URL to the queue if there are < 50 elements
	 * 
	 * @param url
	 *            string of url to be cleaned and made absolute
	 * @param urlQueue
	 *            Queue of URLs to go through if length is < 50
	 */
	private static void addToQueue(String url, Queue<String> urlQueue)
			throws UnknownHostException, IOException, URISyntaxException {

		String html = null;

		html = HTMLCleaner.fetchHTML(url);

		ArrayList<String> linklist = LinkParser.listLinks(html);

		if (linklist.size() > 0) {
			for (String link : linklist) {

				link = normalize(link);

				if (!urlQueue.contains(link)) {
					urlQueue.add(link);
				}
			}
		}
	}

	/**
	 * Takes the URL and turns it into absolute then normalizes
	 * 
	 * @param link
	 * 
	 * @return normalized absolute URL returned as a string
	 */
	private static String normalize(String link) throws MalformedURLException, URISyntaxException {
		URI newURL;

		if (!link.contains("http://")) {
			newURL = new URL(base, "./" + link).toURI();
		} else {
			newURL = new URI(link);
		}

		return newURL.normalize().toString();
	}
}
