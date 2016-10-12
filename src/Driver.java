import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;

// TODO Use the Eclipse Code Indenter (configure Eclipse to do this on save)
// TODO Add Javadoc to *every* class and method (some members if useful)
// PROJECT 2
// TODO Process additional command-line parameters to determine the file to parse for queries.
// 

public class Driver {

	// TODO Never throw exceptions in main.
	public static void main(String[] args) throws IOException {

		String dir = "-dir";
		String index = "-index";
		String exact = "-exact";
		String query = "-query";
		String results = "-results";

		String resultsFileName;
		String jsonFileName;
		Boolean writeResult = true;

		ArgumentParser argParser = new ArgumentParser();
		argParser.parseArguments(args);

		writeResult = argParser.hasFlag(results);

		resultsFileName = argParser.hasValue(results) ? argParser.getValue(results) : "results.json";
		jsonFileName = argParser.hasValue(index) ? argParser.getValue(index) : "index.json";

		if (argParser.hasFlag(dir) && argParser.hasValue(dir)) {
			DirectoryTraverser directory = new DirectoryTraverser(argParser.getValue(dir));
			ArrayList<String> fileLocations = directory.getFileLocations();
			InvertedIndex words = new InvertedIndex(fileLocations);

			if (argParser.hasValue(exact)) {
				words.exactSearch(argParser.getValue(exact), resultsFileName, writeResult);
			}

			if (argParser.hasValue(query)) {
				words.partialSearch(argParser.getValue(query), resultsFileName, writeResult);
			}

			if (argParser.hasFlag(index)) {
				jsonFileName = argParser.getValue(index, jsonFileName);
				new JSONFileWriter(words.getWordIndex(), Paths.get(jsonFileName));
				// still need to change
			}
		}
	}
}
