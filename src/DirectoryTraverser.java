import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * This class traverses a directory and returns a list of all text files found
 * within directory
 */
public class DirectoryTraverser {
	
	public static List<String> traverse(String path){
		ArrayList<String> fileLocations = new ArrayList<>();
		traverse(Paths.get(path), fileLocations);
		return fileLocations;
	}

	/**
	 * Traverses directory recursively and saves the text files
	 * 
	 * @param path
	 *            directory/file location passed through from constructor
	 */
	private static void traverse(Path path, List<String> fileLocations) {

		try (DirectoryStream<Path> listing = Files.newDirectoryStream(path)) {

			for (Path file : listing) {
				// Checks if input is a subdirectory
				if (Files.isDirectory(file)) {
					traverse(file, fileLocations);
					// If path is a .txt file then path is normalized and added
					// to fileLocations ArrayList
				} else if (file.toString().toLowerCase().endsWith(".txt")) {
					fileLocations.add(file.normalize().toString());
				}
			}
		} catch (IOException e) {
//			TODO Print out something nicer
			e.printStackTrace();
		}
	}
}
