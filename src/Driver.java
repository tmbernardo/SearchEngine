import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;

public class Driver {

	public static void main(String[] args) throws IOException {
		String dir = "-dir", index = "-index", jsonFileName = "index.json";
		
		traverseDirectory directory;
		ArrayList<String> fileLocations = null;
		wordIndex words = null;
		
		ArgumentParser parser = new ArgumentParser();
        parser.parseArguments(args);
        
        if(parser.hasFlag(dir) && parser.hasValue(dir)){
        	directory = new traverseDirectory(parser.getValue(dir));
    		fileLocations = directory.getFileLocations();
    		words = new wordIndex(fileLocations);
        }
		
		if(parser.hasFlag(index)){
			jsonFileName = parser.getValue(index, jsonFileName);
			new JSONFileWriter(words.getWordIndex(), Paths.get(jsonFileName));
		}
	}

}
