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
public class InvertedIndex {
	// TODO Use "Refactor" in Eclipse to rename anything

	// TODO Use the final keyword
	private TreeMap<String, TreeMap<String, TreeSet<Integer>>> words;


	public InvertedIndex() {
	}

	// TODO Remove this constructor!
	/**
	 * Constructor: takes in an ArrayList containing the file locations
	 * 
	 * @param fileLocations
	 *            ArrayList of file locations
	 */
	public InvertedIndex(ArrayList<String> fileLocations) throws IOException {

		words = new TreeMap<>(); // TODO This should be in the default constructor, can call this() here to initialize it

		for (String string : fileLocations) {
			parseWords(Paths.get(string));
		}
	}

	// TODO This method belongs somewhere else. Could put this in a "InvertedIndexBuilder" class instead.
	// TODO To make your future projects easier, make it a public static method.
	/**
	 * Goes through the input file passed through from constructor, parses the
	 * words, then adds the words into the TreeMap words
	 */
	// TODO private static void parseWords(Path inputFile, InvertedIndex index)
	private void parseWords(Path inputFile) throws IOException {
		int lineNumber = 0;

		try (BufferedReader reader = Files.newBufferedReader(inputFile, Charset.forName("UTF-8"));) {

			String line = null;

			while ((line = reader.readLine()) != null) {
				for (String word : line.replaceAll("\\p{Punct}+", "").split(" ")) {
					if (!word.trim().isEmpty()) {
						lineNumber++;
						// index.add(...)
						add(word.trim().toLowerCase(), lineNumber, inputFile);
					}
				}
			}

		}
	}

	// TODO All "data structure" classes have public add method.
	// TODO Take a string instead of a path
	private void add(String word, int lineNumber, Path path) {
		String fileName = path.toString(); // TODO Do this conversion in the builder class
		if (!words.containsKey(word)) {
			words.put(word, new TreeMap<>());
		}
		
		if (!words.get(word).containsKey(fileName)) {
			words.get(word).put(fileName, new TreeSet<>());
		}
		
		words.get(word).get(fileName).add(lineNumber);
	}
	
	// TODO
//	public void toJSON(Path outputFile) {
//		JSONWriter.toJSON(words);
//	}

	// TODO Nooooooooo, breaking encapsulation
	public TreeMap<String, TreeMap<String, TreeSet<Integer>>> getWordIndex() {
		return this.words;
	}

	// TODO: Add toJson method instead of running JSONFileWriter through driver
}
