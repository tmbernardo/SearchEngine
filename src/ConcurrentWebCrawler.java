import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

// TODO Multithread this (new class), instead of urlQueue, replace that with
// a work queue.
// TODO Where you added to the queue, you now add a worker to the work queue
// TODO Where you wait for the queue to be empty, wait until no more pending
// work
public class ConcurrentWebCrawler {

	private static final Logger logger = LogManager.getLogger();

	private ConcurrentIndex index;
	private static WorkQueue minions;
	private Set<URL> urls;

	private final static int MAXLINKS = 50;
	private int threads;

	public ConcurrentWebCrawler(ConcurrentIndex index, int threads) {
		this.threads = threads;
		this.index = index;
	}

	public void crawl(String inputURL) {

		urls = new HashSet<URL>();
		minions = new WorkQueue(this.threads);

		try {
			URL baseURL = new URL(inputURL);
			urls.add(baseURL);

			minions.execute(new WebCrawlerMinion(baseURL));

			minions.shutdown();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private class WebCrawlerMinion implements Runnable {

		URL inputlink;
		String html;

		public WebCrawlerMinion(URL inputlink) {
			this.inputlink = inputlink;
			this.html = HTMLCleaner.fetchHTML(inputlink.toString());
			logger.debug("Minion created for {}", inputlink.toString());
		}

		@Override
		public void run() {
			try {
				this.addToQueue(inputlink, html);
				this.parseWordsUrl(inputlink, html);
				logger.debug("Minion finished {}", inputlink.toString());
			} catch (URISyntaxException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

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

		/**
		 * Parses the html for words and adds the words to the inverted index
		 * 
		 * @param url
		 *            url that is currently being parsed
		 * @param html
		 *            html to parse
		 */
		private void parseWordsUrl(URL url, String html) throws UnknownHostException, IOException {
			logger.debug("parsing words from {}", url.toString());
			String[] words = HTMLCleaner.fetchWords(html);
			int lineNumber = 0;
			for (String word : words) {
				lineNumber++;
				if (!word.isEmpty()) {
					index.add(word, lineNumber, inputlink.toString());
				}
			}
		}

	}

}
