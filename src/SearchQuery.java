import java.nio.file.Paths;

public class SearchQuery implements Comparable<SearchQuery> {

	private String where;
	private int count;
	private int index;

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

	@Override
	public int compareTo(SearchQuery compareQuery) {
		int result = Integer.compare(this.count, compareQuery.count);

		if (result == 0) {
			result = Integer.compare(compareQuery.getIndex(), this.index);
			if (result == 0) {
				String file1 = Paths.get(this.where).normalize().toString();
				String file2 = Paths.get(compareQuery.getWhere()).normalize().toString();

				return file2.compareToIgnoreCase(file1);
			}
		}

		return result;
	}

	@Override
	public boolean equals(Object compareQuery) {
		if (compareQuery instanceof SearchQuery) {
			return where.equals(((SearchQuery) compareQuery).getWhere());
		}
		return false;
	}
}
