import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.TreeMap;
import java.util.TreeSet;

// Create code that handles storing a word, file path,
// and location into an inverted index data structure.
// TODO create a helper class for exact and partial search
// 

/**
 * This class stores a word, file path, and location into a triply nested
 * collection (words) structure.
 */
public class InvertedIndex {

	private final TreeMap<String, TreeMap<String, TreeSet<Integer>>> words;
	private final TreeMap<String, ArrayList<SearchQuery>> SearchQueries;

	public InvertedIndex() {
		this.words = new TreeMap<>();
		this.SearchQueries = new TreeMap<>();
	}

	/**
	 * Constructor: takes in an ArrayList containing the file locations
	 * 
	 * @param fileLocations
	 *            ArrayList of file locations
	 */
	public InvertedIndex(List<String> fileLocations){

		this();

		for (String string : fileLocations) {
			InvertedIndexBuilder.parseWords(Paths.get(string), this);
		}
	}

	public void exactSearch(String inputFile){

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
					Collections.sort(SearchQueries.get(SearchQuery));
					Collections.reverse(SearchQueries.get(SearchQuery));
				}
			}
		}
	}

	public void partialSearch(String inputfile){
		List<String> queryList = QueryParser.parseQuery(inputfile);

		for (String SearchQuery : queryList) {

			SearchQueries.put(SearchQuery, new ArrayList<>());

			for (String string : SearchQuery.split(" ")) {
				// TODO Send everything after this to the Searcher class
				// Searcher.partialSearch(string, SearchQueries);

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

	public void add(String word, int lineNumber, String fileName) {
		if (!words.containsKey(word)) {
			words.put(word, new TreeMap<>());
		}

		if (!words.get(word).containsKey(fileName)) {
			words.get(word).put(fileName, new TreeSet<>());
		}

		words.get(word).get(fileName).add(lineNumber);
	}
	
	public void IndexToJSON(String outputFile) {
		JSONFileWriter.IndexToJSON(Paths.get(outputFile), words);
	}
	
	public void SearchResultsToJSON(String outputFile){
		JSONFileWriter.SearchResultsToJSON(Paths.get(outputFile), SearchQueries);
	}
}
