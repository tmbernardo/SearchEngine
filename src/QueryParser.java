import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class QueryParser {
//	TODO parse the query file into lines and lines into either 
//	an array or list of sorted cleaned words
	
	public static List<SearchQuery> parseQuery(Path inputFile) throws IOException{
		List<SearchQuery> queryList = new ArrayList<SearchQuery>();
		
		try (BufferedReader reader = Files.newBufferedReader(inputFile, Charset.forName("UTF-8"));) {
			String line = null;

			while ((line = reader.readLine()) != null) {
				String word = line.trim().replaceAll("\\p{Punct}+", "").replaceAll(" +", " ");
				if (!word.isEmpty()) {
					queryList.add(new SearchQuery(word));
				}
			}
			
			Collections.sort(queryList);
			return queryList;
		}
	}
}
