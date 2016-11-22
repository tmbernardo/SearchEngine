import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ConcurrentIndexBuilder {

	private static final Logger logger = LogManager.getLogger();

	private static WorkQueue minions;

	public static void buildIndex(List<String> fileLocations, ConcurrentIndex index, int threads) {

		minions = new WorkQueue(threads);

		for (String filelocation : fileLocations) {
			minions.execute(new IndexMinion(Paths.get(filelocation), index));
		}

		minions.shutdown();
	}

	private static class IndexMinion implements Runnable {
		Path filelocation;
		ConcurrentIndex index;

		public IndexMinion(Path filelocation, ConcurrentIndex index) {
			logger.debug("Minion created for {}", filelocation.toString());
			this.filelocation = filelocation;
			this.index = index;
		}

		@Override
		public void run() {
			InvertedIndexBuilder.parseWordsDir(this.filelocation, index);
		}

	}
}
