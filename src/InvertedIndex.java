import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * This class stores a word, file path, and location into a triply nested
 * collection (words) structure.
 */
public class InvertedIndex {
	// private static final Logger logger = LogManager.getLogger();
	/**
	 * words indexes all words found in the inputed directory/file.
	 * SearchQueries stores the results of all files where certain search
	 * queries are found
	 */
	private final TreeMap<String, TreeMap<String, TreeSet<Integer>>> words;

	/**
	 * Default Constructor
	 * 
	 * Instantiates words and SearchQueries
	 */
	public InvertedIndex() {
		this.words = new TreeMap<>();
	}

	/**
	 * This method takes in an input file of search queries and checks if the
	 * inverted index contains the exact search term. The locations are then
	 * saved to a SearchQuery object and sorted in a ranked order.
	 * 
	 * @param queries
	 *            list of queries to be searched
	 * @return list of results
	 */
	public List<SearchQuery> exactSearch(String[] queries) {
		List<SearchQuery> results = new ArrayList<>();
		Map<String, SearchQuery> resultmap = new HashMap<>();
		for (String query : queries) {
			if (words.containsKey(query)) {
				this.addResults(query, results, resultmap);
			}
		}
		Collections.sort(results);
		return results;
	}

	/**
	 * This method takes in an input file of search queries and checks if words
	 * in the inverted index starts with the search term. The locations are then
	 * saved to a SearchQuery object and sorted in a ranked order.
	 * 
	 * @param queries
	 *            queries to be searched
	 * @return list of search results
	 */
	public List<SearchQuery> partialSearch(String[] queries) {
		List<SearchQuery> results = new ArrayList<>();
		Map<String, SearchQuery> resultmap = new HashMap<>();
		for (String query : queries) {
			for (String word : words.tailMap(query).keySet()) {
				if (word.startsWith(query)) {
					this.addResults(word, results, resultmap);
				} else {
					break;
				}
			}
		}
		Collections.sort(results);
		return results;
	}

	// TODO Make private, do not override in ConcurrentIndex
	/**
	 * Adds the results of each SearchQuery to the list of results
	 * 
	 * @param word
	 *            string in index to look for
	 * @param results
	 *            list of locations for the SearchQuery
	 * @param resultmap
	 *            map of search results
	 */
	public void addResults(String word, List<SearchQuery> results, Map<String, SearchQuery> resultmap) {
		for (String location : words.get(word).keySet()) {
			int count = words.get(word).get(location).size();
			int index = words.get(word).get(location).first();

			if (resultmap.containsKey(location)) {
				resultmap.get(location).update(count, index);
			} else {
				SearchQuery newquery = new SearchQuery(location);
				newquery.setCount(count);
				newquery.setIndex(index);
				resultmap.put(location, newquery);
				results.add(newquery);
			}
		}
	}

	/**
	 * This method adds a new cleaned word to the inverted index if it does not
	 * already exist in the index
	 * 
	 * @param word
	 *            word to be added to index
	 * @param lineNumber
	 *            line where the word is found
	 * @param fileName
	 *            name of the file where the word is found
	 */
	public void add(String word, int lineNumber, String fileName) {
		// logger.debug("Adding '{}' to words", word);
		if (!words.containsKey(word)) {
			words.put(word, new TreeMap<>());
		}
		if (!words.get(word).containsKey(fileName)) {
			words.get(word).put(fileName, new TreeSet<>());
		}
		words.get(word).get(fileName).add(lineNumber);
	}

	/**
	 * Returns the size of the index
	 * 
	 * @return size size of the inverted index
	 */
	public int getIndexSize() {
		return this.words.size();
	}

	/**
	 * This method writes the inverted index to a default or custom named JSON
	 * file
	 * 
	 * @param outputFile
	 *            name of the JSON file to be written to
	 */
	public void toJSON(String outputFile) {
		JSONFileWriter.indexToJSON(Paths.get(outputFile), words);
	}

	/* TODO
	public void addAll(InvertedIndex other) {
		for (String word : other.words.keySet()) {
			if (this.words.containsKey(word) == false) {
				this.words.put(word, other.words.get(word));
			}
			else {
				loop through each file, folow the same pattern (put when it makes sense, call addAll() otherwise)
			}
		}
	}
	*/
}
