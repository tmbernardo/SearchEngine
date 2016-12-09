import java.nio.file.Paths;
import java.util.List;

/**
 * Builds an inverted index from the files passed through to parseWords
 */
public class InvertedIndexBuilder implements IndexBuilderInterface {
	private final InvertedIndex index;

	/**
	 * Sets the index for use within the class
	 * 
	 * @param InvertedIndex
	 *            object to save word data to
	 */
	public InvertedIndexBuilder(InvertedIndex index) {
		this.index = index;
	}

	@Override
	public void buildIndex(List<String> fileLocations) {
		for (String string : fileLocations) {
			IndexBuilderInterface.parseWordsDir(Paths.get(string), index);
		}
	}
}
