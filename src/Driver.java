import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;

// TODO Remove old TODO comments
// TODO Use the Eclipse Code Indenter (configure Eclipse to do this on save)
// TODO Add Javadoc to *every* class and method (some members if useful)

public class Driver {

	// TODO Never throw exceptions in main.
	public static void main(String[] args) throws IOException {
		// TODO Generally not encouraged in Java 
		String dir = "-dir", index = "-index", jsonFileName = "index.json";

		// TODO Could declare and define in the if block
		traverseDirectory directory;
		ArrayList<String> fileLocations = null;
		InvertedIndex words = null;

		ArgumentParser parser = new ArgumentParser();
		parser.parseArguments(args);

		if (parser.hasFlag(dir) && parser.hasValue(dir)) {
			directory = new traverseDirectory(parser.getValue(dir));
			fileLocations = directory.getFileLocations();
			words = new InvertedIndex(fileLocations);

			if (parser.hasFlag(index)) {
				jsonFileName = parser.getValue(index, jsonFileName);
				new JSONFileWriter(words.getWordIndex(), Paths.get(jsonFileName));
			}
		}
	}
}
