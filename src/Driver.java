import java.util.List;

// TODO Use the Eclipse Code Indenter (configure Eclipse to do this on save)
// TODO Add Javadoc to *every* class and method (some members if useful)
// PROJECT 2  s

public class Driver {

	public static void main(String[] args){

		String dir = "-dir";
		String index = "-index";
		String exact = "-exact";
		String query = "-query";
		String results = "-results";

		String resultsFileName = "results.json";
		String jsonFileName = "index.json";

		ArgumentParser argParser = new ArgumentParser();
		argParser.parseArguments(args);

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
