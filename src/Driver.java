import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;

public class Driver {

	public static void main(String[] args) throws IOException {
		traverseDirectory directory;
		wordIndex words = null;
		ArrayList<String> fileLocations = null;
		String jsonFileName = null;
		
		for (int i = 0; i < args.length; i++) {

			if (args[i].equalsIgnoreCase("-index")) {

				if (i+1 >= args.length) {
					jsonFileName = "index.json";

				} else if (args[i + 1].toLowerCase().contains(".json")) {
					jsonFileName = args[i+1];
				}

			} else if (args[i].equalsIgnoreCase("-dir") && i + 1 < args.length) {
				// check the next args for a directory
				
				directory = new traverseDirectory(args[i + 1]);
				fileLocations = directory.getFileLocations();
				words = new wordIndex(fileLocations);
			}
		}
		// handle the lack of -index flag so that it doesn't create a JSON file
		
		if (jsonFileName != null){
			new JSONFileWriter(words.getWordIndex(), Paths.get(jsonFileName));
		}
		
	}

}
