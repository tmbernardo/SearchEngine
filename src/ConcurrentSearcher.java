import java.io.BufferedReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.TreeMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ConcurrentSearcher {
	private static final Logger logger = LogManager.getLogger();

	private final ConcurrentIndex index;
	private final TreeMap<String, List<SearchQuery>> results;
	private final WorkQueue minions;

	/**
	 * Constructor that saves the location of the InvertedIndex initializes the
	 * results treemap
	 * 
	 * @param index
	 */
	public ConcurrentSearcher(ConcurrentIndex index, int threads) {
		this.index = index;
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
	public void parseQuery(String inputFile, boolean exact) {
		String regex = "\\p{Punct}+";
		String line = null;

		try (BufferedReader reader = Files.newBufferedReader(Paths.get(inputFile), Charset.forName("UTF-8"));) {

			while ((line = reader.readLine()) != null) {
				// TODO move the cleaning/sorting into minion too
				String cleaned = line.trim().toLowerCase().replaceAll(regex, "");
				String[] words = cleaned.split("\\s+");
				Arrays.sort(words);

				minions.execute(new QueryMinion(words, exact));
			}
		} catch (Exception e) {
			System.out.println("Searcher: File could not be opened!");
			System.out.println("Problem File: " + line);
		}

		// TODO it would make sense to call finish() here but not shutdown()
		// TODO Add a separate shutdown method, Driver will call shutdown()
		minions.shutdown();
	}

	/**
	 * Minion class created for inputed search term(s)
	 */
	private class QueryMinion implements Runnable {
		String[] words; // TODO Usually we still use our proper keywords
		boolean exact;

		/**
		 * Goes through each search term by calling the corresponding method in
		 * InvertedIndex
		 * 
		 * @param words
		 *            Search terms to search for in index
		 * @param exact
		 *            specifies whether exact or partial search is wanted
		 */
		public QueryMinion(String[] words, boolean exact) {
			this.words = words;
			this.exact = exact;
			logger.debug("Minion created for {}", String.join(" ", words));
		}

		@Override
		public void run() {
			// TODO results is shared data, must be properly synchronized
			if (exact) {
				results.put(String.join(" ", words), index.exactSearch(words));
			} else {
				results.put(String.join(" ", words), index.partialSearch(words));
			}
			logger.debug("Minion for {} completed", String.join(" ", words));
			
			/*
			 * synchronized (results) {
			 * 		results.put(String.join(" ", words), index.exactSearch(words));
			 * }
			 * 
			 * - or -
			 * 
			 * List<SearchQuery> local = index.exactSearch(words);
			 * 
			 * synchronized (results) {
			 * 		results.put(String.join(...), local);
			 * }
			 * 
			 * (this one is better)
			 */
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
		// TODO protect results here too
		logger.debug("Writing to {}", outputFile);
		JSONFileWriter.searchResultsToJSON(Paths.get(outputFile), results);
	}
}
