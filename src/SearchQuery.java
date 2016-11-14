import java.nio.file.Paths;

/**
 * Creates a SearchQuery object that saves data taken from a text file to its
 * members
 */
public class SearchQuery implements Comparable<SearchQuery> {
	/**
	 * (where) stores the location of the file, (count) stores the occurrences
	 * of the word in the file, (index) stores the first instance of the word in
	 * the file
	 */
	private final String where;
	private int count;
	private int index;

	/**
	 * Constructor that requires the location of the file as a string
	 * 
	 * @param where
	 *            location of the file
	 */
	public SearchQuery(String where) {
		this.where = where;
	}

	/**
	 * @return location of the search query as a string
	 */
	public String getWhere() {
		return where;
	}

	/**
	 * @return number of times that the word occurs in a particular location
	 */
	public int getCount() {
		return count;
	}

	/**
	 * sets the number of word occurrences in a particular location
	 * 
	 * @param length
	 *            of hashmap containing integers
	 */
	public void setCount(int count) {
		this.count = count;
	}

	/**
	 * @return earliest location of word
	 */
	public int getIndex() {
		return index;
	}

	/**
	 * sets the first location of the search query
	 * 
	 * @param earliest
	 *            location of word
	 */
	public void setIndex(int index) {
		this.index = index;
	}

	/**
	 * adds on to the current count if it appears again
	 * 
	 * @param count
	 *            number of occurrences in a certain location
	 */
	public void updateCount(int count) {
		this.count += count;
	}

	/**
	 * sets the input index as the first occurrence if it is less than the
	 * current
	 * 
	 * @param index
	 *            first occurrence of a word
	 */
	public void updateIndex(int index) {
		if (this.index > index) {
			this.index = index;
		}
	}

	/**
	 * updates both count and index if applicable
	 * 
	 * @param count
	 *            number of occurrences of the word in a location
	 * @param index
	 *            first location of word
	 */
	public void update(int count, int index) {
		this.updateCount(count);
		this.updateIndex(index);
	}

	/**
	 * Ranks the data from this file to another using the following criteria:
	 * 
	 * count (frequency) > index (position) > where (filename)
	 * 
	 * @return <0 if this object is ranked lower than passed, 0 if this object
	 *         is the same as object passed and >0 if this object is ranked
	 *         higher than the object passed
	 */
	@Override
	public int compareTo(SearchQuery compareQuery) {

		int result = Integer.compare(this.count, compareQuery.count);

		if (result == 0) {
			result = Integer.compare(compareQuery.getIndex(), this.index);

			if (result == 0) {
				String file1 = Paths.get(this.where).normalize().toString();
				String file2 = Paths.get(compareQuery.getWhere()).normalize().toString();
				return -file2.compareTo(file1);
			}
		}
		return -result;
	}

	/**
	 * Checks whether this file location is the same as the passed in object's
	 * file location
	 * 
	 * @return true if locations are the same false if not
	 */
	@Override
	public boolean equals(Object compareQuery) {
		return this == compareQuery;
	}
}
