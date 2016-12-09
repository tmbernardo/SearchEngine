import java.util.Arrays;

public interface SearcherInterface {

	/**
	 * Goes through search terms in an input file line by line and adds each
	 * word to a list
	 * 
	 * @param inputFile
	 *            file to parse search terms from
	 * @param exact
	 *            Specifies whether to implement an exact search(True) or
	 *            partial(False)
	 */
	public void parseQuery(String inputFile, boolean exact);

	/**
	 * Cleans input string of all extra space and punctuation and sorts the list
	 * of remaining words
	 * 
	 * @param exact
	 *            Specifies whether to implement an exact search(True) or
	 *            partial(False)
	 * @return list of cleaned queries
	 */
	public static String[] cleanLine(String line) {
		String regex = "\\p{Punct}+";
		String cleaned = line.trim().toLowerCase().replaceAll(regex, "");
		String[] queries = cleaned.split("\\s+");
		Arrays.sort(queries);
		return queries;
	}

	/**
	 * Writes the search results to a default or custom named JSON file
	 * 
	 * @param outputFile
	 *            name of the JSON file to be written to
	 */
	public void toJSON(String outputFile);
}
