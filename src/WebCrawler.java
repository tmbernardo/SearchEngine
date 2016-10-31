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

		while (urls.size() < MAXLINKS && !urlQueue.isEmpty()) {

			try {
				base = new URL(inputURL);
				String newURL = normalize(urlQueue.remove());
				urls.add(newURL);
				addLinks(newURL, urlQueue);

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

		return urls;
	}

	private static void addLinks(String url, Queue<String> urls)
			throws URISyntaxException, UnknownHostException, IOException {

		String html = null;

		html = HTTPFetcher.fetchHTML(url);

		for (String link : LinkParser.listLinks(html)) {
			link = normalize(link);

			if (!urls.contains(link) && urls.size() < 50) {
				urls.add(link);
			}
		}
	}

	private static String normalize(String link) throws MalformedURLException, URISyntaxException {
		URI newURL;
		
		if (!link.contains("www")){
			newURL = new URL(base, "../"+ link).toURI();
		}

		newURL = new URI(link).normalize();

		return newURL.toString();
	}
}
