import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/**
 * Builds an inverted index from the files passed through to parseWords
 */
public class InvertedIndexBuilder {

	/**
	 * Takes in an ArrayList containing the file locations
	 * 
	 * @param fileLocations
	 *            ArrayList of file locations
	 */
	public static void buildIndex(List<String> fileLocations, InvertedIndex index) {
		for (String string : fileLocations) {
			parseWordsDir(Paths.get(string), index);
		}
	}

	/**
	 * Goes through all words in each sub-directory/file passed and adds it to
	 * the inverted index passed
	 * 
	 * @param inputFile
	 *            location of the directory to be parsed
	 * @param index
	 *            inverted index to add words to
	 * 
	 */
	public static void parseWordsDir(Path inputFile, InvertedIndex index) {
		int lineNumber = 0;

		try (BufferedReader reader = Files.newBufferedReader(inputFile, Charset.forName("UTF-8"));) {
			String line = null;
			String path = inputFile.toString();

			while ((line = reader.readLine()) != null) {
				for (String word : line.trim().replaceAll("\\p{Punct}+", "").split("\\s+")) {
					if (!word.isEmpty()) {
						lineNumber++;
						index.add(word.trim().toLowerCase(), lineNumber, path);
					}
				}
			}

		} catch (IOException e) {
			System.out.println("InvertedIndexBuilder: File is invalid!");
		}
	}
}
