import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * Takes objects from a TreeMap and outputs them in a "pretty" JSON format.
 */
public class JSONFileWriter {
	/** Tab character used for pretty JSON output. */
	public static final char TAB = '\t';

	/** End of line character used for pretty JSON output. */
	public static final char END = '\n';

	/**
	 * Returns a quoted version of the provided text.
	 * 
	 * @param text
	 * @return "text" in quotes
	 */
	public static String quote(String text) {
		return String.format("\"%s\"", text);
	}

	/**
	 * Returns n tab characters.
	 * 
	 * @param n
	 *            number of tab characters
	 * @return n tab characters concatenated
	 */
	public static String tab(int n) {
		char[] tabs = new char[n];
		Arrays.fill(tabs, TAB);
		return String.valueOf(tabs);
	}

	/**
	 * Writes an Inverted Index to a "pretty" JSON format
	 * 
	 * @param outputFile
	 *            name/path to write JSON file to
	 * @param words
	 *            TreeMap containing inverted index
	 */
	public static void IndexToJSON(Path outputFile, TreeMap<String, TreeMap<String, TreeSet<Integer>>> words) {
		try (BufferedWriter writer = writerCreator(outputFile)) {

			writer.write("{" + END);

			for (String str : words.keySet()) {
				writer.write(TAB + quote(str) + ": ");
				writeFileName(words.get(str), writer, 2);

				if (str.compareTo(words.lastKey()) != 0) {
					writer.write("," + END);
				} else {
					writer.write(END);
				}
			}
			writer.write("}" + END);
			writer.flush();

		} catch (Exception e) {
			System.err.println("IndexToJson: File cannot be written!");
		}
	}

	/**
	 * Writes given search results to a "pretty" JSON format
	 * 
	 * @param outputFile
	 *            name/path to write JSON file to
	 * @param words
	 *            TreeMap containing results of search
	 */
	public static void SearchResultsToJSON(Path outputFile, TreeMap<String, ArrayList<SearchQuery>> SearchQueries) {
		// TODO Files.newBufferedWriter(outputFile, Charset.forName("UTF8"));
		try (BufferedWriter writer = writerCreator(outputFile)) {

			writer.write("{" + END);

			for (String query : SearchQueries.keySet()) {

				writer.write(TAB + quote(query) + ": ");

				writeAttributes(SearchQueries.get(query), writer, 2);

				if (query != SearchQueries.lastKey()) {
					writer.write("," + END);
				} else {
					writer.write(END);
				}
			}
			writer.write("}" + END);
			writer.flush();

		} catch (Exception e) {
			System.err.println("SearchResultsToJSON: File cannot be written to!");
		}
	}

	/**
	 * Writes the files and data of a search term
	 * 
	 * @param query
	 *            List of SearchQuery objects where a certain search term can be
	 *            found
	 * @param writer
	 *            writer used by SearchResultsToJSON
	 * @param tabN
	 *            number of tabs to shift the name to
	 * @throws IOException
	 */
	private static void writeAttributes(List<SearchQuery> query, BufferedWriter writer, int tabN) throws IOException {
		writer.write("[");

		for (SearchQuery searchQuery : query) {
			writer.write(END + tab(tabN) + "{" + END);
			writer.write(tab(tabN + 1) + quote("where") + ": " + quote(searchQuery.getWhere()));
			writer.write("," + END);
			writer.write(tab(tabN + 1) + quote("count") + ": " + searchQuery.getCount());
			writer.write("," + END);
			writer.write(tab(tabN + 1) + quote("index") + ": " + searchQuery.getIndex());
			writer.write(END);
			writer.write(tab(tabN) + "}");

			if (!searchQuery.equals(query.get(query.size() - 1))) {
				writer.write(",");
			}
		}
		writer.write("\n" + TAB + "]");
	}

	/**
	 * Writes the file name/location of a word
	 * 
	 * @param fileNames
	 *            TreeMap of file names where a certain word is found
	 * @param writer
	 *            writer used by IndexToJSON
	 * @param tabN
	 *            number of tabs to shift the name to
	 * @throws IOException
	 */
	private static void writeFileName(TreeMap<String, TreeSet<Integer>> fileNames, BufferedWriter writer, int tabN)
			throws IOException {
		writer.write("{" + END);

		for (String str : fileNames.keySet()) {
			writer.write(tab(tabN) + quote(str) + ": ");
			writeIntSet(fileNames.get(str), writer, 3);

			if (str.compareTo(fileNames.lastKey()) != 0) {
				writer.write("," + END);
			} else {
				writer.write(END);
			}
		}
		writer.write(tab(tabN - 1) + "}");
	}

	/**
	 * Writes the line numbers that a certain word appears in a certain file
	 * 
	 * @param elements
	 *            TreeSet of line numbers where the word appears in a file
	 * @param writer
	 *            writer used by IndexToJSON
	 * @param tabN
	 *            number of tabs to shift the name to
	 * @throws IOException
	 */
	private static void writeIntSet(TreeSet<Integer> elements, BufferedWriter writer, int tabN) throws IOException {

		writer.write("[" + END);
		for (Integer integer : elements) {
			writer.write(tab(tabN) + integer.toString());

			if (integer != elements.last()) {
				writer.write("," + END);
			} else {
				writer.write(END);
			}
		}
		writer.write(tab(tabN - 1) + "]");
	}

	/**
	 * Writes the line numbers that a certain word appears in a certain file
	 * 
	 * @param path
	 *            file to write to
	 * @throws FileNotFoundException
	 * @throws UnsupportedEncodingException
	 */
	public static BufferedWriter writerCreator(Path path) throws UnsupportedEncodingException, FileNotFoundException {

		BufferedWriter writer = new BufferedWriter(
				new OutputStreamWriter(new FileOutputStream(path.toFile()), "UTF-8"));
		return writer;
	}
}
