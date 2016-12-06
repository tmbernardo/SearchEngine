import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Thread safe version of InvertedIndex
 */
public class ConcurrentIndex extends InvertedIndex {

	private static final Logger logger = LogManager.getLogger();

	// TODO final
	private ReadWriteLock lock;

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
		// TODO lock for read
		lock.lockReadWrite();
		try {
			return super.exactSearch(queries);
		} finally {
			lock.unlockReadWrite();
		}
	}

	@Override
	public List<SearchQuery> partialSearch(String[] queries) {
		// TODO lock for read
		lock.lockReadWrite();
		try {
			return super.partialSearch(queries);
		} finally {
			lock.unlockReadWrite();
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

	// TODO Use try/finally just in case there are any runtime exceptions
	
	@Override
	public void addAll(InvertedIndex local) {
		lock.lockReadWrite();
		super.addAll(local);
		lock.unlockReadWrite();
	}

	@Override
	public void toJSON(String outputFile) {
		// TODO read, try/finally
		lock.lockReadWrite();
		logger.debug("Writing to {}", outputFile);
		logger.debug("size of words: {}", super.getIndexSize());
		super.toJSON(outputFile);
		lock.unlockReadWrite();
	}

	@Override
	public int getIndexSize() {
		// TODO read only
		lock.lockReadWrite();
		try {
			return super.getIndexSize();
		} finally {
			lock.unlockReadWrite();
		}
	}
}
