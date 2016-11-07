import java.io.BufferedReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.TreeMap;

/**
 * Parses the queries given in an input file
 */

public class QueryParser {
	private final InvertedIndex index;
	private final TreeMap<String, List<SearchQuery>> results;

	public QueryParser(InvertedIndex index) {
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
	public void parseQuery(String inputFile, boolean exact) {
		String regex = "\\p{Punct}+";
		new ArrayList<String>();

		try (BufferedReader reader = Files.newBufferedReader(Paths.get(inputFile), Charset.forName("UTF-8"));) {
			String line = null;

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
		}
	}

	/**
	 * This method writes the search results to a default or custom named JSON
	 * file
	 * 
	 * @param outputFile
	 *            name of the JSON file to be written to
	 */
	public void toJSON(String outputFile) {
		JSONFileWriter.searchResultsToJSON(Paths.get(outputFile), results);
	}
}
