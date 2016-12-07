
public interface SearcherInterface {

	/**
	 * Goes through search terms in an input file line by line and cleans and
	 * adds each word to a list
	 * 
	 * @param inputFile
	 *            file to parse search terms from
	 */
	public void parseQuery(String inputFile, boolean exact);

	/**
	 * This method writes the search results to a default or custom named JSON
	 * file
	 * 
	 * @param outputFile
	 *            name of the JSON file to be written to
	 */
	public void toJSON(String outputFile);
}
