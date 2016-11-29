import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Thread safe version of InvertedIndexBuilder
 */
public class ConcurrentIndexBuilder {

	private static final Logger logger = LogManager.getLogger();

	// TODO Very rare we would want a static work queue, make this a local variable
	private static WorkQueue minions;

	/**
	 * Builds the InvertedIndex splitting up work between a specified amount of
	 * threads by file location
	 * 
	 * @param fileLocations
	 *            ArrayList of file locations
	 * @param index
	 *            thread safe inverted index
	 * @param threads
	 *            number of threads to split up workload with
	 */
	public static void buildIndex(List<String> fileLocations, ConcurrentIndex index, int threads) {

		minions = new WorkQueue(threads);

		for (String filelocation : fileLocations) {
			minions.execute(new IndexMinion(Paths.get(filelocation), index));
		}

		minions.shutdown();
	}

	/**
	 * Creates a minion for an input file location
	 */
	private static class IndexMinion implements Runnable {
		Path filelocation;
		ConcurrentIndex index;

		/**
		 * Minion that only parses words from given filelocation path
		 * 
		 * @param fileLocation
		 *            path to location of text file to parse
		 * @param index
		 *            thread safe inverted index
		 */
		public IndexMinion(Path filelocation, ConcurrentIndex index) {
			logger.debug("Minion created for {}", filelocation.toString());
			this.filelocation = filelocation;
			this.index = index;
		}

		@Override
		public void run() {
			InvertedIndexBuilder.parseWordsDir(this.filelocation, index);
			
			/*
			 * TODO
			 * InvertedIndex local = new InvertedIndex();
			 * InvertedIndexBuilder.parseWordsDir(this.filelocation, local);
			 * index.addAll(local);
			 */
		}

	}
}
