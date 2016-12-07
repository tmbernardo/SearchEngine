import java.io.BufferedReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.TreeMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ConcurrentSearcher implements SearcherInterface {
	private static final Logger logger = LogManager.getLogger();

	private final InvertedIndex index;
	private final TreeMap<String, List<SearchQuery>> results;
	private final WorkQueue minions;

	/**
	 * Constructor that saves the location of the InvertedIndex initializes the
	 * results treemap
	 * 
	 * @param index2
	 */
	public ConcurrentSearcher(InvertedIndex index2, int threads) {
		this.index = index2;
		this.results = new TreeMap<>();
		this.minions = new WorkQueue(threads);
	}

	/**
	 * Goes through search terms in an input file line by line and cleans and
	 * adds each word to a list then executes
	 * 
	 * @param inputFile
	 *            file to parse search terms from
	 * @param exact
	 *            if exact searches for the exact term
	 */
	@Override
	public void parseQuery(String inputFile, boolean exact) {
		String line = null;
		try (BufferedReader reader = Files.newBufferedReader(Paths.get(inputFile), Charset.forName("UTF-8"));) {

			while ((line = reader.readLine()) != null) {

				minions.execute(new QueryMinion(line, exact));
			}

		} catch (Exception e) {
			System.out.println("Searcher: File could not be opened!");
			System.out.println("Problem File: " + line);
		}
		minions.finish();
	}

	public void shutdownSearch() {
		minions.shutdown();
	}

	/**
	 * Minion class created for inputed search term(s)
	 */
	private class QueryMinion implements Runnable {
		String line;
		String regex = "\\p{Punct}+";
		boolean exact;

		/**
		 * Goes through each search term by calling the corresponding method in
		 * InvertedIndex
		 * 
		 * @param line
		 *            Search terms to search for in index
		 * @param exact
		 *            specifies whether exact or partial search is wanted
		 */
		public QueryMinion(String line, boolean exact) {
			this.line = line;
			this.exact = exact;
			logger.debug("Minion created for {}", line);
		}

		@Override
		public void run() {

			String cleaned = line.trim().toLowerCase().replaceAll(regex, "");
			String[] queries = cleaned.split("\\s+");
			Arrays.sort(queries);

			List<SearchQuery> local = null;

			if (exact) {
				local = index.exactSearch(queries);
			} else {
				local = index.partialSearch(queries);
			}

			String query = String.join(" ", queries);

			synchronized (results) {
				results.put(query, local);
			}

			logger.debug("Minion for {} completed", String.join(" ", queries));
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
		synchronized (results) {
			logger.debug("Writing to {}", outputFile);
			JSONFileWriter.searchResultsToJSON(Paths.get(outputFile), results);
		}
	}
}
