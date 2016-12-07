import java.util.List;

public interface IndexBuilderInterface {

	/**
	 * Takes in an ArrayList containing the file locations then saves all words
	 * found within the file to the InvertedIndex
	 * 
	 * @param fileLocations
	 *            ArrayList of file locations
	 */
	public void buildIndex(List<String> fileLocations);
}
