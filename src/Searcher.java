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

	@Override
	public void parseQuery(String inputFile, boolean exact) {
		String regex = "\\p{Punct}+";
		String line = null;

		try (BufferedReader reader = Files.newBufferedReader(Paths.get(inputFile), Charset.forName("UTF-8"));) {

			while ((line = reader.readLine()) != null) {
				// TODO Minor comment, since you do this in both places, could make a helper method:
				// TODO public static String[] cleanLine(String line) { replaceAll, split, sort }
				// TODO And put that method in the interface so both classes can access it
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

	@Override
	public void toJSON(String outputFile) {
		JSONFileWriter.searchResultsToJSON(Paths.get(outputFile), results);
	}
}
