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
		String query = "-query";
		String exact = "-exact";
		String results = "-results";
		
		String resultsFileName = "results.json";
		String jsonFileName = "index.json";

		ArgumentParser argParser = new ArgumentParser();
		argParser.parseArguments(args);

		if (argParser.hasFlag(dir) && argParser.hasValue(dir)) {
			DirectoryTraverser directory = new DirectoryTraverser(argParser.getValue(dir));
			ArrayList<String> fileLocations = directory.getFileLocations();
			InvertedIndex words = new InvertedIndex(fileLocations);
			
			if (argParser.hasFlag(exact)){
				words.exactSearch(argParser.getValue(exact));
			}
			
			if (argParser.hasFlag(index)) {
				jsonFileName = argParser.getValue(index, jsonFileName);
				new JSONFileWriter(words.getWordIndex(), Paths.get(jsonFileName));
			}
		}
	}
}
