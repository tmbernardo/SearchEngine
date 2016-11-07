import java.util.List;

/**
 * Driver
 * 
 * Parses the command line input for flags, creates an inverted index of files,
 * searches for strings, and writes data to the respective JSON file
 */
public class Driver {

	public static void main(String[] args) {

		String dir_flag = "-dir";
		String index_flag = "-index";
		String exact_flag = "-exact";
		String query_flag = "-query";
		String results_flag = "-results";
		String url_flag = "-url";

		String resultsFileName = "results.json";
		String jsonFileName = "index.json";

		InvertedIndex index = new InvertedIndex();

		QueryParser searcher = new QueryParser(index);

		ArgumentParser argParser = new ArgumentParser();
		argParser.parseArguments(args);

		if (argParser.hasValue(dir_flag)) {
			List<String> fileLocations = DirectoryTraverser.traverse(argParser.getValue(dir_flag));
			index.invertedIndexDir(fileLocations);
		}

		if (argParser.hasValue(url_flag)) {
			List<String> urls = WebCrawler.getURLs(argParser.getValue(url_flag));
			index.invertedIndexUrl(urls);
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
