import java.io.BufferedReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
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
		String line = null;

		try (BufferedReader reader = Files.newBufferedReader(Paths.get(inputFile), Charset.forName("UTF-8"));) {

			while ((line = reader.readLine()) != null) {

				String[] queries = SearcherInterface.cleanLine(line);

				if (exact) {
					results.put(String.join(" ", queries), index.exactSearch(queries));
				} else {
					results.put(String.join(" ", queries), index.partialSearch(queries));
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
