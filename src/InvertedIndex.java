import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
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

	
	public InvertedIndex(){
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
	
	// TODO add EXACT search functionality that goes through words index
	
	public void exactSearch(String inputFile) throws IOException {
		List<SearchQuery> queryList = QueryParser.parseQuery(Paths.get(inputFile));
		// needs a treeset with a Collections sort that compares frequency>position>location
		
		for (SearchQuery SearchQuery : queryList) {
			
			// convert this to a triply nested arraylist instead
			TreeMap<String, TreeMap<String, TreeSet<Integer>>> querySets = new TreeMap<>();
			//convert to doubly nested arraylist instead
			TreeMap<String, TreeSet<Integer>> fileMatches = new TreeMap<>();
			
			for (String string : SearchQuery.getQuery().split(" ")) {
				if(words.containsKey(string)){
					querySets.put(string, words.get(string));
					fileMatches.putAll(words.get(string));
				}
			}
			
			for (String key : querySets.keySet()) {
				// TODO ASK: is there a better way to do this?
				fileMatches.keySet().retainAll(querySets.get(key).keySet());
			}
			
			for (String word : querySets.keySet()) {
				for (String fileMatch : fileMatches.keySet()) {
					fileMatches.get(fileMatch).retainAll(querySets.get(word).get(fileMatch));
				}
			}
			
			for (String filematch : fileMatches.keySet()) {
				System.out.println(filematch);
			}
			// compareto will be able 
//			Collections.sort(querySets, );
			
		}
	}
	
	// TODO add PARTIAL search functionality that goes through words index
	public void partialSearch(Path inputfile) throws IOException {
		
	}

	public void add(String word, int lineNumber, String path) {
		String fileName = path;
		if (!words.containsKey(word)) {
			words.put(word, new TreeMap<>());
		}
		
		if (!words.get(word).containsKey(fileName)) {
			words.get(word).put(fileName, new TreeSet<>());
		}
		
		words.get(word).get(fileName).add(lineNumber);
	}
	
	// TODO
//	public void toJSON(Path outputFile) {
//		JSONWriter.toJSON(words);
//	}

	// TODO Nooooooooo, breaking encapsulation
	public TreeMap<String, TreeMap<String, TreeSet<Integer>>> getWordIndex() {
		return this.words;
	}

	// TODO: Add toJson method instead of running JSONFileWriter through driver
}
