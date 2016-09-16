import java.nio.file.Paths;
import java.util.Arrays;

public class Driver {

	public static void main(String[] args) {
		for (int i = 0; i < args.length; i++) {

			if (args[i].equalsIgnoreCase("-index")) {

				if (args[i + 1] == null || !args[i + 1].toLowerCase().endsWith(".json")) {
					System.out.println("default 'index.json' created since no file provided");

				} else if (args[i + 1].toLowerCase().endsWith(".json")) {
					System.out.println(args[i + 1]);
				}

			} else if (args[i].equalsIgnoreCase("-dir")) {
				// check the next args for a directory

				new traverseDirectory(args[i + 1]);
				System.out.println("Path: " + Paths.get(args[i + 1]));
			}
		}

		System.out.println(Arrays.toString(args));
	}

}
