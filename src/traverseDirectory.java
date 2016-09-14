import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

// Create code that is able to traverse a directory and 
// return a list of all the text files found within that directory.
// Store the normalized relative path for file locations as a String object.
// Do not convert the file paths to absolute paths!
// Use the UTF-8 character encoding for all file processing, including reading and writing.

public class traverseDirectory {
	private static Path path;

	public traverseDirectory() {
	}

	public traverseDirectory(String inputPath) {
		path = Paths.get(inputPath);

		try (DirectoryStream<Path> listing = Files.newDirectoryStream(path)) {

			for (Path file : listing) {

				// Check if this is a subdirectory
				if (Files.isDirectory(file)) {

					traverse(file);

				} else if (file.endsWith(".txt")) {
					// Add the file size next to the name
					System.out.printf(file.toString());
				}
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void traverse(Path directory) {

		if (Files.isDirectory(directory)) {
			traverse(directory);
		} else if (directory.endsWith(".txt")) {
			// save the text file location
			System.out.println(directory.toString()); // path not printing
		}
	}
}
