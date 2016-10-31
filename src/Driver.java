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

		ArgumentParser argParser = new ArgumentParser();
		argParser.parseArguments(args);

		if (argParser.hasValue(dir_flag)) {
			List<String> fileLocations = DirectoryTraverser.traverse(argParser.getValue(dir_flag));
			index.InvertedIndexDir(fileLocations);
		}

		if (argParser.hasValue(url_flag) ) {
			List<String> urls = WebCrawler.getURLs(argParser.getValue(url_flag));
			index.InvertedIndexURL(urls);
		}

		if (argParser.hasValue(exact_flag)) {
			index.exactSearch(argParser.getValue(exact_flag));
			index.SearchResultsToJSON(argParser.getValue(results_flag, resultsFileName));
		}

		if (argParser.hasValue(query_flag)) {
			index.partialSearch(argParser.getValue(query_flag));
			index.SearchResultsToJSON(argParser.getValue(results_flag, resultsFileName));
		}

		if (argParser.hasFlag(index_flag)) {
			index.IndexToJSON(argParser.getValue(index_flag, jsonFileName));
		}
	}
}
