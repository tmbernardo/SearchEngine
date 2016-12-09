
public interface SearcherInterface {

	/**
	 * Goes through search terms in an input file line by line and cleans and
	 * adds each word to a list
	 * 
	 * @param inputFile
	 *            file to parse search terms from
	 * @param exact
	 *            TODO
	 */
	public void parseQuery(String inputFile, boolean exact);

	/**
	 * Writes the search results to a default or custom named JSON file
	 * 
	 * @param outputFile
	 *            name of the JSON file to be written to
	 */
	public void toJSON(String outputFile);
}
