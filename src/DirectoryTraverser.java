import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

/**
 * This class traverses a directory and returns a list of all text files found
 * within directory
 */
public class DirectoryTraverser {

	// TODO Always initialize in the constructor
	// TODO Actually, remove this entirely!
	private ArrayList<String> fileLocations = new ArrayList<>();

	public DirectoryTraverser() {
	}

	/**
	 * Constructor: requires input directory/file
	 * 
	 * @param inputPath
	 *            directory/file location
	 */
	public DirectoryTraverser(String inputPath) {
		Path path = Paths.get(inputPath);
		traverse(path);
	}

	/**
	 * Traverses directory recursively and saves the text files
	 * 
	 * @param path
	 *            directory/file location passed through from constructor
	 */
	private void traverse(Path path) {

		try (DirectoryStream<Path> listing = Files.newDirectoryStream(path)) {

			for (Path file : listing) {
				// Checks if input is a subdirectory
				if (Files.isDirectory(file)) {
					traverse(file);
					// If path is a .txt file then path is normalized and added
					// to fileLocations ArrayList
				} else if (file.toString().toLowerCase().endsWith(".txt")) {
					fileLocations.add(file.normalize().toString());
				}
			}

		} catch (IOException e) {
			// TODO catch exception properly
			e.printStackTrace();
		}
	}

	/**
	 * Returns the ArrayList of file locations
	 */
	public ArrayList<String> getFileLocations() {
		return fileLocations;
	}
	
	/* TODO
	public static List<String> traverse(Path path) {
		ArrayList<String> paths = new ArrayList<>();
		traverse(path, paths);
		return paths;
	}
	
	
	private static void traverse(Path path, List<String> paths) {
		if (directory)
			traverse(subdirectory, paths)
		else if text file
			add to paths
	}
	*/

}
