import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Thread safe version of InvertedIndexBuilder
 */
public class ConcurrentIndexBuilder implements IndexBuilderInterface {

	private final Logger logger = LogManager.getLogger();
	private final ConcurrentIndex index;
	private final WorkQueue minions;

	/**
	 * Sets the index for use within the class
	 * 
	 * @param InvertedIndex
	 *            object to save word data to
	 * @param minions
	 *            WorkQueue object containing helper threads
	 */
	public ConcurrentIndexBuilder(ConcurrentIndex index, WorkQueue minions) {
		this.index = index;
		this.minions = minions;
	}

	@Override
	public void buildIndex(List<String> fileLocations) {

		for (String filelocation : fileLocations) {
			minions.execute(new IndexMinion(Paths.get(filelocation), index));
		}
		minions.finish();
	}

	/**
	 * Creates a minion for an input file location
	 */
	private class IndexMinion implements Runnable {
		Path filelocation;
		InvertedIndex index;

		/**
		 * Minion that only parses words from given filelocation path
		 * 
		 * @param fileLocation
		 *            path to location of text file to parse
		 * @param index
		 *            thread safe inverted index
		 */
		public IndexMinion(Path filelocation, InvertedIndex index) {
			logger.debug("Minion created for {}", filelocation.toString());
			this.filelocation = filelocation;
			this.index = index;
		}

		@Override
		public void run() {
			InvertedIndex local = new InvertedIndex();
			IndexBuilderInterface.parseWordsDir(filelocation, local);
			index.addAll(local);
		}

	}
}
