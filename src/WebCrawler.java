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

	private final Set<URL> urls;
	private final Queue<URL> urlQueue;
	public InvertedIndex index;

	public WebCrawler() {
		urls = new HashSet<URL>();
		urlQueue = new LinkedList<URL>();
	}

	public WebCrawler(InvertedIndex index) {
		this();
		this.index = index;
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
	public void crawl(String inputURL) {
		try {
			urlQueue.add(new URL(inputURL));
			urls.add(new URL(inputURL));

			while (!urlQueue.isEmpty()) {

				URL url = urlQueue.remove();
				String html = HTMLCleaner.fetchHTML(url.toString());

				if (urls.size() + urlQueue.size() < MAXLINKS) {
					this.addToQueue(url, html);
				}

				this.parseWordsUrl(url, html);

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

	/**
	 * Adds link from a given URL to the queue if there are < 50 elements
	 * 
	 * @param url
	 *            string of url to be cleaned and made absolute
	 * @param urlQueue
	 *            Queue of URLs to go through if length is < 50
	 */
	private void addToQueue(URL url, String html) throws UnknownHostException, IOException, URISyntaxException {

		ArrayList<URL> linklist = LinkParser.listLinks(url.toString(), html);

		for (URL link : linklist) {
			if (urls.size() >= 50) {
				break;
			} else {
				if (!urls.contains(link)) {
					urlQueue.add(link);
					urls.add(link);
				}
			}
		}
	}

	/**
	 * Parses the html for words and adds the words to the inverted index
	 * 
	 * @param url
	 *            url that is currently being parsed
	 * @param html
	 *            html to parse
	 */
	private void parseWordsUrl(URL url, String html) throws UnknownHostException, IOException {
		String[] words = null;

		words = HTMLCleaner.fetchWords(html);

		int lineNumber = 0;

		for (String word : words) {

			lineNumber++;

			if (!word.isEmpty()) {
				index.add(word, lineNumber, url.toString());
			}
		}
	}
}
