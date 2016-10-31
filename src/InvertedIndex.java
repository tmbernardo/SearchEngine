import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
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
	private final TreeMap<String, ArrayList<SearchQuery>> SearchQueries; // TODO
																			// In
																			// a
																			// separate
																			// class
																			// dedicated
																			// to
																			// dealing
																			// with
																			// the
																			// query
																			// file

	/**
	 * Default Constructor
	 * 
	 * Instantiates words and SearchQueries
	 */
	public InvertedIndex() {
		this.words = new TreeMap<>();
		this.SearchQueries = new TreeMap<>();
	}

	/**
	 * Takes in an ArrayList containing the file locations
	 * 
	 * @param fileLocations
	 *            ArrayList of file locations
	 */
	public void InvertedIndexDir(List<String> fileLocations) {
		for (String string : fileLocations) {
			InvertedIndexBuilder.parseWordsDir(Paths.get(string), this);
		}
	}

	/**
	 * Takes in an ArrayList containing the URLs TODO: create URL
	 * invertedindex
	 * 
	 * @param urls
	 *            ArrayList of file locations
	 */
	public void InvertedIndexURL(List<String> urls) {

		for (String url : urls) {
			InvertedIndexBuilder.parseWordsUrl(url, this);
		}
	}

	/**
	 * This method takes in an input file of search queries and checks if the
	 * inverted index contains the exact search term. The locations are then
	 * saved to a SearchQuery object and sorted in a ranked order.
	 * 
	 * @param inputFile
	 *            Location of file containing queries to be searched
	 */
	public void exactSearch(String inputFile) { // TODO Keep here, return search
												// results List<SearchQuery>,
												// take in already split lines

		List<String> queryList = QueryParser.parseQuery(inputFile);

		for (String SearchQuery : queryList) {

			SearchQueries.put(SearchQuery, new ArrayList<>());

			for (String string : SearchQuery.split(" ")) {

				if (words.containsKey(string)) {

					for (String filematch : words.get(string).keySet()) {

						SearchQuery newQuery = new SearchQuery(filematch);
						newQuery.setCount(words.get(string).get(filematch).size());
						newQuery.setIndex(words.get(string).get(filematch).first());

						if (SearchQueries.get(SearchQuery).contains(newQuery)) {

							int index = SearchQueries.get(SearchQuery).indexOf(newQuery);
							SearchQuery similarQuery = SearchQueries.get(SearchQuery).get(index);
							similarQuery.setCount(similarQuery.getCount() + newQuery.getCount());

							if (newQuery.getIndex() < similarQuery.getIndex()) {
								similarQuery.setIndex(newQuery.getIndex());
							}

						} else {
							SearchQueries.get(SearchQuery).add(newQuery);
						}
					}
					Collections.sort(SearchQueries.get(SearchQuery)); // TODO
																		// Fix
																		// the
																		// sort
																		// order?
					Collections.reverse(SearchQueries.get(SearchQuery));
				}
			}
		}
	}

	/**
	 * This method takes in an input file of search queries and checks if words
	 * in the inverted index starts with the search term. The locations are then
	 * saved to a SearchQuery object and sorted in a ranked order.
	 * 
	 * @param inputFile
	 *            Location of file containing queries to be searched
	 */
	public void partialSearch(String inputfile) {
		List<String> queryList = QueryParser.parseQuery(inputfile);

		for (String SearchQuery : queryList) {

			SearchQueries.put(SearchQuery, new ArrayList<>());

			for (String string : SearchQuery.split(" ")) {

				for (String word : words.keySet()) {

					if (word.startsWith(string)) {
						for (String filematch : words.get(word).keySet()) {

							SearchQuery newQuery = new SearchQuery(filematch);
							newQuery.setCount(words.get(word).get(filematch).size());
							newQuery.setIndex(words.get(word).get(filematch).first());

							if (SearchQueries.get(SearchQuery).contains(newQuery)) {

								int index = SearchQueries.get(SearchQuery).indexOf(newQuery);
								SearchQuery similarQuery = SearchQueries.get(SearchQuery).get(index);
								similarQuery.setCount(similarQuery.getCount() + newQuery.getCount());

								if (newQuery.getIndex() < similarQuery.getIndex()) {
									similarQuery.setIndex(newQuery.getIndex());
								}

							} else {
								SearchQueries.get(SearchQuery).add(newQuery);
							}
						}
						Collections.sort(SearchQueries.get(SearchQuery));
						Collections.reverse(SearchQueries.get(SearchQuery));
					}
				}
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
	public void IndexToJSON(String outputFile) { // TODO toJSON()
		JSONFileWriter.IndexToJSON(Paths.get(outputFile), words);
	}

	/**
	 * This method writes the search results to a default or custom named JSON
	 * file
	 * 
	 * @param outputFile
	 *            name of the JSON file to be written to
	 */
	public void SearchResultsToJSON(String outputFile) {
		JSONFileWriter.SearchResultsToJSON(Paths.get(outputFile), SearchQueries);
	}
}
