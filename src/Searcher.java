import java.io.BufferedReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.TreeMap;

/**
 * Parses the queries given in an input file
 */

public class Searcher implements SearcherInterface {
	private final InvertedIndex index;
	private final TreeMap<String, List<SearchQuery>> results;

	/**
	 * Constructor that saves the location of the InvertedIndex initializes the
	 * results treemap
	 * 
	 * @param index
	 */
	public Searcher(InvertedIndex index) {
		this.index = index;
		this.results = new TreeMap<>();
	}

	/**
	 * Goes through search terms in an input file line by line and cleans and
	 * adds each word to a list
	 * 
	 * @param inputFile
	 *            file to parse search terms from
	 */
	@Override
	public void parseQuery(String inputFile, boolean exact) {
		String regex = "\\p{Punct}+";
		String line = null;

		try (BufferedReader reader = Files.newBufferedReader(Paths.get(inputFile), Charset.forName("UTF-8"));) {

			while ((line = reader.readLine()) != null) {
				String cleaned = line.trim().toLowerCase().replaceAll(regex, "");
				String[] words = cleaned.split("\\s+");
				Arrays.sort(words);

				if (exact) {
					results.put(String.join(" ", words), index.exactSearch(words));
				} else {
					results.put(String.join(" ", words), index.partialSearch(words));
				}
			}
		} catch (Exception e) {
			System.out.println("QueryParser: File could not be opened!");
			System.out.println("Problem File: " + line);
		}
	}

	/**
	 * This method writes the search results to a default or custom named JSON
	 * file
	 * 
	 * @param outputFile
	 *            name of the JSON file to be written to
	 */
	@Override
	public void toJSON(String outputFile) {
		JSONFileWriter.searchResultsToJSON(Paths.get(outputFile), results);
	}
}
