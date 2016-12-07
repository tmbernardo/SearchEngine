import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Driver
 * 
 * Parses the command line input for flags, creates an inverted index of files,
 * searches for strings, and writes data to the respective JSON file
 */
public class Driver {

	public static void main(String[] args) {

		final Logger logger = LogManager.getLogger();

		String dir_flag = "-dir";
		String index_flag = "-index";
		String exact_flag = "-exact";
		String query_flag = "-query";
		String results_flag = "-results";
		String url_flag = "-url";
		String multi_flag = "-multi";

		String resultsFileName = "results.json";
		String jsonFileName = "index.json";
		int defaultThreads = 5;
		int inputThreads;

		ArgumentParser argParser = new ArgumentParser();
		argParser.parseArguments(args);

		InvertedIndex index = null;
		CrawlerInterface crawler = null;
		SearcherInterface searcher = null;

		if (argParser.hasFlag(multi_flag)) {

			inputThreads = argParser.getValue(multi_flag, defaultThreads);

			if (inputThreads < 1) {
				System.err.println("Invalid thread input: setting threads to default 5");
				inputThreads = defaultThreads;
			}
			logger.debug("Multithreading index, Threads: {}", argParser.getValue(multi_flag, defaultThreads));
			index = new ConcurrentIndex();
			crawler = new ConcurrentWebCrawler(index, inputThreads);
			searcher = new ConcurrentSearcher(index, inputThreads);

		} else {

			index = new InvertedIndex();
			crawler = new WebCrawler(index);
			searcher = new Searcher(index);
		}

		if (argParser.hasValue(dir_flag)) {
			List<String> fileLocations = DirectoryTraverser.traverse(argParser.getValue(dir_flag));
			InvertedIndexBuilder.buildIndex(fileLocations, index);
		}

		if (argParser.hasValue(url_flag)) {
			crawler = new WebCrawler(index);
			crawler.crawl(argParser.getValue(url_flag));
		}

		if (argParser.hasValue(exact_flag)) {
			searcher.parseQuery(argParser.getValue(exact_flag), true);
			searcher.toJSON(argParser.getValue(results_flag, resultsFileName));
		}

		if (argParser.hasValue(query_flag)) {
			searcher.parseQuery(argParser.getValue(query_flag), false);
			searcher.toJSON(argParser.getValue(results_flag, resultsFileName));
		}

		if (argParser.hasFlag(index_flag)) {
			index.toJSON(argParser.getValue(index_flag, jsonFileName));
		}
	}
}