import java.io.BufferedReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.TreeMap;

/**
 * Parses the queries given in an input file
 */

public class QueryParser {
	/*
	private final InvertedIndex index;
	private final TreeMap<String, ArrayList<SearchQuery>> results;
	
	public QueryParser(InvertedIndex index) {
		
	}
	*/
	
	/**
	 * Goes through search terms in an input file line by line and cleans and
	 * adds each word to a list
	 * 
	 * @param inputFile
	 *            file to parse search terms from
	 * @return List of search terms to look for in the index file
	 */
	public static ArrayList<String> parseQuery(String inputFile) {
		ArrayList<String> queryList = new ArrayList<String>();

		try (BufferedReader reader = Files.newBufferedReader(Paths.get(inputFile), Charset.forName("UTF-8"));) {
			String line = null;

			while ((line = reader.readLine()) != null) {
				String word = line.trim().toLowerCase().replaceAll("\\p{Punct}+", "").replaceAll(" +", " ");
				if (!word.isEmpty()) {
					List<String> newWord = Arrays.asList(word.split(" "));
					Collections.sort(newWord);
					queryList.add(String.join(" ", newWord));
				}
				
				/*
				String cleaned = line.trim().toLowerCase().replaceAll(regex, replacement);
				String[] words = cleaned.split("\\s+");
				Arrays.sort(words);
				
				if you add a parameter to determine if it is exact or partial
				if (exact)
					results.put(String.join(" ", words), index.exactSearch(words));
				else
					results.put(String.join(" ", words), index.partialSearch(words));
				*/
				
			}
		} catch (Exception e) {
			System.out.println("QueryParser: File could not be opened!");
		}

		Collections.sort(queryList);
		return queryList;
	}
	
	// TODO add a toJSON that calls your writer
}
