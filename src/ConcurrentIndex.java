import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Thread safe version of InvertedIndex
 */
public class ConcurrentIndex extends InvertedIndex {

	private static final Logger logger = LogManager.getLogger();

	private final ReadWriteLock lock;

	/**
	 * Default Constructor
	 * 
	 * Instantiates words and SearchQueries from InvertedIndex as well as
	 * ReadWriteLock
	 */
	public ConcurrentIndex() {
		super();
		this.lock = new ReadWriteLock();
	}

	@Override
	public List<SearchQuery> exactSearch(String[] queries) {
		lock.lockReadOnly();
		;
		try {
			return super.exactSearch(queries);
		} finally {
			lock.unlockReadOnly();
		}
	}

	@Override
	public List<SearchQuery> partialSearch(String[] queries) {
		lock.lockReadOnly();
		try {
			return super.partialSearch(queries);
		} finally {
			lock.unlockReadOnly();
		}
	}

	@Override
	public void add(String word, int lineNumber, String fileName) {
		lock.lockReadWrite();
		try {
			super.add(word, lineNumber, fileName);
		} finally {
			lock.unlockReadWrite();
		}
	}

	@Override
	public void addAll(InvertedIndex local) {
		lock.lockReadWrite();
		try {
			super.addAll(local);
		} finally {
			lock.unlockReadWrite();
		}
	}

	@Override
	public void toJSON(String outputFile) {
		lock.lockReadWrite();
		try {
			logger.debug("Writing to {}", outputFile);
			logger.debug("size of words: {}", super.getIndexSize());
			super.toJSON(outputFile);
		} finally {
			lock.unlockReadWrite();
		}
	}

	@Override
	public int getIndexSize() {
		lock.lockReadOnly();
		try {
			return super.getIndexSize();
		} finally {
			lock.unlockReadOnly();
		}
	}
}
