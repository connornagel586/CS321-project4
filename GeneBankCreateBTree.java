import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;

public class GeneBankCreateBTree {

	public static void main(String[] args) {
		Scanner scan = null;
		int debugLevel = -1, cacheSize, degree = -1, sequenceLength = 0;
		boolean useCache = false;
		boolean useDebug = false;
		String nextLine = "";
		String line = "";
		String sequence2 = "";
		String sequence1 = "";
		String startflag = "ORIGIN";
		String endflag = "//";
		String DELIMITER = "[actgn/]*";

		// Check arg length
		try {
			if (args.length < 4 || args.length > 6) {
				printUsage();
				System.exit(0);
			}

			// Check for chache
			if (args[0].equals("0") || args[0].equals("1")) {
				if (args[0].equals("0")) {
					useCache = false;
				} else {
					useCache = true;
				}
			} else {
				printUsage();
				System.exit(0);
			}

			if (Integer.parseInt(args[1]) >= 0) {
				if (degree == 0) {
					degree = 127;
				} else {
					degree = Integer.parseInt(args[1]);
				}
			} else {
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

			// Check Debug and Cache Size
			if (args.length == 6 && useCache == false) {
				printUsage();
				System.exit(0);
			} else if (args.length == 6 && useCache == true) {
				cacheSize = Integer.parseInt(args[4]);
				debugLevel = Integer.parseInt(args[5]);
				useDebug = true;
				if (debugLevel > 1 || debugLevel < 0) {
					printUsage();
					System.exit(0);
				}
			} else if (args.length == 5 && useCache == true) {
				cacheSize = Integer.parseInt(args[4]);
			} else if (args.length == 5 && useCache == false) {
				debugLevel = Integer.parseInt(args[4]);
				useDebug = true;
				if (debugLevel > 1 || debugLevel < 0) {
					printUsage();
					System.exit(0);
				}
			}

			BTree tree = new BTree(degree, sequenceLength, null);

			while (!nextLine.contains(endflag)) {

				while (!nextLine.contains(startflag)) {
					nextLine = scan.nextLine();
				}

				scan.useDelimiter(DELIMITER);

				int start = 0;
				line = nextLine;
				nextLine = scan.nextLine().toLowerCase();

				// Read sequence.
				while (start < line.length()) {

					int end = start + sequenceLength;

					if (end < line.length()) {

						for (int i = start; i < end; i++) {
							sequence2 += line.charAt(i);
						}

					} else {

						end = sequenceLength - (nextLine.length() - start);

						for (int i = start; i < line.length(); i++) {
							sequence2 += line.charAt(i);
						}

						for (int i = 0; i < end; i++) {
							sequence2 += nextLine.charAt(i);
						}
					}

					// Converts to binary.
					if (sequence2.length() == sequenceLength) {
						for (int i = 0; i < sequence2.length(); i++) {
							char c = sequence2.charAt(i);

							switch (c) {
							case ('a'):
								sequence1 += "00";
							case ('c'):
								sequence1 += "01";
							case ('g'):
								sequence1 += "10";
							case ('t'):
								sequence1 += "11";
							case ('n'):
								sequence1 = "";
							}
						}

						// Add the binary sequence to the tree.
						if (sequence1.length() == sequenceLength * 2) {
							TreeObject o = new TreeObject(Long.parseLong(sequence1));
							tree.InsertNode(o);
						}
					}

					start++;
					sequence2 = "";
					sequence1 = "";
				}
			}

			scan.close();

			if (useDebug) {
				if (debugLevel == 0) {
					// do some stuff
				}
				if (debugLevel == 1) {
					// do some stuff
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
		System.err.println("GeneBankCreateBTree <0/1(no/with Cache)> <degree 1<=k<=31> <gbk file> "
				+ "<sequence length>" + "[<cache size>] [<debug level>]" + "[<debug level>]\n");
	}
}
