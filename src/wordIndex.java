import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.TreeMap;
import java.util.TreeSet;

//Create code that handles storing a word, file path,
// and location into an inverted index data structure.

/**
 * This class stores a word, file path, and location into a triply nested
 * collection (words) structure.
 */
public class wordIndex {

	private TreeMap<String, TreeMap<String, TreeSet<Integer>>> words;

	public wordIndex() {
	}

	/**
	 * Constructor: takes in an ArrayList containing the file locations
	 * 
	 * @param fileLocations
	 *            ArrayList of file locations
	 */
	public wordIndex(ArrayList<String> fileLocations) throws IOException {

		words = new TreeMap<>();

		for (String string : fileLocations) {
			parseWords(Paths.get(string));
		}
	}

	/**
	 * Goes through the input file passed through from constructor, parses the
	 * words, then adds the words into the TreeMap words
	 */
	private void parseWords(Path inputFile) throws IOException {
		int lineNumber = 0;

		try (BufferedReader reader = Files.newBufferedReader(inputFile, Charset.forName("UTF-8"));) {

			String line = null;

			while ((line = reader.readLine()) != null) {
				for (String word : line.replaceAll("\\p{Punct}+", "").split(" ")) {
					if (!word.trim().isEmpty()) {
						lineNumber++;
						add(word.trim().toLowerCase(), lineNumber, inputFile);
					}
				}
			}

		}
	}

	private void add(String word, int lineNumber, Path path) {
		String fileName = path.toString();
		if (!words.containsKey(word)) {
			words.put(word, new TreeMap<>());
		}
		if (!words.get(word).containsKey(fileName)) {
			words.get(word).put(fileName, new TreeSet<>());
		}
		words.get(word).get(fileName).add(lineNumber);
	}

	public TreeMap<String, TreeMap<String, TreeSet<Integer>>> getWordIndex() {
		return this.words;
	}

	// TODO: Add toJson method instead of running JSONFileWriter through driver
}
