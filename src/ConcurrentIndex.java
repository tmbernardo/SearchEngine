import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

// TODO convert to custom lock
public class ConcurrentIndex extends InvertedIndex {

	private static final Logger logger = LogManager.getLogger();

	private ReadWriteLock lock;

	public ConcurrentIndex() {
		super();
		this.lock = new ReadWriteLock();
	}

	@Override
	public List<SearchQuery> exactSearch(String[] queries) {
		return super.exactSearch(queries);
	}

	@Override
	public List<SearchQuery> partialSearch(String[] queries) {
		return super.partialSearch(queries);
	}

	@Override
	public void addResults(String word, List<SearchQuery> results, Map<String, SearchQuery> resultmap) {
		for (String location : this.wordsCopy().get(word).keySet()) {
			int count = this.wordsCopy().get(word).get(location).size();
			int index = this.wordsCopy().get(word).get(location).first();

			lock.lockReadWrite();
			if (resultmap.containsKey(location)) {
				resultmap.get(location).update(count, index);
			} else {
				SearchQuery newquery = new SearchQuery(location);
				newquery.setCount(count);
				newquery.setIndex(index);
				resultmap.put(location, newquery);
				results.add(newquery);
			}
			lock.unlockReadWrite();
		}
	}

	@Override
	public TreeMap<String, TreeMap<String, TreeSet<Integer>>> wordsCopy() {
		return super.wordsCopy();
	}

	@Override
	public void add(String word, int lineNumber, String fileName) {
		lock.lockReadWrite();
		super.add(word, lineNumber, fileName);
		lock.unlockReadWrite();
	}

	@Override
	public void toJSON(String outputFile) {
		logger.debug("Writing to {}", outputFile);
		logger.debug("size of words: {}", this.wordsCopy().size());
		super.toJSON(outputFile);
	}
}
