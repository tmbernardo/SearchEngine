import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

/**
 * 
 */
public class WebCrawler {

	private final static int MAXLINKS = 50;
	private static URL url;

	private static Set<String> urls;
	private static Queue<String> urlQueue;

	public static void parseWordsUrl(String url, InvertedIndex index) {
		String[] html = null;
		try {
			html = HTMLCleaner.fetchWords(url);
		} catch (UnknownHostException e) {
			System.out.println("parseWordsUrl: Host could not be determined!");
		} catch (IOException e) {
			System.out.println("pareWordsUrl: IOException from fetchWords");
		}

		int lineNumber = 0;

		for (String word : html) {

			lineNumber++;

			if (!word.isEmpty()) {
				index.add(word, lineNumber, url);
			}
		}
	}

	/**
	 * Runs through a URL parsing all of the links within and saves them to a
	 * queue
	 * 
	 * @param inputURL
	 *            Origin URL to parse links through
	 * 
	 * @return urls list of URLs that have been found from originating link
	 */
	public static Set<String> getURLs(String inputURL) {

		urls = new HashSet<String>();
		urlQueue = new LinkedList<String>();

		urlQueue.add(inputURL);

		while (urls.size() < MAXLINKS && urlQueue.size() > 0) {

			try {
				url = new URL(urlQueue.remove());
				urls.add(url.toString());

				if (urls.size() + urlQueue.size() < MAXLINKS) {
					addToQueue(url.toString(), urlQueue);
				}

			} catch (MalformedURLException e) {
				System.out.println("getURLs: String could not be formatted to a URL");
			} catch (UnknownHostException e) {
				System.out.println("getURLs: Host is unknown");
			} catch (IOException e) {
				System.out.println("getURLs: file IOException");
			} catch (URISyntaxException e) {
				System.out.println("getURLs: URISyntaxException");
				e.printStackTrace();
			}
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

		ArrayList<String> linklist = LinkParser.listLinks(url);

		if (linklist.size() > 0) {
			for (String link : linklist) {
				urlQueue.add(link);
			}
		}
	}
}
