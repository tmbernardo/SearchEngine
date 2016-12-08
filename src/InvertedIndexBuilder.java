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
public class InvertedIndexBuilder implements IndexBuilderInterface {
	private final InvertedIndex index;

	/**
	 * Sets the index for use within the class
	 * 
	 * @param InvertedIndex
	 *            object to save word data to
	 */
	public InvertedIndexBuilder(InvertedIndex index) {
		this.index = index;
	}

	@Override
	public void buildIndex(List<String> fileLocations) {
		for (String string : fileLocations) {
			// TODO Call IndexBuilderInterface.parseWords(string, index) instead
			parseWordsDir(Paths.get(string));
		}
	}

	// TODO Make this a public static parseWords(Path input, InvertedIndex index) method
	// TODO And move it to the interface, then use in both builders
	
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
	private void parseWordsDir(Path inputFile) {
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
