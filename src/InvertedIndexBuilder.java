import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Builds an inverted index from the files passed through to parseWords
 */
public class InvertedIndexBuilder {
	/**
	 * Goes through all words in each sub-directory/file passed and adds it to
	 * the inverted index passed
	 * 
	 * @param inputFile
	 *            location of the directory to be parsed
	 * @param index
	 *            inverted index to add words to
	 * 
	 */
	public static void parseWordsDir(Path inputFile, InvertedIndex index) {
		int lineNumber = 0;

		try (BufferedReader reader = Files.newBufferedReader(inputFile, Charset.forName("UTF-8"));) {
			String line = null;
			// TODO String path = inputFile.toString(); outside the while loop

			while ((line = reader.readLine()) != null) {
				for (String word : line.trim().replaceAll("\\p{Punct}+", "").split("\\s+")) {
					if (!word.isEmpty()) {
						lineNumber++;
						index.add(word.trim().toLowerCase(), lineNumber, inputFile.toString());
					}
				}
			}

		} catch (IOException e) {
			System.out.println("InvertedIndexBuilder: File is invalid!");
		}
	}

	// TODO parseWords from URLS
	
	public static void parseWordsUrl(String url, InvertedIndex index){
		String html = HTMLCleaner.fetchHTML(url);
		html = HTMLCleaner.cleanHTML(html);
		
		int lineNumber = 0;
		
		for (String lines : html.split("\n+")) {
			
			System.out.println(lines);
			
			if (!lines.trim().isEmpty()){
				lineNumber++;
				
				for (String word : lines.trim().replaceAll("\\p{Punct}+", "").split("\\s+")) {
					
					word = word.trim().toLowerCase();
					
					if(!word.isEmpty()){
						index.add(word, lineNumber, url);
					}
				
				}
			}
		}
	}

}
