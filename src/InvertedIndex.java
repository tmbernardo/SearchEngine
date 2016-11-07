import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * This class stores a word, file path, and location into a triply nested
 * collection (words) structure.
 */
public class InvertedIndex {
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
	 * Takes in an ArrayList containing the file locations
	 * 
	 * @param fileLocations
	 *            ArrayList of file locations
	 */
	public void invertedIndexDir(List<String> fileLocations) {
		for (String string : fileLocations) {
			InvertedIndexBuilder.parseWordsDir(Paths.get(string), this);
		}
	}

	/**
	 * Takes in an ArrayList containing the URLs
	 * 
	 * @param urls
	 *            ArrayList of file locations
	 */
	public void invertedIndexUrl(List<String> urls) {

		for (String url : urls) {
			InvertedIndexBuilder.parseWordsUrl(url, this);
		}
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

		Map<String, SearchQuery> resultmap = new TreeMap<>();

		for (String query : queries) {
			if (words.containsKey(query)) {
				for (String location : words.get(query).keySet()) {
					int count = words.get(query).get(location).size();
					int index = words.get(query).get(location).first();

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

		Map<String, SearchQuery> resultmap = new TreeMap<>();

		for (String query : queries) {
			for (String word : words.tailMap(query).keySet()) {

				if (word.startsWith(query)) {
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
			}
		}
		Collections.sort(results);
		return results;
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
		if (!words.containsKey(word)) {
			words.put(word, new TreeMap<>());
		}

		if (!words.get(word).containsKey(fileName)) {
			words.get(word).put(fileName, new TreeSet<>());
		}
		words.get(word).get(fileName).add(lineNumber);
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
}
