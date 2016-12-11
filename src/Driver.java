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
		String port_flag = "-port";

		String resultsFileName = "results.json";
		String jsonFileName = "index.json";

		int defaultThreads = 5;
		int inputThreads;

		int defaultPort = 8080;
		int inputPort;

		ArgumentParser argParser = new ArgumentParser();
		argParser.parseArguments(args);

		InvertedIndex index = null;
		IndexBuilderInterface builder = null;
		CrawlerInterface crawler = null;
		SearcherInterface searcher = null;
		WorkQueue minions = null;
		BaseServer newServer = null;

		if (argParser.hasFlag(multi_flag) | argParser.hasFlag(port_flag)) {

			inputThreads = argParser.getValue(multi_flag, defaultThreads);

			if (inputThreads < 1) {
				System.err.println("Invalid thread input: setting threads to default 5");
				inputThreads = defaultThreads;
			}
			logger.debug("Multithreading index, Threads: {}", argParser.getValue(multi_flag, defaultThreads));

			ConcurrentIndex concurrent = new ConcurrentIndex();
			index = concurrent;

			minions = new WorkQueue(inputThreads);

			crawler = new ConcurrentWebCrawler(concurrent, minions);
			searcher = new ConcurrentSearcher(concurrent, minions);
			builder = new ConcurrentIndexBuilder(concurrent, minions);

			if (argParser.hasFlag(port_flag)) {
				inputPort = argParser.getValue(port_flag, defaultPort);
				newServer = new BaseServer(inputPort, concurrent);
			}

		} else {

			index = new InvertedIndex();
			crawler = new WebCrawler(index);
			searcher = new Searcher(index);
			builder = new InvertedIndexBuilder(index);
		}

		if (argParser.hasValue(dir_flag)) {
			List<String> fileLocations = DirectoryTraverser.traverse(argParser.getValue(dir_flag));
			builder.buildIndex(fileLocations);
		}

		if (argParser.hasValue(url_flag)) {
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

		if (argParser.hasFlag(multi_flag)) {
			minions.shutdown();
		}

		if (argParser.hasFlag(port_flag)) {

			newServer.startServer();
		}

		logger.debug("Main shutting down");
	}
}
