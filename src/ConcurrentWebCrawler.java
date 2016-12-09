import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Thread safe version of WebCrawler
 */
public class ConcurrentWebCrawler implements CrawlerInterface {

	private static final Logger logger = LogManager.getLogger();

	private final InvertedIndex index;
	private WorkQueue minions; // TODO I think this can be final too
	private final Set<URL> urls;

	private final static int MAXLINKS = 50;

	/**
	 * saves a thread safe InvertedIndex locally and specifies how many threads
	 * are to be used
	 * 
	 * @param index
	 *            thread safe InvertedIndex
	 * @param minions
	 *            WorkQueue object containing helper threads
	 */
	public ConcurrentWebCrawler(InvertedIndex index, WorkQueue minions) {
		this.index = index;
		this.urls = new HashSet<URL>();
		this.minions = minions;
	}

	@Override
	public void crawl(String inputURL) {

		try {
			URL baseURL = new URL(inputURL);
			urls.add(baseURL);

			minions.execute(new WebCrawlerMinion(baseURL));

		} catch (IOException e) {
			System.err.println("ConcurentWebCrawler: Unable to open url");
			logger.warn("Unable to open url: {}", inputURL, e);
		}

		minions.finish();
	}

	/**
	 * Private minion class used to crawl links
	 */
	private class WebCrawlerMinion implements Runnable {

		URL inputlink;
		String html;

		/**
		 * Takes url and fetches the html
		 * 
		 * @param inputlink
		 *            url that is currently being parsed
		 */
		public WebCrawlerMinion(URL inputlink) {
			this.inputlink = inputlink;
			logger.debug("Minion created for {}", inputlink.toString());
		}

		/**
		 * Goes through the inputlink's html and adds the links-to-parse to the
		 * queue and parses the current html's words and adds to the thread safe
		 * index
		 */
		@Override
		public void run() {
			try {
				this.html = HTMLCleaner.fetchHTML(inputlink.toString());
				this.addToQueue(inputlink, html);
				WebCrawler.addHTML(inputlink, html, index);
				logger.debug("Minion finished {}", inputlink.toString());
			} catch (Exception e) {
				logger.warn("Unable to either add URL to queue or parse url: {}", inputlink, e);
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
		private void addToQueue(URL url, String html) throws UnknownHostException, IOException, URISyntaxException {
			synchronized (urls) {
				if (urls.size() < MAXLINKS) {
					ArrayList<URL> linklist = LinkParser.listLinks(url.toString(), html);
					logger.debug("addToQueue: size of linklist {}", linklist.size());
					for (URL link : linklist) {
						if (urls.size() >= 50) {
							break;
						} else {
							if (!urls.contains(link)) {
								urls.add(link);
								logger.debug("addToQueue: size of urls {}", urls.size());
								minions.execute(new WebCrawlerMinion(link));
							}
						}
					}
				}
			}
		}

	}

}
