

public class SearchQuery implements Comparable<SearchQuery>{
	
	private String query;
	private String where;
	private int count;
	private int index;
	
	public SearchQuery(String query){
		this.query = query;
	}

	public String getQuery() {
		return query;
	}

	public void setQuery(String query) {
		this.query = query;
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
		return this.query.compareTo(compareQuery.getQuery());
	}
}
