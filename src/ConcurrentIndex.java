import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

// TODO convert to custom lock
public class ConcurrentIndex extends InvertedIndex {

	private static final Logger logger = LogManager.getLogger();

	// private ReadWriteLock lock;

	public ConcurrentIndex() {
		super();
		// this.lock = new ReadWriteLock();
	}

	@Override
	public synchronized List<SearchQuery> exactSearch(String[] queries) {
		return super.exactSearch(queries);
	}

	@Override
	public synchronized List<SearchQuery> partialSearch(String[] queries) {
		return super.partialSearch(queries);
	}

	@Override
	public synchronized void addResults(String word, List<SearchQuery> results, Map<String, SearchQuery> resultmap) {
		super.addResults(word, results, resultmap);
	}

	@Override
	public synchronized TreeMap<String, TreeMap<String, TreeSet<Integer>>> wordsCopy() {
		return super.wordsCopy();
	}

	@Override
	public synchronized void add(String word, int lineNumber, String fileName) {
		// logger.debug("ConcurrentIndex: Adding {}", word);
		super.add(word, lineNumber, fileName);
	}

	@Override
	public synchronized void toJSON(String outputFile) {
		logger.debug("Writing to {}", outputFile);
		logger.debug("size of words: {}", this.wordsCopy().size());
		super.toJSON(outputFile);
	}
}
