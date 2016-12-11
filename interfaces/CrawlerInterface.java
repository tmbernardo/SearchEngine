
public interface CrawlerInterface {

	/**
	 * Runs through a URL parsing all of the links within and saves them to a
	 * queue
	 * 
	 * @param inputURL
	 *            Origin URL to parse links through
	 */
	public void crawl(String inputURL);
}
