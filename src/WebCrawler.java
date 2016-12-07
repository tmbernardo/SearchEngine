import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

/**
 * This class crawls through a given url, parses all words, and adds the cleaned
 * words to an Inverted Index
 */
public class WebCrawler implements CrawlerInterface {

	private final static int MAXLINKS = 50;

	private final Set<URL> urls;
	private final Queue<URL> urlQueue;
	private final InvertedIndex index;

	/**
	 * Initializes the urls and urlQueue to save links to
	 * 
	 * @param index
	 *            InvertedIndex to add words to
	 */
	public WebCrawler(InvertedIndex index) {
		this.index = index;
		urls = new HashSet<URL>();
		urlQueue = new LinkedList<URL>();
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
	@Override
	public void crawl(String inputURL) {
		try {
			urlQueue.add(new URL(inputURL));
			urls.add(new URL(inputURL));

			while (!urlQueue.isEmpty()) {

				URL url = urlQueue.remove();
				String html = HTMLCleaner.fetchHTML(url.toString());

				this.addToQueue(url, html);
				this.parseWordsUrl(url, html);

			}
		} catch (Exception e) {
			System.err.println("getURLs: Unable to open url " + inputURL);
			System.err.println(e);
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
		if (urls.size() < MAXLINKS) {
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
	}

	/**
	 * Parses the html for words and adds the words to a local inverted index
	 * 
	 * @param url
	 *            url that is currently being parsed
	 * @param html
	 *            html to parse
	 */
	public static void addHTML(URL url, String html, InvertedIndex index) throws UnknownHostException, IOException {
		String[] words = HTMLCleaner.fetchWords(html);

		int lineNumber = 0;

		for (String word : words) {

			lineNumber++;

			if (!word.isEmpty()) {
				index.add(word, lineNumber, url.toString());
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
		String[] words = HTMLCleaner.fetchWords(html);

		int lineNumber = 0;

		for (String word : words) {

			lineNumber++;

			if (!word.isEmpty()) {
				index.add(word, lineNumber, url.toString());
			}
		}
	}
}
