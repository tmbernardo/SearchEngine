import java.util.Arrays;

public class Driver {

	public static void main(String[] args) {
		// TODO Create code that handles parsing command-line arguments,
		// so that you can easily retrieve the directory containing the text
		// files to parse.
		// Process command-line parameters to determine the directory to parse
		// and output to produce.
		// See the Execution section below for specifics.
		// default file should be 'index.json'

		for (int i = 0; i < args.length; i++) {

			if (args[i].equalsIgnoreCase("-index")) {
				if (args[i + 1] == null || !args[i + 1].toLowerCase().contains(".json")) {
					System.out.println("default index.json created since no file provided");
				} else {
					System.out.println(args[i + 1]);
				}
				// maybe this is enough? i can technically still create the
				// index if I construct it after
			} else if (args[i].equalsIgnoreCase("-dir")) {
				// check the next directory in args
			}
		}

		System.out.println(Arrays.toString(args));
	}

}
