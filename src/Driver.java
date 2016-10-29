import java.util.List;

/**
 * Driver
 * 
 * Parses the command line input for flags, creates an inverted index of files,
 * searches for strings, and writes data to the respective JSON file
 */
public class Driver {

	public static void main(String[] args) {

		String dir = "-dir";
		String index = "-index";
		String exact = "-exact";
		String query = "-query";
		String results = "-results";

		String resultsFileName = "results.json";
		String jsonFileName = "index.json";

		ArgumentParser argParser = new ArgumentParser();
		argParser.parseArguments(args);
		
		/*
		 * InvertedIndex index = new InvertedIndex();
		 * 
		 * if (hasFlag(-dir) {
		 *    build stuff only
		 * }
		 * 
		 * if (query flag) {
		 *    search
		 * }
		 * 
		 * if (output index) {
		 *    output index
		 * }
		 * 
		 * 
		 */

		if (argParser.hasFlag(dir) && argParser.hasValue(dir)) {
			List<String> fileLocations = DirectoryTraverser.traverse(argParser.getValue(dir));
			InvertedIndex words = new InvertedIndex(fileLocations);

			if (argParser.hasValue(exact)) {
				words.exactSearch(argParser.getValue(exact));
				words.SearchResultsToJSON(argParser.getValue(results, resultsFileName));
			}

			if (argParser.hasValue(query)) {
				words.partialSearch(argParser.getValue(query));
				words.SearchResultsToJSON(argParser.getValue(results, resultsFileName));
			}

			if (argParser.hasFlag(index)) {
				words.IndexToJSON(argParser.getValue(index, jsonFileName));
			}
		}
	}
}
