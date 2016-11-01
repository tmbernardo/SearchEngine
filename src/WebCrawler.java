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

public class WebCrawler {

	private final static int MAXLINKS = 50;

	private static URL base;

	public static List<String> getURLs(String inputURL) {

		System.out.println("Crawling Link: " + inputURL);
		List<String> urls = new ArrayList<String>();
		Queue<String> urlQueue = new LinkedList<String>();

		urlQueue.add(inputURL);

		while (urlQueue.size() < MAXLINKS && urls.size() < MAXLINKS && urlQueue.size() > 0) {

			try {
				base = new URL(urlQueue.remove());

				urls.add(base.toString());

				addToQueue(base.toString(), urlQueue);

			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
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

		while (urls.size() < MAXLINKS && urlQueue.size() > 0) {
			urls.add(urlQueue.remove());
		}

		return urls;
	}

	private static void addToQueue(String url, Queue<String> urlQueue)
			throws URISyntaxException, UnknownHostException, IOException {

		String html = null;

		html = HTTPFetcher.fetchHTML(url);

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
