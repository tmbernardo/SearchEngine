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
	private String where;
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

	public String getWhere() {
		return where;
	}

	public void setWhere(String where) {
		this.where = where;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public void updateCount(int count) {
		this.count += count;
	}

	public void updateIndex(int index) {
		if (this.index > index) {
			this.index = index;
		}
	}

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
		// tried making both - but still didn't come out correct

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
		return where.equals(((SearchQuery) compareQuery).getWhere());
	}
}
