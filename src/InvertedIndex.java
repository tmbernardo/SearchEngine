import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.TreeMap;
import java.util.TreeSet;

//Create code that handles storing a word, file path,
// and location into an inverted index data structure.

/**
 * This class stores a word, file path, and location into a triply nested
 * collection (words) structure.
 */
public class InvertedIndex {

	private final TreeMap<String, TreeMap<String, TreeSet<Integer>>> words;

	public InvertedIndex() {
		this.words = new TreeMap<>();
	}

	/**
	 * Constructor: takes in an ArrayList containing the file locations
	 * 
	 * @param fileLocations
	 *            ArrayList of file locations
	 */
	public InvertedIndex(ArrayList<String> fileLocations) throws IOException {

		this();

		for (String string : fileLocations) {
			InvertedIndexBuilder.parseWords(Paths.get(string), this);
		}
	}

	public void exactSearch(String inputFile, String path, Boolean write) throws IOException {

		List<String> queryList = QueryParser.parseQuery(inputFile);
		TreeMap<String, ArrayList<SearchQuery>> SearchQueries = new TreeMap<>();

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
			if (write) {
				new JSONFileWriter(SearchQueries, Paths.get(path), "");
			}

		}
	}

	// TODO add PARTIAL search functionality that goes through words index
	public void partialSearch(String inputfile, String path, Boolean write) throws IOException {
		List<String> queryList = QueryParser.parseQuery(inputfile);

		TreeMap<String, ArrayList<SearchQuery>> SearchQueries = new TreeMap<>();

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
			if (write) {
				new JSONFileWriter(SearchQueries, Paths.get(path), "");
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

	// TODO
	// public void toJSON(Path outputFile) {
	// JSONWriter.toJSON(words);
	// }
	// TODO: Add toJson method instead of running JSONFileWriter through driver

	// TODO Nooooooooo, breaking encapsulation
	public TreeMap<String, TreeMap<String, TreeSet<Integer>>> getWordIndex() {
		return this.words;
	}
}
