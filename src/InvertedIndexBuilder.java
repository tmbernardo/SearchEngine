import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
public class InvertedIndexBuilder {
	
	public static void parseWords(Path inputFile, InvertedIndex index){
		int lineNumber = 0;

		try (BufferedReader reader = Files.newBufferedReader(inputFile, Charset.forName("UTF-8"));) {
			String line = null;

			while ((line = reader.readLine()) != null) {
				for (String word : line.trim().replaceAll("\\p{Punct}+", "").split(" +")) {
					if (!word.isEmpty()) {
						lineNumber++;
						index.add(word.trim().toLowerCase(), lineNumber, inputFile.toString());
					}
				}
			}

		} catch (IOException e) {
//			TODO Print out something nicer
			e.printStackTrace();
		}
	}
	

}
