import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

// Create code that is able to traverse a directory and 
// return a list of all the text files found within that directory.
// Store the normalized relative path for file locations as a String object.
// Do not convert the file paths to absolute paths!
// Use the UTF-8 character encoding for all file processing, including reading and writing.

public class traverseDirectory {
	
	private ArrayList<String> fileLocations = new ArrayList<>();

	public traverseDirectory() {}

	public traverseDirectory(String inputPath) {
		
		Path path = Paths.get(inputPath);
		
		if (Files.isDirectory(path)) {
			traverse(path);
			
		} else if (path.toString().endsWith(".txt")) {
			// save the text file location
			System.out.println(path);
		}
	}

	private void traverse(Path path) {

		try (DirectoryStream<Path> listing = Files.newDirectoryStream(path)) {

			for (Path file : listing) {
				// Check if this is a subdirectory
				if (Files.isDirectory(file)) {
					traverse(file);

				} else if (file.toString().toLowerCase().endsWith(".txt")) {
					// Add the file size next to the name
					fileLocations.add(file.normalize().toString());
				}
			}

		} catch (IOException e) {
			// TODO catch exception properly
			e.printStackTrace();
		}
	}
	
	public ArrayList<String> getFileLocations(){ return fileLocations; }
	
	
}
