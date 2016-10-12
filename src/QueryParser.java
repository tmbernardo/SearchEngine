import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class QueryParser {
	// TODO parse the query file into lines and lines into either
	// an array or list of sorted cleaned words

	public static ArrayList<String> parseQuery(String inputFile) throws IOException {
		ArrayList<String> queryList = new ArrayList<String>();

		try (BufferedReader reader = Files.newBufferedReader(Paths.get(inputFile), Charset.forName("UTF-8"));) {
			String line = null;

			while ((line = reader.readLine()) != null) {
				String word = line.trim().toLowerCase().replaceAll("\\p{Punct}+", "").replaceAll(" +", " ");
				if (!word.isEmpty()) {
					List<String> newWord = Arrays.asList(word.split(" "));
					Collections.sort(newWord);
					queryList.add(String.join(" ", newWord));
				}
			}
		} catch (Exception e) {
			System.out.println("File error");
		}

		Collections.sort(queryList);
		return queryList;
	}
}
