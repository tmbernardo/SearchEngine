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

		InvertedIndex index = new InvertedIndex();

		Searcher searcher = new Searcher(index);

		ArgumentParser argParser = new ArgumentParser();
		argParser.parseArguments(args);

		if (argParser.hasFlag(multi_flag)) {

			int threads = argParser.getValue(multi_flag, defaultThreads);

			if (threads < 1) {
				System.err.println("Invalid thread input: setting threads to default 5");
				threads = defaultThreads;
			}

			ConcurrentIndex threadSafeIndex = new ConcurrentIndex();
			ConcurrentSearcher multiThreadSearcher = new ConcurrentSearcher(threadSafeIndex, threads);

			logger.debug("Multithreading index, Threads: {}", argParser.getValue(multi_flag, defaultThreads));

			if (argParser.hasValue(dir_flag)) {
				List<String> fileLocations = DirectoryTraverser.traverse(argParser.getValue(dir_flag));
				ConcurrentIndexBuilder.buildIndex(fileLocations, threadSafeIndex, threads);
			}

			if (argParser.hasValue(url_flag)) {
				ConcurrentWebCrawler crawler = new ConcurrentWebCrawler(threadSafeIndex, threads);
				crawler.crawl(argParser.getValue(url_flag));
			}

			if (argParser.hasValue(exact_flag)) {
				multiThreadSearcher.parseQuery(argParser.getValue(exact_flag), true);
				multiThreadSearcher.toJSON(argParser.getValue(results_flag, resultsFileName));
			}

			if (argParser.hasValue(query_flag)) {
				multiThreadSearcher.parseQuery(argParser.getValue(query_flag), false);
				multiThreadSearcher.toJSON(argParser.getValue(results_flag, resultsFileName));
			}

			if (argParser.hasFlag(index_flag)) {
				threadSafeIndex.toJSON(argParser.getValue(index_flag, jsonFileName));
			}

		} else {

			if (argParser.hasValue(dir_flag)) {
				List<String> fileLocations = DirectoryTraverser.traverse(argParser.getValue(dir_flag));
				InvertedIndexBuilder.buildIndex(fileLocations, index);
			}

			if (argParser.hasValue(url_flag)) {
				WebCrawler crawler = new WebCrawler(index);
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

		/*
		 * TODO Project 4 Driver if (not multithreading) eveyrthing you have
		 * above
		 * 
		 * else everything you have above, changing one thing at a time to
		 * multithreaded
		 * 
		 * 
		 */
	}
}
