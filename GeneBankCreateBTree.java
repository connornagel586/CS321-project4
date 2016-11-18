import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class GeneBankCreateBTree {

	public static void main(String[] args) {
		Scanner scan;
		int debugLevel, cacheSize, degree, sequenceLength;

		// Check arg length
		try {
			if (args.length < 4 || args.length > 6) {
				printUsage();
				System.exit(0);
			}
			
			
			// Check for chache
			/*if (args[0].equals("0") || args[0].equals("1")) {
			}else{
				printUsage();
				System.exit(0);
			}*/
			
			// Check degree
			if (Integer.parseInt(args[1]) > 0 && !args[1].equals("1")) {
				degree = Integer.parseInt(args[1]); 
			}else{
				printUsage();
				System.exit(0);
			}
			// Check file
			if (args[2].contains(".gbk")) {
				File file = new File(args[2]);
				scan = new Scanner(file);
			} else {
				printUsage();
				System.exit(0);
			}
			// Check sequence length
			if (Integer.parseInt(args[3]) > 1 || Integer.parseInt(args[3]) < 31) {
				sequenceLength = Integer.parseInt(args[3]);
			} else {
				printUsage();
				System.exit(0);
			}
			// check cachSize
			/*if (need to put somthing here) {
				cacheSize = Integer.parseInt(args[4]);
			} else {
				printUsage();
				System.exit(0);
			}*/
			
			// Check fifth input
			if (args.length == 6) {
				if (args[5].equals("0") || args[5].equals("1")) {
					debugLevel = Integer.parseInt(args[5]);
				} else {
					printUsage();
					System.exit(0);
				}
			}

		} catch (NumberFormatException e) {
			printUsage();
		} catch (FileNotFoundException e) {
			printUsage();
		} catch (Exception e) {
			printUsage();
		}
	}

	private static void printUsage() {
		System.err.println("GeneBankCreateBTree <0/1(no/with Cache)> <degree 1<=k<=31> <gbk file> <sequence length>\n"
				+ "[<debug level>]\n");
	}
}
